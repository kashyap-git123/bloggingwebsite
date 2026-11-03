#!/usr/bin/env python3

import argparse
import json
import os
import sys
from pathlib import Path
from urllib import error, request


def load_payload(path: str | None, raw_payload: str | None):
    if raw_payload and raw_payload.strip() not in ("{}", "null", ""):
        try:
            return json.loads(raw_payload)
        except json.JSONDecodeError as exc:
            raise ValueError(f"raw payload is not valid JSON: {exc}") from exc
    if path:
        p = Path(path)
        if not p.exists():
            raise FileNotFoundError(f"Input file not found: {path}")
        with p.open("r", encoding="utf-8") as fh:
            return json.load(fh)
    return {}


def build_dispatch_body(event_type: str, payload: object):
    return {
        "event_type": event_type,
        "client_payload": payload,
    }


def send_dispatch(repo: str, token: str, event_type: str, payload: object, api_url: str):
    if not repo or "/" not in repo:
        raise ValueError("repo must be in the form OWNER/REPO")
    if not token:
        raise ValueError("GITHUB_TOKEN or --token is required")

    url = f"{api_url.rstrip('/')}/repos/{repo}/dispatches"
    body = build_dispatch_body(event_type, payload)
    data = json.dumps(body).encode("utf-8")
    req = request.Request(
        url,
        data=data,
        headers={
            "Authorization": f"Bearer {token}",
            "Accept": "application/vnd.github+json",
            "X-GitHub-Api-Version": "2022-11-28",
            "Content-Type": "application/json",
            "User-Agent": "kibana-log-analyzer/1.0",
        },
        method="POST",
    )

    try:
        with request.urlopen(req) as response:
            print(f"Repository dispatch accepted with HTTP {response.status}.")
            return 0
    except error.HTTPError as exc:
        output = exc.read().decode("utf-8", errors="replace")
        print(f"GitHub rejected the repository dispatch: {exc.code} - {output}", file=sys.stderr)
        return 1
    except error.URLError as exc:
        print(f"Unable to reach GitHub API: {exc}", file=sys.stderr)
        return 1


def main():
    parser = argparse.ArgumentParser(
        description="Send Kibana events to GitHub as a repository_dispatch event for the log analysis workflow."
    )
    parser.add_argument("--repo", default=os.environ.get("GITHUB_REPOSITORY"), help="GitHub repository in OWNER/REPO form")
    parser.add_argument("--token", default=os.environ.get("GITHUB_TOKEN"), help="GitHub token with repo scope")
    parser.add_argument("--event-type", default="kibana-errors", help="Repository dispatch event type")
    parser.add_argument("--api-url", default=os.environ.get("GITHUB_API_URL", "https://api.github.com"), help="GitHub API base URL")
    parser.add_argument("--input", default=None, help="Path to the Kibana JSON payload file")
    parser.add_argument("--payload", default=None, help="Raw JSON payload to send")
    args = parser.parse_args()

    try:
        payload = load_payload(args.input, args.payload)
        return send_dispatch(args.repo, args.token, args.event_type, payload, args.api_url)
    except Exception as exc:  # pragma: no cover - CLI helper path
        print(f"Dispatch failed: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
