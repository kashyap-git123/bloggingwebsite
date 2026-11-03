# Blogging Website

## Local ELK Logging

The optional `elk` Spring profile writes ECS-formatted JSON to `logs/blog-app.json`. Filebeat reads only that file and indexes events in Elasticsearch for Kibana. The existing default profile remains available for the current local development setup.

This Compose stack disables Elasticsearch security for ease of local development. Its ports bind to localhost; do not expose it to a network or use it for production data.

### Prerequisites

- Docker Desktop using Linux containers, with at least 3 GB available to Docker. Elasticsearch uses a 512 MB JVM heap.
- Java 21 and the project's Maven wrapper.
- Docker Desktop will also run an isolated MySQL 8 demo database on localhost port 3307.
- PowerShell 5.1 or newer.

### Prepare an isolated demo database

Compose creates a separate `blogging_elk_demo` schema in its own MySQL container and persistent volume. It binds only to localhost port 3307 and never touches the application's normal `blogging_db` schema. Its default credentials are synthetic and for local demonstrations only. You can override the password before starting the stack:

```powershell
$env:BLOG_DEMO_DB_PASSWORD = "blogging-demo-local-only"
$env:BLOG_DEMO_JDBC_URL = "jdbc:mysql://localhost:3307/blogging_elk_demo?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"
$env:BLOG_DEMO_DB_USERNAME = "blogging_demo"
$env:SPRING_PROFILES_ACTIVE = "elk"
```

Set the password value to the same private local value you use when starting Compose. Keep these environment variables in this PowerShell session for starting both Compose and the application.

### Start Elasticsearch, Kibana, and Filebeat

From the repository root:

```powershell
docker compose -f docker-compose.logging.yml up -d
docker compose -f docker-compose.logging.yml ps
```

This starts the isolated demo database as well as Elasticsearch, Kibana, and Filebeat. Wait for the `demo-mysql` and `elasticsearch` services to report healthy. Elasticsearch is available at <http://localhost:9200>, Kibana at <http://localhost:5601>, and the synthetic MySQL database at localhost port 3307. Filebeat starts after Elasticsearch is healthy. It watches only `/usr/share/blog-logs/blog-app.json`, mounted from the repository's `logs` directory; the old text log and JVM crash/replay files are not ingested.

### Start the application

In the same PowerShell session where you set the demo database and Spring profile variables:

```powershell
.\mvnw.cmd spring-boot:run
```

The first launch initializes the demo tables. Wait until Spring Boot reports that Tomcat started before generating activity.

### Generate synthetic blogging activity

In a second PowerShell session, pass the demo JDBC URL explicitly. The script rejects any URL that does not target `blogging_elk_demo`.

```powershell
$demoJdbcUrl = "jdbc:mysql://localhost:3307/blogging_elk_demo?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"
.\tools\generate-demo-traffic.ps1 -DatabaseUrl $demoJdbcUrl -Count 3
```

It creates synthetic BLOGGER and GUEST users and exercises registration, authentication, publishing, browsing, searching, likes, comments, and replies. It does not print account credentials or generated content.

### Explore events in Kibana

1. Open <http://localhost:5601>.
2. In **Stack Management → Data Views**, create a data view named `bloggingwebsite-logs-*` and select `@timestamp` as the time field.
3. Open **Discover** and select that data view. Useful filters include `event.action`, `event.outcome`, `log.level`, `service.name`, `post.id`, and `user.role`.

The app emits successful/failed method outcomes and durations, plus domain events for account registration/authentication and post, comment, reply, and like actions. Request values, credentials, email addresses, and post/comment/reply text are intentionally excluded from application events.

### Exercise a handled backend failure and relay it to GitHub Actions

The authenticated diagnostic endpoint below simulates a downstream profile-lookup timeout, logs a WARN ECS event, and returns a fallback profile with HTTP 200. It does not change normal page behavior or write to the database. Use it against the local `elk` profile:

1. Log in to the local app, then open <http://localhost:8080/blog/users/debug/backend-fallback?simulate=true> in the same browser. The response should have `"status":"ok"` and a fallback user.
2. In Kibana Discover, use the `bloggingwebsite-logs-*` data view and filter with `event.action : "user.profile.lookup" and event.outcome : "failure"`. Confirm the simulated timeout event appears before relaying.
3. Ensure the repository-root `.env` contains `GITHUB_TOKEN=<your-token>` (do not commit or print the token), then start the relay. It reads `.env` relative to its own file location, so it works from any current directory. Existing process environment variables take precedence over `.env`. The token needs permission to dispatch workflows on the target GitHub repository. The relay polls every 10 seconds by default and only searches for events whose `@timestamp` is at or after the relay's startup time. Stop it with Ctrl+C:

   ```powershell
   python .\tools\elasticsearch_webhook_relay.py --es-url http://localhost:9200 --index-pattern bloggingwebsite-logs-* --interval 10 --repo OWNER/REPO
   ```

   Replace `OWNER/REPO` with the repository containing `.github/workflows/copilot-error-analysis.yml`. The relay token needs **Actions: read and write** permission and only **Contents: read-only**; it uses the workflow-dispatch API rather than repository dispatch. This token is separate from the Copilot CLI credential. Each poll covers newly timestamped events since the previous poll, with a one-minute overlap to catch indexing delays and event-ID deduplication to avoid resending events already accepted by GitHub.
4. Configure the repository Actions secret `COPILOT_GITHUB_TOKEN` with a fine-grained token granted **Copilot Requests** permission and no repository write permissions. The GitHub Actions runner invokes Copilot CLI directly for root-cause analysis; no local Copilot process or separate rule-based analyzer generates the RCA. The job has no checkout, no GitHub token permissions, no built-in MCP servers, and no available tools. It passes only an allowlisted set of event fields to Copilot CLI and publishes its report to the Actions summary and as an artifact.
5. To test a single error without Elasticsearch, open **Actions → Copilot Error Analysis**, choose **Run workflow**, and enter the error in `error_message`. For the live webhook path, inspect the `workflow_dispatch` run. Both paths use Copilot CLI in GitHub Actions to create the RCA.
6. To investigate manually with Copilot CLI, use your repository checkout and the event details from Kibana or the Actions run as context. This workflow does not give Copilot access to the repository or permit it to modify files.

The failure is caught and converted into a fallback response; in Kibana it is a WARN-level event, not an uncaught HTTP 500. The relay’s default index pattern matches the Filebeat index configured for this project.

### Stop the stack

```powershell
docker compose -f docker-compose.logging.yml down
```

Elasticsearch and Filebeat state persist in Docker volumes. To remove those local indexed records and Filebeat offsets as well:

```powershell
docker compose -f docker-compose.logging.yml down -v
```

This removes the stack's Docker volumes, including demo MySQL and indexed Elastic data, not the repository log files or any separate local MySQL data.