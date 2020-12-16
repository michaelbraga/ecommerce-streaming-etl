package sample.sparkstreamingetl.entity.flatten;

import lombok.Data;
import sample.sparkstreamingetl.entity.Behavior;

@Data
public class CheckedReviews extends Behavior {
    private String product_id;
    private int total_reviews;

    public CheckedReviews(Behavior b) {
        super(b);
    }
}
