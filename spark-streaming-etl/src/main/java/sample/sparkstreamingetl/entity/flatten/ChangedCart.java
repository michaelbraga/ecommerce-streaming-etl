package sample.sparkstreamingetl.entity.flatten;

import lombok.Data;
import sample.sparkstreamingetl.entity.Behavior;

@Data
public class ChangedCart extends Behavior {
    private String action;
    private String product_id;
    private int new_total_items;

    public ChangedCart(Behavior b) {
        super(b);
    }
}
