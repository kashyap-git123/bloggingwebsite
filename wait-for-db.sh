#!/bin/bash
until nc -z db 3306; do
  echo "⏳ Waiting for MySQL to be ready..."
  sleep 2
done

echo "✅ MySQL is up! Starting the app..."
exec java -jar /app/blogging-app.jar
