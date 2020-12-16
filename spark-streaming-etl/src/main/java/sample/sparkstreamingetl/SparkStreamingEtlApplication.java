package sample.sparkstreamingetl;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.streaming.DataStreamReader;
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
import sample.sparkstreamingetl.component.SourceManager;
import sample.sparkstreamingetl.component.TransformManager;
import sample.sparkstreamingetl.entity.Behavior;
import sample.sparkstreamingetl.transformer.BehaviorTransformer;

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
public class SparkStreamingEtlApplication implements CommandLineRunner {
	private final TransformManager transformManager;
	private final SourceManager sourceManager;

	public SparkStreamingEtlApplication(TransformManager transformManager, SourceManager sourceManager) {
		this.transformManager = transformManager;
		this.sourceManager = sourceManager;
	}

	public static void main(String[] args) {
		SpringApplication.run(SparkStreamingEtlApplication.class, args);
	}

	@Override
	public void run(String... args) {
		try {
			// initialize stream connection
			DataStreamReader rawStream = sourceManager.establishConnection();
			// transform data to semi-structured Behavior object
			Dataset<Behavior> dataStream = rawStream.load()
					.selectExpr("timestamp", "CAST(value AS STRING)")
					.withColumnRenamed("timestamp", "processed_at")
					.flatMap(BehaviorTransformer.transform(), Encoders.bean(Behavior.class));
			transformManager.transform(dataStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
