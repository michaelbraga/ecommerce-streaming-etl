package sample.sparkstreamingetl.entity.flatten;

import lombok.Data;
import sample.sparkstreamingetl.entity.Behavior;

@Data
public class PurchasedError extends Behavior {
    private float total_amount;
    private String payment_type;
    private String error_type;
    private String error_message;
    private boolean used_promo;

    public PurchasedError(Behavior b) {
        super(b);
    }
}
