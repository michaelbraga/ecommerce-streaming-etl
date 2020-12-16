### Start zookeeper, kafka, and hadoop for local setup

```
cd docker
docker network create etl-net
docker-compose up --build --remove-orphans
```