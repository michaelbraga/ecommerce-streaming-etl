package sample.sparkstreamingetl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "writer")
public class WriterConfig {
    private String destination;
    private String format;
    private String checkpointLocation;
    private String triggerTime;
    private List<String> partitions;
}
