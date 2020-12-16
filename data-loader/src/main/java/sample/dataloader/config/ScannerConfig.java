package sample.dataloader.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "scanner")
@Data
public class ScannerConfig {
    private List<String> includes;
    private List<String> excludes;
}
