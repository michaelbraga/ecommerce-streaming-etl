package sample.sparkstreamingetl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "source")
public class SourceConfig {
    private String format;
    private List<String> options;
}
