package sample.sparkstreamingetl.component;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.Trigger;
import org.springframework.stereotype.Component;
import sample.sparkstreamingetl.config.WriterConfig;

import java.io.Serializable;
import java.util.concurrent.TimeoutException;

@Component
public class DataWriter implements Serializable {
    private final static long serialVersionUID = 1230L;
    private final WriterConfig writerConfig;

    public DataWriter(WriterConfig writerConfig) {
        this.writerConfig = writerConfig;
    }

    public void writeStream(Dataset<Row> dataStream, String tableName) throws TimeoutException {
        dataStream.writeStream()
                .outputMode(OutputMode.Append())
                .format(writerConfig.getFormat())
                .trigger(Trigger.ProcessingTime(writerConfig.getTriggerTime()))
                .queryName(tableName)
                .option("path", writerConfig.getDestination() + "/" + tableName)
                .option("checkpointLocation", writerConfig.getCheckpointLocation() + "/" + tableName)
                .partitionBy(writerConfig.getPartitions().toArray(new String[0]))
                .start();
    }
}
