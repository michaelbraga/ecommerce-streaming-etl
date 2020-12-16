package sample.sparkstreamingetl.entity.flatten;

import lombok.Data;
import sample.sparkstreamingetl.entity.Behavior;

@Data
public class ViewProduct extends Behavior {
    private String product_id;

    public ViewProduct(Behavior b) {
        super(b);
    }
}
