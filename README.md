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

Set the password value to the same private local value you use when starting Compose. Start the application in that same PowerShell session:

```powershell
.\mvnw.cmd spring-boot:run
```

The first launch initializes the demo tables. Wait until Spring Boot reports that Tomcat started before generating activity.

### Start Elasticsearch, Kibana, and Filebeat

From the repository root:

```powershell
docker compose -f docker-compose.logging.yml up -d
docker compose -f docker-compose.logging.yml ps
```

Elasticsearch is available at <http://localhost:9200>, Kibana at <http://localhost:5601>, and the synthetic MySQL database at localhost port 3307. Filebeat starts after Elasticsearch is healthy. It watches only `/usr/share/blog-logs/blog-app.json`, mounted from the repository's `logs` directory; the old text log and JVM crash/replay files are not ingested.

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

### Stop the stack

```powershell
docker compose -f docker-compose.logging.yml down
```

Elasticsearch and Filebeat state persist in Docker volumes. To remove those local indexed records and Filebeat offsets as well:

```powershell
docker compose -f docker-compose.logging.yml down -v
```

This removes the stack's Docker volumes, including demo MySQL and indexed Elastic data, not the repository log files or any separate local MySQL data.