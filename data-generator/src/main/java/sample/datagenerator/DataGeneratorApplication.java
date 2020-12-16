package sample.datagenerator;

import com.github.javafaker.Faker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sample.datagenerator.component.JsonRandomizerService;
import sample.datagenerator.config.AppConfig;
import sample.datagenerator.service.KafkaProducerService;

import java.util.Arrays;
import java.util.List;

@Log4j2
@SpringBootApplication
public class DataGeneratorApplication implements CommandLineRunner {
	@Autowired
	private JsonRandomizerService jsonRandomizerService;
	@Autowired
	private KafkaProducerService kafkaProducer;
	@Autowired
	private AppConfig appConfig;

	public static void main(String[] args) {
		SpringApplication.run(DataGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Generating random dataset for timed-series behavioral data (JSON)");
		Faker faker = new Faker();
		while(true) {
			Thread.sleep(appConfig.getIntervalSeconds() * 1000);
			List<String> dataList = null;
			if(faker.number().randomNumber() % 2 == 0) {
				String data = jsonRandomizerService.generateData();
				dataList = Arrays.asList(data);
			} else {
				dataList = jsonRandomizerService.generateBulkData();
			}
			log.info("Generated " + dataList.size() + " logs");
			kafkaProducer.sendToKafka(dataList);
		}
	}
}
