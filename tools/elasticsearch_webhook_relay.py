#!/usr/bin/env python3

import argparse
import base64
import json
import os
import re
import sys
import time
from datetime import datetime, timedelta, timezone
from pathlib import Path
from urllib import error, request

INGESTION_OVERLAP_SECONDS = 60


def load_dotenv(dotenv_path: Path):
    if not dotenv_path.is_file():
        return

    for line_number, line in enumerate(dotenv_path.read_text(encoding="utf-8").splitlines(), start=1):
        entry = line.strip()
        if not entry or entry.startswith("#"):
            continue
        if entry.startswith("export "):
            entry = entry[len("export "):].lstrip()

        key, separator, value = entry.partition("=")
        key = key.strip()
        if not separator or not re.fullmatch(r"[A-Za-z_][A-Za-z0-9_]*", key):
            raise ValueError(f"Invalid .env entry at {dotenv_path}:{line_number}")

        value = value.strip()
        if value.startswith(("'", '"')):
            quote = value[0]
            if len(value) < 2 or value[-1] != quote:
                raise ValueError(f"Unclosed quoted value in .env at {dotenv_path}:{line_number}")
            value = value[1:-1]
        else:
            value = value.split(" #", maxsplit=1)[0].rstrip()

        os.environ.setdefault(key, value)


def http_json_request(url: str, method: str = "GET", payload: dict | None = None, username: str | None = None, password: str | None = None):
    data = None if payload is None else json.dumps(payload).encode("utf-8")
    headers = {"Accept": "application/json", "Content-Type": "application/json"}
    if username and password:
        token = base64.b64encode(f"{username}:{password}".encode("utf-8")).decode("ascii")
        headers["Authorization"] = f"Basic {token}"

    req = request.Request(url, data=data, headers=headers, method=method)
    with request.urlopen(req, timeout=30) as response:
        return json.loads(response.read().decode("utf-8"))


def format_timestamp(value: datetime):
    return value.isoformat(timespec="microseconds").replace("+00:00", "Z")


def query_error_events(
    es_url: str,
    index_pattern: str,
    start_time: datetime,
    end_time: datetime,
    size: int,
    username: str | None,
    password: str | None,
):
    if start_time >= end_time:
        return []

    query = {
        "size": size,
        "sort": [{"@timestamp": {"order": "desc"}}],
        "query": {
            "bool": {
                "filter": [{
                    "range": {
                        "@timestamp": {
                            "gte": format_timestamp(start_time),
                            "lt": format_timestamp(end_time),
                        }
                    }
                }],
                "should": [
                    {"terms": {"log.level": ["WARN", "ERROR"]}},
                    {"term": {"event.outcome": "failure"}},
                    {"exists": {"field": "error.type"}},
                    {"exists": {"field": "error.message"}}
                ],
                "minimum_should_match": 1,
            }
        },
    }

    url = f"{es_url.rstrip('/')}/{index_pattern}/_search"
    response = http_json_request(url, method="POST", payload=query, username=username, password=password)
    hits = response.get("hits", {}).get("hits", [])
    return [(item["_id"], item.get("_source", {})) for item in hits]


def dispatch_to_github_workflow(repo: str, token: str, workflow: str, ref: str, payload: dict, api_url: str):
    if not repo or "/" not in repo:
        raise ValueError("repo must be in the form OWNER/REPO")
    if not token:
        raise ValueError("GITHUB_TOKEN is required to dispatch the workflow")
    if not workflow:
        raise ValueError("workflow is required to dispatch the workflow")

    url = f"{api_url.rstrip('/')}/repos/{repo}/actions/workflows/{workflow}/dispatches"
    request_body = {
        "ref": ref,
        "inputs": {"event_payload": json.dumps(payload, separators=(",", ":"))},
    }
    headers = {
        "Accept": "application/vnd.github+json",
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json",
        "X-GitHub-Api-Version": "2022-11-28",
        "User-Agent": "kibana-webhook-relay/1.0",
    }

    req = request.Request(url, data=json.dumps(request_body).encode("utf-8"), headers=headers, method="POST")
    try:
        with request.urlopen(req, timeout=30) as response:
            print(f"workflow_dispatch accepted (HTTP {response.status}).")
            return True
    except error.HTTPError as exc:
        body = exc.read().decode("utf-8", errors="replace")
        print(f"GitHub rejected workflow_dispatch: {exc.code} - {body}", file=sys.stderr)
        return False


def relay_once(args, start_time: datetime, end_time: datetime, seen_event_ids: dict[str, datetime]):
    hits = query_error_events(
        es_url=args.es_url,
        index_pattern=args.index_pattern,
        start_time=start_time,
        end_time=end_time,
        size=args.size,
        username=args.es_username,
        password=args.es_password,
    )
    new_hits = [(event_id, event) for event_id, event in hits if event_id not in seen_event_ids]
    events = [event for _, event in new_hits]
    if not events:
        print(
            "No new Elasticsearch error events found from "
            f"{format_timestamp(start_time)} through {format_timestamp(end_time)}."
        )
        return 0

    payload = {
        "source": "elasticsearch",
        "index_pattern": args.index_pattern,
        "window_start": format_timestamp(start_time),
        "window_end": format_timestamp(end_time),
        "event_count": len(events),
        "events": events,
    }
    dispatched = dispatch_to_github_workflow(
        repo=args.repo,
        token=args.token,
        workflow=args.workflow,
        ref=args.ref,
        payload=payload,
        api_url=args.github_api_url,
    )
    if dispatched:
        for event_id, event in new_hits:
            timestamp = event.get("@timestamp")
            if timestamp:
                event_time = datetime.fromisoformat(timestamp.replace("Z", "+00:00"))
            else:
                event_time = end_time
            seen_event_ids[event_id] = event_time
        return 0
    return 1


def run_polling(args):
    relay_started_at = datetime.now(timezone.utc)
    last_successful_poll = relay_started_at
    seen_event_ids: dict[str, datetime] = {}
    next_poll = time.monotonic()

    while True:
        if args.interval > 0:
            next_poll += args.interval
            time.sleep(max(0, next_poll - time.monotonic()))

        poll_ended_at = datetime.now(timezone.utc)
        query_start = max(
            relay_started_at,
            last_successful_poll - timedelta(seconds=INGESTION_OVERLAP_SECONDS),
        )
        for event_id, event_time in list(seen_event_ids.items()):
            if event_time < query_start:
                del seen_event_ids[event_id]

        try:
            exit_code = relay_once(args, query_start, poll_ended_at, seen_event_ids)
        except Exception as exc:
            print(f"Relay poll failed; it will retry without advancing its time window: {exc}", file=sys.stderr)
            exit_code = 1

        if exit_code == 0:
            last_successful_poll = poll_ended_at
        elif args.interval <= 0:
            return exit_code

        if args.interval <= 0:
            return exit_code


def main():
    load_dotenv(Path(__file__).resolve().parents[1] / ".env")

    parser = argparse.ArgumentParser(description="Poll Elasticsearch for error events and dispatch a GitHub Actions workflow.")
    parser.add_argument("--es-url", default=os.environ.get("ELASTICSEARCH_URL", "http://localhost:9200"), help="Elasticsearch base URL")
    parser.add_argument("--index-pattern", default=os.environ.get("ELASTICSEARCH_INDEX_PATTERN", "bloggingwebsite-logs-*"), help="Elasticsearch index pattern to query")
    parser.add_argument("--size", type=int, default=int(os.environ.get("ELASTICSEARCH_MAX_EVENTS", "50")), help="Maximum number of events to include per dispatch")
    parser.add_argument("--interval", type=int, default=int(os.environ.get("RELAY_POLL_INTERVAL_SECONDS", "10")), help="Seconds between Elasticsearch polls. Set to 0 for a single poll.")
    parser.add_argument("--repo", default=os.environ.get("GITHUB_REPOSITORY"), help="GitHub repository in OWNER/REPO form")
    parser.add_argument("--token", default=os.environ.get("GITHUB_TOKEN"), help="GitHub PAT with Actions: write permission")
    parser.add_argument("--workflow", default=os.environ.get("GITHUB_WORKFLOW_FILE", "copilot-error-analysis.yml"), help="Workflow file name or ID")
    parser.add_argument("--ref", default=os.environ.get("GITHUB_DEFAULT_BRANCH", "master"), help="Branch or tag containing the workflow")
    parser.add_argument("--github-api-url", default=os.environ.get("GITHUB_API_URL", "https://api.github.com"), help="GitHub API base URL")
    parser.add_argument("--es-username", default=os.environ.get("ELASTICSEARCH_USERNAME"), help="Optional Elasticsearch username")
    parser.add_argument("--es-password", default=os.environ.get("ELASTICSEARCH_PASSWORD"), help="Optional Elasticsearch password")
    args = parser.parse_args()

    if not args.repo:
        print("A GitHub repository name is required. Set GITHUB_REPOSITORY or pass --repo.", file=sys.stderr)
        return 2

    try:
        return run_polling(args)
    except KeyboardInterrupt:
        print("Relay stopped.")
        return 0
    except Exception as exc:  # pragma: no cover - CLI path for local automation
        print(f"Relay failed: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
