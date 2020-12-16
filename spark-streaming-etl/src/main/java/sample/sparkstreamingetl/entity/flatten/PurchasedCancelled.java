package sample.sparkstreamingetl.entity.flatten;

import lombok.Data;
import sample.sparkstreamingetl.entity.Behavior;

@Data
public class PurchasedCancelled extends Behavior {
    private float total_amount;
    private String payment_type;
    private String reason;
    private boolean used_promo;

    public PurchasedCancelled(Behavior b) {
        super(b);
    }
}
