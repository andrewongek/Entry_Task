# Entry_Task

Requires Docker instance of: 
- MYSQL
- Redis cache
- Zipkin

can be found in the `compose.yaml` file. 
```
# This will reset all the data in the DB and Cache
docker compose down -v
docker compose up -d
```