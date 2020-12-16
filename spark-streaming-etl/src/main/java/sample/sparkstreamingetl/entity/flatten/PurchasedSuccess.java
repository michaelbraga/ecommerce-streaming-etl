package sample.sparkstreamingetl.entity.flatten;

import lombok.Data;
import sample.sparkstreamingetl.entity.Behavior;

@Data
public class PurchasedSuccess extends Behavior {
    private float total_amount;
    private String payment_type;
    private boolean used_promo;

    public PurchasedSuccess(Behavior b) {
        super(b);
    }
}
