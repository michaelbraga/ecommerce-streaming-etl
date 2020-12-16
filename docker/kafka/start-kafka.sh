KAFKA_HOME=/opt/kafka

sh /opt/scripts/create-topic.sh &
$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties