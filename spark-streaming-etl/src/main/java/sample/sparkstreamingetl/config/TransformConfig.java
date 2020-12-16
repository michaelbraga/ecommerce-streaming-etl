package sample.sparkstreamingetl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "transform")
public class TransformConfig {
    private String mode;
    private String baseTable;
    private String categoryColumn;
}
