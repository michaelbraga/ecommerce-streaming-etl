package sample.sparkstreamingetl.transformer;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.json.JSONArray;
import org.json.JSONObject;
import sample.sparkstreamingetl.entity.Behavior;
import sample.sparkstreamingetl.entity.EcommEvent;

import java.util.ArrayList;
import java.util.Collections;

public class EcommEventTransformer {
    public static FlatMapFunction<Behavior, EcommEvent> transform() {
        return b -> {
            if(b == null)
                return Collections.emptyIterator();
            ArrayList<EcommEvent> ecommEvents = new ArrayList<>();
            JSONArray fields = new JSONArray(b.getFields());
            for(int i=0; i<fields.length(); i+=1) {
                EcommEvent e = new EcommEvent(b);
                JSONObject jsonObject = fields.getJSONObject(i);
                e.setFieldKey(jsonObject.getString("key"));
                e.setFieldValue(jsonObject.getString("value"));
                ecommEvents.add(e);
            }
            return ecommEvents.iterator();
        };
    }
}
