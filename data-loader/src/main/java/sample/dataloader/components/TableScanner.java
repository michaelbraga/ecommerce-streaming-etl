package sample.dataloader.components;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Component;
import sample.dataloader.config.ScannerConfig;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TableScanner {
    private final ScannerConfig scannerConfig;
    private Configuration conf;

    @PostConstruct
    private void initializeConfig(){
        conf = new Configuration();
    }

    public TableScanner(ScannerConfig scannerConfig) {
        this.scannerConfig = scannerConfig;
    }

    public List<String> scanTables(String sourceLocation) throws IOException, URISyntaxException {
        FileSystem fs = null;
        if(sourceLocation.startsWith("hdfs://"))
             fs = FileSystem.get(new URI("hdfs://localhost:9000/"), conf);
        else
            fs = FileSystem.get(conf);

        FileStatus[] fileStatus = fs.listStatus(new Path(sourceLocation));
        List<String> tables = new ArrayList<>();
        boolean hasIncludes = scannerConfig.getIncludes() != null && scannerConfig.getIncludes().size() > 0;
        for (FileStatus status : fileStatus) {
            String table = status.getPath().getName();
            if(hasIncludes && scannerConfig.getIncludes().contains(table)) {
                tables.add(table);
            }
            else if(!hasIncludes && scannerConfig.getExcludes() != null && !scannerConfig.getExcludes().contains(table)) {
                tables.add(table);
            }
        }
        return tables;
    }
}
