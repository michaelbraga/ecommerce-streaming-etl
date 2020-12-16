package sample.dataloader.components;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.functions;
import org.springframework.stereotype.Component;
import sample.dataloader.config.LoaderConfig;
import sample.dataloader.spark.LoaderSparkSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
public class DataLoader {
    private final LoaderConfig loaderConfig;
    private final LoaderSparkSession loaderSparkSession;
    private Configuration conf;

    @PostConstruct
    private void initializeConfig(){
        conf = new Configuration();
    }

    public DataLoader(LoaderConfig loaderConfig, LoaderSparkSession loaderSparkSession) {
        this.loaderConfig = loaderConfig;
        this.loaderSparkSession = loaderSparkSession;
    }

    public Dataset<Row> readTable(String table) {
        return loaderSparkSession.getSparkSession()
                .read()
				.format(loaderConfig.getFormat())
				.load(loaderConfig.getDestinationLocation() + "/" + table);
    }

    public boolean isTableLevel() {
        return "TABLE".equalsIgnoreCase(loaderConfig.getLevel());
    }

    public boolean isPartitionLevel() {
        return "PARTITION".equalsIgnoreCase(loaderConfig.getLevel());
    }

    public void loadTable(Dataset<Row> dataset, String table) {
        System.out.println(String.format("- Loading [%s]...", table));
        dataset = dataset.repartition(functions.col("date"));

        // perform any sorting
        for(String orderByClause : loaderConfig.getOrderBy()){
            if(orderByClause.contains("=")){
                String[] oc = orderByClause.split("=");
                Column column = functions.col(oc[0]);
                if("desc".equalsIgnoreCase(oc[1]))
                    column = column.desc();
                else
                    column = column.asc();
                dataset = dataset.orderBy(column);
            }
        }

        // @TODO: add computation to avoid small files
        int numFiles = 2;

        dataset.coalesce(numFiles)
                .write()
                .format(loaderConfig.getFormat())
                .mode(SaveMode.Overwrite)
                .partitionBy(loaderConfig.getPartitions().toArray(new String[0]))
                .option("path", loaderConfig.getDestinationLocation() + "/" + table)
                .save();
        System.out.println(String.format("[%s] loaded!", table));
    }

    public boolean isTablePresent(String table) throws IOException, URISyntaxException {
        FileSystem fs = null;
        if(loaderConfig.getDestinationLocation().startsWith("hdfs://"))
            fs = FileSystem.get(new URI("hdfs://localhost:9000/"), conf);
        else
            fs = FileSystem.get(conf);
        return fs.exists(new Path(loaderConfig.getDestinationLocation() + "/" + table));
    }

    public List<String> partitions() {
        return loaderConfig.getPartitions();
    }
}
