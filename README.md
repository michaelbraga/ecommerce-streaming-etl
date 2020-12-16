# ecommerce-streaming-etl

### Architecture
```
 Data Generator (data-generator)  -- generate logs -->    Kafka
                                                            |
                                                            V
                                                Spark Structured Streaming
 Staging (HDFS)  <--------------------------    (spark-streaming-etl)
       |
       |
       V                                                         
 Data Loader (data-loader)  ----------------->  Final Table (HDFS)
    
```
### Components
1. `Data Generator` is an application implemented in Java using SpringBoot framework to create random logs and sends it to kafka. A sample log data would look like this:
```json
{
  "country": "UY",
  "event_type": "PURCHASED_SUCCESS",
  "user_id": "fb2a5a61-d241-485d-93c6-876283bec74a",
  "session_id": "26954947-b7c4-42a4-ad7c-59ae496edb7f",
  "language": "PN",
  "fields": [
    {
      "value": "31.90",
      "key": "total_amount"
    },
    {
      "value": "jcb",
      "key": "payment_type"
    },
    {
      "value": true,
      "key": "used_promo"
    }
  ],
  "timestamp": "2020-12-17 03:56:46.63"
}
```
```json
{
  "country": "DZ",
  "event_type": "PURCHASED_CANCELLED",
  "user_id": "1c7657a3-4a17-49a8-9536-65c1638b9f97",
  "session_id": "46af3268-fd21-4930-93ef-c89167d0ca2a",
  "language": "CL",
  "fields": [
    {
      "value": "37.77",
      "key": "total_amount"
    },
    {
      "value": "discover",
      "key": "payment_type"
    },
    {
      "value": "iusto sit nam",
      "key": "reason"
    },
    {
      "value": true,
      "key": "used_promo"
    }
  ],
  "timestamp": "2020-12-17 04:08:47.326"
}
```

2. `Spark Streaming ETL` is a structured streaming application that consumes data from kafka and transforms it to relational data, 
   and saves it in parquet format (partitioned by "DATE"). There are two modes of transformation, `GENERIC` and `CATEGORIZED`. In `GENERIC` mode, all data are saved
   in 1 table  having this generic structure

`ecomm_event table`

| country  | event_type | user_id | session_id | language | timestamp | fieldKey | fieldValue |
| -------- | ---------- | ------- | ---------- | -------- | --------- | -------- | ---------- |
| UY  | PURCHASED_SUCCESS  | fb2a5a61-... | 26954947-... | PN | 2020-12-17 03:56:46.63 | total_amount | 31.90 |
| UY  | PURCHASED_SUCCESS  | fb2a5a61-... | 26954947-... | PN | 2020-12-17 03:56:46.63 | payment_type | jcb |
| UY  | PURCHASED_SUCCESS  | fb2a5a61-... | 26954947-... | PN | 2020-12-17 03:56:46.63 | used_promo | true |
| DZ  | PURCHASED_CANCELLED  | 1c7657a3-... | 46af3268-... | CL | 2020-12-17 04:08:47.326 | total_amount | 37.77 |
| DZ  | PURCHASED_CANCELLED  | 1c7657a3-... | 46af3268-... | CL | 2020-12-17 04:08:47.326 | payment_type | discover |
| DZ  | PURCHASED_CANCELLED  | 1c7657a3-... | 46af3268-... | CL | 2020-12-17 04:08:47.326 | reason | iusto sit nam |
| DZ  | PURCHASED_CANCELLED  | 1c7657a3-... | 46af3268-... | CL | 2020-12-17 04:08:47.326 | used_promo | true |

In `CATEGORIZED` mode data will be saved based on the event_type, for the above example, data will be saved in 2 tables

`ecomm_event_purchased_success table`

| country  | event_type | user_id | session_id | language | timestamp | total_amount | payment_type | used_promo |
| -------- | ---------- | ------- | ---------- | -------- | --------- | ------------ | ------------ | ---------- |
| UY  | PURCHASED_SUCCESS  | fb2a5a61-... | 26954947-... | PN | 2020-12-17 03:56:46.63 | 31.90 | jcb | true |

`ecomm_event_purchased_success table`

| country  | event_type | user_id | session_id | language | timestamp | total_amount | payment_type | reason | used_promo |
| -------- | ---------- | ------- | ---------- | -------- | --------- | ------------ | ------------ | ------ | ---------- | 
| DZ  | PURCHASED_CANCELLED  | 1c7657a3-... | 46af3268-... | CL | 2020-12-17 04:08:47.326 | 31.90 | jcb | iusto sit nam | true |

3. `Data Loader` reads data from the staging location where Spark Streaming ETL is saving logs. There are 2 modes in loading data, `TABLE` level and `PARTITION` level. 
   `TABLE` level reads the whole table from staging and unions the data with the same table in the final location (if it exists), then overwrites (overwrite=dynamic) 
   the resulting data in the final location. While in `PARTITION` level, data loader retrieves all missing partitions in final table from staging table, and moves the 
   missing partitions from staging to final table. During loading of data, you can configure resulting parquet data to have less files generated compared to the number
   of files generated by Spark Streaming, and perform operations like ordering based on column.
   

## Dev Setup
1. Setup Kafka and zookeeper using Docker (will be hosted here: `localhost:9092`)
```shell
cd docker
docker-compose rm
docker-compose build
docker-compose up
```
2. Install Spark binaries and replace snakeyaml-1.24.jar with snakeyaml-1.27.jar, to avoid conflict with SpringBoot
3. Install Hadoop, and run hadoop in pseudo-distributed mode
4. Run `spark-streaming-etl`
```shell
cd spark-streaming-etl
mvn clean package -DskipTests
spark-submit \
  --master local[*] \
  --class sample.sparkstreamingetl.SparkStreamingEtlApplication \
  --files target/classes/log4j.properties \
  --conf spark.driver.extraJavaOptions=-Dlog4j.configuration=log4j.properties \
  target/spark-streaming-etl-1.0.jar
```
5. Run `data-generator`
```shell
cd data-generator
mvn clean package -DskipTests
java -jar target/data-generator-1.0.jar
```
6. Run `data-loader`. As of the moment, it is not scheduled and must be executed on demand, so it is better to run it when there is already data in HDFS.
```shell
cd data-loader
mvn clean package -DskipTests
spark-submit \
  --master local[*] \
  --class sample.dataloader.DataLoaderApplication \
  --files target/classes/log4j.properties \
  --conf spark.driver.extraJavaOptions=-Dlog4j.configuration=log4j.properties \
  target/data-loader-1.0.jar
```