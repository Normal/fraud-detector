## Design

![Architecture](doc/fd-design.png)

## Versions

- java 8
- scala 2.11
- spark 2.3
- kafka 1.1.0
- cassandra 3.10

## Data generation

Generate log for 1 bot, 1000 users, 100 requestes/sec, duration 300 seconds:    

    python3 botgen.py -b 1 -u 1000 -n 100 -d 300 -f data.json
    
## Running kafka
    
    bin/zookeeper-server-start.sh config/zookeeper.properties
    bin/kafka-server-start.sh config/server.properties
    bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic input
    
## Running kafka connect

    bin/connect-standalone.sh $PROJECT_HOME/conf/connect-standalone.properties $PROJECT_HOME/conf/text-source.properties
    # check data is populated
    bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic input --from-beginning

## Running cassandra locally

    docker run --name some-cassandra -d -p 9042:9042 cassandra:3.10
    docker exec -it some-cassandra cqlsh
    CREATE KEYSPACE fx WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};    
    CREATE TABLE fx.fraud (
         ip text,
         type text,
         count text,
         primary key (ip));
         
## Run and build spark streaming job

    sbt assembly
    spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.3.0 target/scala-2.11/fraud-detector-0.1.jar
    
## Check results

    docker exec -it some-cassandra cqlsh
    select * from fx.fraud;
    