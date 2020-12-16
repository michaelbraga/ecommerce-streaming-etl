package sample.sparkstreamingetl.transformer;

import org.apache.commons.lang.StringUtils;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Row;
import org.json.JSONObject;
import sample.sparkstreamingetl.entity.Behavior;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class BehaviorTransformer {
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static FlatMapFunction<Row, Behavior> transform() {
        return row -> {
            if(row == null)
                return Collections.emptyIterator();
            // parse processed_at column
            Timestamp processedAt = row.getTimestamp(0);
            Behavior behavior = new Behavior();
            behavior.setProcessed_at(processedAt);
            // parse JSON fields for Behavior entity
            String value = row.getString(1);
            if(StringUtils.isBlank(value))
                return Collections.emptyIterator();
            JSONObject obj = new JSONObject(value);
            behavior.setEvent_type(obj.getString("event_type"));
            behavior.setCountry(obj.getString("country"));
            behavior.setLanguage(obj.getString("language"));
            behavior.setSession_id(obj.getString("session_id"));
            behavior.setUser_id(obj.getString("user_id"));
            String ts = obj.getString("timestamp");
            behavior.setTimestamp(Timestamp.valueOf(ts));
            behavior.setDate(Timestamp.valueOf(ts).toLocalDateTime().format(DATE_FORMATTER));
            behavior.setFields(obj.getJSONArray("fields").toString());
            return Collections.singleton(behavior).iterator();
        };
    }
}
