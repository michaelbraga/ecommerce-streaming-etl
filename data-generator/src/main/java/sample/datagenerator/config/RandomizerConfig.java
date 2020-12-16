package sample.datagenerator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "randomizer")
public class RandomizerConfig {
    private int totalUsers;
    private int totalProducts;
    private List<ColumnConfig> randomDataSchema;
    private Map<String, Map<String, String>> eventTypeFields;
}
