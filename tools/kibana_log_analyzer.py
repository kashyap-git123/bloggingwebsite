#!/usr/bin/env python3

import argparse
import json
import os
import sys
from pathlib import Path

WINDOWS = [
    "user.registration",
    "user.lookup",
    "http.request",
    "application.method",
]


def load_payload(path: str | None, raw_payload: str | None):
    if raw_payload and raw_payload.strip() not in ("{}", "null", ""):
        try:
            return json.loads(raw_payload)
        except json.JSONDecodeError:
            raise ValueError("client payload is not valid JSON")
    if path:
        p = Path(path)
        if not p.exists():
            raise FileNotFoundError(f"Input file not found: {path}")
        with p.open("r", encoding="utf-8") as fh:
            return json.load(fh)
    return {}


def normalize_events(payload):
    if isinstance(payload, dict):
        if "events" in payload and isinstance(payload["events"], list):
            return payload["events"]
        if "items" in payload and isinstance(payload["items"], list):
            return payload["items"]
        return [payload]
    if isinstance(payload, list):
        return payload
    return []


def lookup_value(event, *keys):
    for key in keys:
        if key in event:
            return event[key]
    return None


def find_recommendation(event):
    error_data = event.get("error") or {}
    message = error_data.get("message") or lookup_value(event, "error.message", "message") or ""
    error_type = error_data.get("type") or lookup_value(event, "error.type", "error_type") or ""
    combined = f"{error_type} {message}".lower()

    if "favicon" in combined or "no static resource" in combined:
        return "Missing static asset. Add the missing favicon or route and confirm the browser request is expected."
    if "useralreadyexist" in combined or "already exists" in combined:
        return "Duplicate user registration. Validate unique email checks and prevent duplicate submissions before creating the account."
    if "accessdenied" in combined or "forbidden" in combined:
        return "Authorization mismatch. Check the user role and request mapping for the protected endpoint."
    if "connection refused" in combined or "communications link failure" in combined or "cannot get jdbc connection" in combined:
        return "Database connectivity issue. Verify the JDBC URL, MySQL service, credentials, and network access for the app."
    if "duplicate" in combined or "data integrity" in combined or "constraint" in combined:
        return "Data integrity conflict. Review the database schema and unique constraints on the affected entity."
    if "not found" in combined or "user not found" in combined:
        return "Resource lookup issue. Check the records being queried and the service logic that resolves the entity."
    return "Review the failing request path and related dependency state; verify the source code and runtime configuration that produced this event."


def render_summary(events):
    if not events:
        return "No actionable Kibana events found."

    lines = ["## Kibana error summary", ""]
    for idx, event in enumerate(events[:10], start=1):
        log_obj = event.get("log") or {}
        log_level = log_obj.get("level") or lookup_value(event, "log.level", "log_level") or "UNKNOWN"
        event_obj = event.get("event") or {}
        event_name = event_obj.get("action") or lookup_value(event, "event.action", "event_action") or "unknown"
        error_data = event.get("error") or {}
        error_type = error_data.get("type") or lookup_value(event, "error.type", "error_type") or "unknown"
        error_message = error_data.get("message") or lookup_value(event, "error.message", "message") or "No message available"
        correlation_obj = event.get("correlation") or {}
        correlation_id = correlation_obj.get("id") or lookup_value(event, "correlation.id", "correlation_id") or "n/a"
        recommendation = find_recommendation(event)

        lines.append(f"### Event {idx}")
        lines.append(f"- level: {log_level}")
        lines.append(f"- action: {event_name}")
        lines.append(f"- error.type: {error_type}")
        lines.append(f"- error.message: {error_message}")
        lines.append(f"- correlation.id: {correlation_id}")
        lines.append(f"- likely fix: {recommendation}")
        lines.append("")
    return "\n".join(lines)


def main():
    parser = argparse.ArgumentParser(description="Analyze Kibana ECS error events and suggest likely fixes.")
    parser.add_argument("--input", default=None, help="Path to a JSON file containing Kibana events")
    parser.add_argument("--payload", default=None, help="Raw JSON payload passed by the workflow")
    args = parser.parse_args()

    try:
        payload = load_payload(args.input or os.environ.get("KIBANA_INPUT_PATH"), args.payload or os.environ.get("KIBANA_EVENT_PAYLOAD"))
        events = normalize_events(payload)
        filtered = []
        for event in events:
            if not isinstance(event, dict):
                continue
            log_level = (event.get("log") or {}).get("level") or lookup_value(event, "log.level", "log_level") or ""
            event_outcome = (event.get("event") or {}).get("outcome") or lookup_value(event, "event.outcome") or ""
            error_type = (event.get("error") or {}).get("type") or lookup_value(event, "error.type", "error_type") or ""
            if str(log_level).upper() in {"WARN", "ERROR"} or str(event_outcome).lower() == "failure" or error_type:
                filtered.append(event)
        summary = render_summary(filtered)
        print(summary)
        return 0
    except Exception as exc:  # pragma: no cover - CLI path for workflow diagnostics
        print(f"Analysis failed: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
