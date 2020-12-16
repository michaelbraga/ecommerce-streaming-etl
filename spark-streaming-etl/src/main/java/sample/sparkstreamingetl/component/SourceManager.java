package sample.sparkstreamingetl.component;

import org.apache.spark.sql.streaming.DataStreamReader;
import org.springframework.stereotype.Component;
import sample.sparkstreamingetl.config.SourceConfig;
import sample.sparkstreamingetl.spark.EtlSparkSession;

@Component
public class SourceManager {
    private final EtlSparkSession etlSparkSession;
    private final SourceConfig sourceConfig;

    public SourceManager(EtlSparkSession etlSparkSession, SourceConfig sourceConfig) {
        this.etlSparkSession = etlSparkSession;
        this.sourceConfig = sourceConfig;
    }

    public DataStreamReader establishConnection() {
        DataStreamReader stream = etlSparkSession.getSparkSession()
                .readStream().format(sourceConfig.getFormat());
        // parse options from config file and add to DataStreamReader
        for (String option : sourceConfig.getOptions()) {
            if (option.contains("=")) {
                String[] optionParts = option.split("=");
                stream = stream.option(optionParts[0], optionParts[1]);
            }
        }
        return stream;
    }
}
