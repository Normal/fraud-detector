## Design

![Architecture](doc/fd-design.png)

## Versions

- scala 2.11
- spark 2.3
- kafka 1.1.0
- cassandra 3.10

## Data generation

Write log for 1 bot, 1000 users, 100 requestes/sec, duration 300 seconds:    

    python3 botgen.py -b 1 -u 1000 -n 100 -d 300 -f data.json
    
## Running kafka
    
    bin/zookeeper-server-start.sh config/zookeeper.properties
    bin/kafka-server-start.sh config/server.properties
    bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic input
    
## Running kafka connect

    bin/connect-standalone.sh config/connect-standalone.properties config/json-source.properties
    

## Running locally

    docker run --name some-cassandra -d cassandra:3.10