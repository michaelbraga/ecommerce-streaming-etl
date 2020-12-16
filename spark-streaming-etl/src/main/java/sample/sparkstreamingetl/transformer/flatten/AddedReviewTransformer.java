package sample.sparkstreamingetl.transformer.flatten;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.json.JSONArray;
import org.json.JSONObject;
import sample.sparkstreamingetl.entity.Behavior;
import sample.sparkstreamingetl.entity.flatten.AddedReview;
import sample.sparkstreamingetl.transformer.FlattenedTransformer;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;

public class AddedReviewTransformer extends FlattenedTransformer implements Serializable {
    final static Class entityClass = AddedReview.class;
    final static String event_type = "ADDED_REVIEW";

    public FlatMapFunction<Behavior, AddedReview> transform() {
        return b -> {
            if(b == null)
                return Collections.emptyIterator();
            JSONArray fields = new JSONArray(b.getFields());
            AddedReview e = new AddedReview(b);
            for(int i=0; i<fields.length(); i+=1) {
                JSONObject jsonObject = fields.getJSONObject(i);
                Field f = AddedReview.class.getDeclaredField(jsonObject.getString("key"));
                setField(f, e, jsonObject);
            }
            return Collections.singleton(e).iterator();
        };
    }
}
