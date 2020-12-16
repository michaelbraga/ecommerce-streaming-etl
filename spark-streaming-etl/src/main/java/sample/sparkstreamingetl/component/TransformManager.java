package sample.sparkstreamingetl.component;

import lombok.extern.log4j.Log4j;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.StreamingQueryListener;
import org.apache.spark.sql.streaming.StreamingQueryManager;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.stereotype.Component;
import sample.sparkstreamingetl.config.TransformConfig;
import sample.sparkstreamingetl.entity.Behavior;
import sample.sparkstreamingetl.entity.EcommEvent;
import sample.sparkstreamingetl.spark.EtlSparkSession;
import sample.sparkstreamingetl.transformer.EcommEventTransformer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class TransformManager {
    private final static String CATEGORIZED = "CATEGORIZED";
    private final static String GENERIC = "GENERIC";
    private final static String FIELDS_COL = "fields";
    private final static String DBED_AT_COL = "dbed_at";
    private final static String ENTITY_CLASS = "entityClass";
    private final static String TRANSFORM_METHOD = "transform";
    private final static String TRANSFORMERS_PACKAGE = "sample.sparkstreamingetl.transformer.flatten";

    private final EtlSparkSession etlSparkSession;
    private final TransformConfig transformConfig;
    private final DataWriter dataWriter;

    public TransformManager(EtlSparkSession etlSparkSession, TransformConfig transformConfig, DataWriter dataWriter) {
        this.etlSparkSession = etlSparkSession;
        this.transformConfig = transformConfig;
        this.dataWriter = dataWriter;
    }

    public void transform(Dataset<Behavior> dataStream) throws Exception {
        if(transformConfig.getMode().equalsIgnoreCase(CATEGORIZED)) {
            String category = transformConfig.getCategoryColumn();
            // iterate all specific transformers
            for (BeanDefinition bean: getFlattenedTransformers()) {
                Class<?> clazz = Class.forName(bean.getBeanClassName());
                Object transformerClass = clazz.newInstance();
                FlatMapFunction transformMethod = getTransformMethod(clazz, transformerClass);
                Class classEncoding = getClassEncoding(clazz, transformerClass);
                String eventType = getEventType(clazz, transformerClass);
                String tableName = transformConfig.getBaseTable().toLowerCase() + "_" + eventType.toLowerCase();
                Dataset<Row> filteredDs = dataStream.filter(functions.col(category).equalTo(eventType))
                        .flatMap(transformMethod, Encoders.bean(classEncoding))
                        .drop(FIELDS_COL, category)
                        .withColumn(DBED_AT_COL, functions.lit(new Timestamp(new Date().getTime())));
                dataWriter.writeStream(filteredDs, tableName);
            }
        } else if(transformConfig.getMode().equalsIgnoreCase(GENERIC)) {
            // transform using generic transformer
            Dataset<Row> tds = dataStream.flatMap(EcommEventTransformer.transform(), Encoders.bean(EcommEvent.class))
                    .drop(FIELDS_COL)
                    .withColumn(DBED_AT_COL, functions.lit(new Timestamp(new Date().getTime())));
            dataWriter.writeStream(tds, transformConfig.getBaseTable());
        } else {
            System.out.println("Nothing to do here!");
        }
        waitStreamTermination();
    }

    private void waitStreamTermination() throws StreamingQueryException {
        StreamingQueryManager sqm = etlSparkSession.getSparkSession().streams();
        sqm.addListener(new StreamingQueryListener() {
            @Override
            public void onQueryStarted(QueryStartedEvent query) {
                System.out.println(String.format("Query started: %s (%s)", query.id(), query.name()));
            }
            @Override
            public void onQueryTerminated(QueryTerminatedEvent query) {
                System.out.println(String.format("Query terminated: %s (%s)", query.id(), query.exception()));
            }
            @Override
            public void onQueryProgress(QueryProgressEvent query) {
                System.out.println(String.format("Query in-progress: %s (%d rows)", query.progress().name(),
                        query.progress().numInputRows()));
            }
        });
        sqm.awaitAnyTermination();
    }

    private String getEventType(Class<?> clazz, Object transformerClass) throws Exception {
        Field classField = clazz.getDeclaredField(transformConfig.getCategoryColumn());
        classField.setAccessible(true);
        return String.valueOf(classField.get(transformerClass));
    }

    private Class getClassEncoding(Class<?> clazz, Object transformerClass) throws Exception {
        Field classField = clazz.getDeclaredField(ENTITY_CLASS);
        classField.setAccessible(true);
        return (Class) classField.get(transformerClass);
    }

    private FlatMapFunction getTransformMethod(Class<?> clazz, Object transformerClass) throws Exception {
        Method method = clazz.getDeclaredMethod(TRANSFORM_METHOD);
        return (FlatMapFunction) method.invoke(transformerClass);
    }

    private Set<BeanDefinition> getFlattenedTransformers() {
        final ClassPathScanningCandidateComponentProvider prov = new ClassPathScanningCandidateComponentProvider(false);
        prov.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));
        return prov.findCandidateComponents(TRANSFORMERS_PACKAGE);
    }
}