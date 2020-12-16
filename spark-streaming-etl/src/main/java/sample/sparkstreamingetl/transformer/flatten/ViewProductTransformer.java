package sample.sparkstreamingetl.transformer.flatten;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.json.JSONArray;
import org.json.JSONObject;
import sample.sparkstreamingetl.entity.Behavior;
import sample.sparkstreamingetl.entity.flatten.ViewProduct;
import sample.sparkstreamingetl.transformer.FlattenedTransformer;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;

public class ViewProductTransformer extends FlattenedTransformer implements Serializable {
    final static Class entityClass = ViewProduct.class;
    final static String event_type = "VIEW_PRODUCT";

    public FlatMapFunction<Behavior, ViewProduct> transform() {
        return b -> {
            if(b == null)
                return Collections.emptyIterator();
            JSONArray fields = new JSONArray(b.getFields());
            ViewProduct e = new ViewProduct(b);
            for(int i=0; i<fields.length(); i+=1) {
                JSONObject jsonObject = fields.getJSONObject(i);
                Field f = ViewProduct.class.getDeclaredField(jsonObject.getString("key"));
                setField(f, e, jsonObject);
            }
            return Collections.singleton(e).iterator();
        };
    }
}
