package sample.sparkstreamingetl.transformer.flatten;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.json.JSONArray;
import org.json.JSONObject;
import sample.sparkstreamingetl.entity.Behavior;
import sample.sparkstreamingetl.entity.flatten.PurchasedError;
import sample.sparkstreamingetl.transformer.FlattenedTransformer;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;

public class PurchasedErrorTransformer extends FlattenedTransformer implements Serializable {
    final static Class entityClass = PurchasedError.class;
    final static String event_type = "PURCHASED_ERROR";

    public FlatMapFunction<Behavior, PurchasedError> transform() {
        return b -> {
            if(b == null)
                return Collections.emptyIterator();
            JSONArray fields = new JSONArray(b.getFields());
            PurchasedError e = new PurchasedError(b);
            for(int i=0; i<fields.length(); i+=1) {
                JSONObject jsonObject = fields.getJSONObject(i);
                Field f = PurchasedError.class.getDeclaredField(jsonObject.getString("key"));
                setField(f, e, jsonObject);
            }
            return Collections.singleton(e).iterator();
        };
    }
}
