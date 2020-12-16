package sample.sparkstreamingetl.spark;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Component;
import sample.sparkstreamingetl.config.SparkConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Getter
@Component
public class EtlSparkSession {
    private final SparkConfig sparkConfig;
    private SparkSession sparkSession;

    public EtlSparkSession(SparkConfig sparkConfig) {
        this.sparkConfig = sparkConfig;
    }

    @PostConstruct
    private void initializeSparkSession() {
        SparkConf conf = new SparkConf();
        if(StringUtils.isNotBlank(sparkConfig.getMaster()))
            conf.setMaster(sparkConfig.getMaster());
        conf.setAppName(sparkConfig.getAppName());
        // set spark configurations
        for(String c : sparkConfig.getConfigurations()){
            if(c.contains("=")){
                String[] confParts = c.split("=");
                conf.set(confParts[0], confParts[1]);
                System.out.println(String.format("\t%s = %s", confParts[0], confParts[1]));
            }
        }
        sparkSession = SparkSession.builder().config(conf).getOrCreate();
        Configuration hadoopConf = sparkSession.sparkContext().hadoopConfiguration();
        // set hadoop configurations
        for(String c : sparkConfig.getHadoopConfigurations()){
            if(c.contains("=")){
                String[] confParts = c.split("=");
                hadoopConf.set(confParts[0], confParts[1]);
                System.out.println(String.format("\t%s = %s", confParts[0], confParts[1]));
            }
        }
        sparkSession.sparkContext().setLogLevel(sparkConfig.getLogLevel());
        System.out.println("Created spark session");
        System.out.println("Spark Configuration: " + sparkConfig.toString());
        System.out.println(String.valueOf(sparkSession.conf().getAll()).replace(",", "\n"));
    }

    @PreDestroy
    private void destroySparkSession(){
        if(sparkSession != null){
            try {
                sparkSession.close();
            } catch (Exception e){
                System.out.println("Failed shutting down spark session: " + e.getMessage());
            }
        }
        System.out.println("Spark session destroyed");
    }
}
