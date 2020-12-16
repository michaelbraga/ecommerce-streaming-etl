package sample.datagenerator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfig {
    private String topic;
    private String bootstrapServers;
    private String acks;
    private String keySerializer;
    private String valueSerializer;
    private int retries;
    private int batchSize;
}
