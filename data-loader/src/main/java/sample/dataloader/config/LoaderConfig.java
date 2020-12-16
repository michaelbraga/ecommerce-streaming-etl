package sample.dataloader.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "loader")
@Data
public class LoaderConfig {
    private String level;
    private String format;
    private String destinationLocation;
    private List<String> partitions;
    private List<String> orderBy;
    private boolean deleteSourceOnSuccess;
}
