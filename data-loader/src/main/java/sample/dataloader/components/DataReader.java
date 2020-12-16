package sample.dataloader.components;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Component;
import sample.dataloader.config.ReaderConfig;
import sample.dataloader.spark.LoaderSparkSession;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Component
public class DataReader {
    private final TableScanner tableScanner;
    private final ReaderConfig readerConfig;
    private final LoaderSparkSession loaderSparkSession;

    public DataReader(ReaderConfig readerConfig, TableScanner tableScanner, LoaderSparkSession loaderSparkSession) {
        this.readerConfig = readerConfig;
        this.tableScanner = tableScanner;
        this.loaderSparkSession = loaderSparkSession;
    }

    public List<String> getTables() throws IOException, URISyntaxException {
        return tableScanner.scanTables(readerConfig.getSourceLocation());
    }

    public Dataset<Row> readTableFromStaging(String table) {
        Dataset<Row> stageDs = loaderSparkSession.getSparkSession().read()
                .format(readerConfig.getFormat())
                .load(readerConfig.getSourceLocation() + "/" + table);
        return stageDs;
    }

    public List<String> partitions() {
        return readerConfig.getPartitions();
    }
}
