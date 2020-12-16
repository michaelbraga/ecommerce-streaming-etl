KAFKA_HOME=/opt/kafka
KAFKA_TOPIC=behavioral-data
sleep 15
echo "$KAFKA_HOME/bin/kafka-topics.sh --zookeeper zookeeper:2181 --create --topic $KAFKA_TOPIC --partitions 1 --replication-factor 1" && \
$KAFKA_HOME/bin/kafka-topics.sh --zookeeper zookeeper:2181 --create --topic $KAFKA_TOPIC --partitions 1 --replication-factor 1