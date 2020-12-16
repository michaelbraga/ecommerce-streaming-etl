package sample.datagenerator.service;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sample.datagenerator.config.KafkaConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Properties;

@Log4j2
@Component
public class KafkaProducerService {
    @Autowired
    private KafkaConfig kafkaConfig;
    private Producer<String, String> producer;

    @PostConstruct
    private void initialize(){
        log.info(kafkaConfig.toString());
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaConfig.getBootstrapServers());
        props.put("acks", kafkaConfig.getAcks());
        props.put("retries", kafkaConfig.getRetries());
        props.put("key.serializer", kafkaConfig.getKeySerializer());
        props.put("value.serializer", kafkaConfig.getValueSerializer());
        producer = new KafkaProducer<>(props);
    }

    @PreDestroy
    private void shutdownProducer(){
        if(producer != null)
            producer.close();
    }

    public void sendToKafka(List<String> dataList) {
        dataList.forEach(data -> producer.send(new ProducerRecord<>(kafkaConfig.getTopic(), data)));
    }
}
