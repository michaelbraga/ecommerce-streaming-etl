FROM openjdk:11-jre-slim

ENV KAFKA_VERSION 2.6.0
ENV SCALA_VERSION 2.12
ENV KAFKA_FOLDER kafka_"$SCALA_VERSION"-"$KAFKA_VERSION"
ENV KAFKA_HOME /opt/kafka
ENV KAFKA_SERVER_PROPS $KAFKA_HOME/config/server.properties
ENV KAFKA_URL http://mirror.rise.ph/apache/kafka/"$KAFKA_VERSION"/$KAFKA_FOLDER.tgz

RUN apt-get update && \
    apt-get install -y wget && \
    apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    wget --quiet -O - $KAFKA_URL | tar xzf - && \
    mv /$KAFKA_FOLDER $KAFKA_HOME && \
    rm $KAFKA_SERVER_PROPS

ADD server.properties $KAFKA_SERVER_PROPS
ADD create-topic.sh /opt/scripts/create-topic.sh
ADD start-kafka.sh /opt/scripts/start-kafka.sh

EXPOSE 9092

ENTRYPOINT ["sh", "/opt/scripts/start-kafka.sh"]