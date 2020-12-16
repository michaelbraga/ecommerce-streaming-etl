package sample.datagenerator.component;

import com.github.javafaker.Faker;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sample.datagenerator.config.ColumnConfig;
import sample.datagenerator.config.RandomizerConfig;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class JsonRandomizerService {
    @Autowired
    private RandomizerConfig randomizerConfig;
    // track and save users
    private List<String> userList = new ArrayList<>();
    // track and save products
    private List<String> productList = new ArrayList<>();
    private Faker faker = new Faker();

    public String generateData() {
        JSONObject jsonObject = new JSONObject();
        for (ColumnConfig c: randomizerConfig.getRandomDataSchema()){
                jsonObject.put(c.getName(), String.valueOf(generateRandomValue(c)));
        }
        randomizeFields(jsonObject);
        return jsonObject.toString();
    }

    private void randomizeFields(JSONObject jsonObject) {
        Map<String, String> fields = randomizerConfig.getEventTypeFields().get(jsonObject.getString("event_type"));
        JSONArray fieldsArray = new JSONArray();
        fields.forEach((name, type) -> {
            JSONObject j = new JSONObject();
            j.put("key", name);
            j.put("value", generateRandomValue(new ColumnConfig(name, type)));
            fieldsArray.put(j);
        });
        jsonObject.put("fields", fieldsArray);
    }

    private Object generateRandomValue(ColumnConfig c) {
        if("timestamp".equalsIgnoreCase(c.getType())){
            return Timestamp.from(Instant.now()).toString();
        }
        else if ("error_type".equalsIgnoreCase(c.getName())) {
            return faker.code().isbn10();
        }
        else if ("keyword".equalsIgnoreCase(c.getName())) {
            return faker.commerce().productName();
        }
        else if ("action".equalsIgnoreCase(c.getName())) {
            return faker.number().randomNumber() % 2 == 0? "added" : "removed";
        }
        else if("payment_type".equalsIgnoreCase(c.getName())) {
            return faker.business().creditCardType();
        }
        else if ("rating".equalsIgnoreCase(c.getName())) {
            return faker.number().numberBetween(1, 5);
        }
        else if ("country".equalsIgnoreCase(c.getName())){
            return faker.country().countryCode2().toUpperCase();
        }
        else if ("language".equalsIgnoreCase(c.getName())){
            return faker.address().countryCode();
        }
        else if("id".equalsIgnoreCase(c.getType())){
            String id = UUID.randomUUID().toString();
            if("user_id".equalsIgnoreCase(c.getName())) {
                if(userList.size() < randomizerConfig.getTotalUsers()) {
                    // add new user
                    userList.add(id);
                } else {
                    // get random user from the user list
                    id = userList.get((int) (faker.number().randomNumber() % userList.size()));
                }
            }
            else if("product_id".equalsIgnoreCase(c.getName())) {
                if(productList.size() < randomizerConfig.getTotalUsers()) {
                    // add new product
                    productList.add(id);
                } else {
                    // get random user from the product list
                    id = productList.get((int) (faker.number().randomNumber() % productList.size()));
                }
            }
            return id;
        }
        else if("enum".equalsIgnoreCase(c.getType())){
            return c.getValues() == null || c.getValues().isEmpty() ?
                    null : c.getValues().get((int) (faker.number().randomNumber() % c.getValues().size()));
        }
        else if ("string".equalsIgnoreCase(c.getType())) {
            return String.join(" ", faker.lorem().words(3));
        }
        else if ("float".equalsIgnoreCase(c.getType())) {
            return faker.commerce().price();
        }
        else if ("int".equalsIgnoreCase(c.getType())) {
            return faker.number().numberBetween(0, 14);
        }
        else if ("boolean".equalsIgnoreCase(c.getType())) {
            return faker.bool().bool();
        }
        return null;
    }

    public List<String> generateBulkData() {
        int items = faker.number().numberBetween(8, 15);
        List<String> dataList = new ArrayList<>();
        for(int i=0; i<items; i+=1){
            dataList.add(generateData());
        }
        return dataList;
    }
}
