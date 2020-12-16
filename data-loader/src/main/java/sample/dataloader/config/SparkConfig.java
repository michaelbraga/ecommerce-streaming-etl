package sample.dataloader.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "spark")
@Data
public class SparkConfig {
    private String master;
    private String appName;
    private String logLevel;
    private List<String> configurations;
    private List<String> hadoopConfigurations;
}
