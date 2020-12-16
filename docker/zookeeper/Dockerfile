FROM openjdk:11-jre-slim

ARG ZOOKEEPER_VER=zookeeper-3.6.2
ARG ZOOKEEPER_URL=https://downloads.apache.org/zookeeper/"$ZOOKEEPER_VER"/apache-"$ZOOKEEPER_VER"-bin.tar.gz

ARG ZOOKEEPER_HOME=/opt/zookeeper

RUN apt-get update && \
    apt-get install -y wget && \
    apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    wget --quiet -O - $ZOOKEEPER_URL | tar xzf - && \
    mv /apache-"$ZOOKEEPER_VER"-bin $ZOOKEEPER_HOME

ADD zoo.cfg $ZOOKEEPER_HOME/conf/

RUN echo "$(ls $ZOOKEEPER_HOME/)"
RUN echo "$(ls $ZOOKEEPER_HOME/conf/)"

EXPOSE 2181

ENTRYPOINT ["/opt/zookeeper/bin/zkServer.sh", "start-foreground"]

