package sample.dataloader.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "reader")
@Data
public class ReaderConfig {
    private String sourceLocation;
    private String format;
    private List<String> partitions;
}
