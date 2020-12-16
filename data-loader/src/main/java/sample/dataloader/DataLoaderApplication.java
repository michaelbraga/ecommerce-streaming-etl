package sample.dataloader;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import sample.dataloader.components.DataLoader;
import sample.dataloader.components.DataReader;

import java.util.List;

@SpringBootApplication(exclude = {
		GsonAutoConfiguration.class,
		CodecsAutoConfiguration.class,
		EmbeddedWebServerFactoryCustomizerAutoConfiguration.class,
		HttpMessageConvertersAutoConfiguration.class,
		JmxAutoConfiguration.class,
		JacksonAutoConfiguration.class,
		ProjectInfoAutoConfiguration.class,
		RestTemplateAutoConfiguration.class,
		ReactiveSecurityAutoConfiguration.class,
		ValidationAutoConfiguration.class
})
public class DataLoaderApplication implements CommandLineRunner {
	private final DataReader dataReader;
	private final DataLoader dataLoader;

	public DataLoaderApplication(DataReader dataReader, DataLoader dataLoader) {
		this.dataReader = dataReader;
		this.dataLoader = dataLoader;
	}

	public static void main(String[] args) {
		SpringApplication.run(DataLoaderApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<String> tables = dataReader.getTables();
		for(String table :  tables){
			Dataset<Row> stageDs = dataReader.readTableFromStaging(table);
			if(dataLoader.isTablePresent(table)) {
				Dataset<Row> destinationDs = dataLoader.readTable(table);
				if(dataLoader.isTableLevel()) {
					stageDs = stageDs.union(destinationDs)
							.dropDuplicates();
				} else if(dataLoader.isPartitionLevel()
						&& hasSamePartitions(dataReader.partitions(), dataLoader.partitions())
						&& dataReader.partitions().size() == 1) {
					String[] partitions = dataReader.partitions().toArray(new String[0]);
					Dataset<Row> sPartitions = stageDs.selectExpr(partitions).distinct();
					Dataset<Row> dPartitions = destinationDs.selectExpr(partitions).distinct();
					List<String> missing = sPartitions.except(dPartitions).as(Encoders.STRING()).collectAsList();
					stageDs = stageDs.filter(functions.col(partitions[0]).isInCollection(missing));
					/* @TODO: can be improved by loading the missing partition itself instead of filtering
					    and this will also allow more than 1 partitions */
					stageDs.show();
				}
			}
			dataLoader.loadTable(stageDs, table);
		}
	}

	private boolean hasSamePartitions(List<String> p1, List<String> p2) {
		if(p1.size() == 0 || p2.size() == 0 || (p1.size() != p2.size()))
			return false;
		for(int i=0; i<p1.size(); i+=1){
			if(!p1.get(i).equalsIgnoreCase(p2.get(i))){
				return false;
			}
		}
		return true;
	}
}
