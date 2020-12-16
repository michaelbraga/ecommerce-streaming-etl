package sample.sparkstreamingetl.transformer;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.json.JSONObject;
import sample.sparkstreamingetl.entity.Behavior;

import java.lang.reflect.Field;

public abstract class FlattenedTransformer {
    protected abstract <T> FlatMapFunction<Behavior, T> transform();
    protected void setField(Field f, Object e, JSONObject jsonObject) throws IllegalAccessException {
        f.setAccessible(true);
        if(f.getType() == String.class)
            f.set(e, jsonObject.getString("value"));
        else if(f.getType() == int.class)
            f.set(e, jsonObject.getInt("value"));
        else if(f.getType() == float.class)
            f.set(e, (float) jsonObject.getDouble("value"));
        else if(f.getType() == boolean.class)
            f.set(e, jsonObject.getBoolean("value"));
    }
}
