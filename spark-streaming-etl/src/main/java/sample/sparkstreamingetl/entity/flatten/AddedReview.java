package sample.sparkstreamingetl.entity.flatten;

import lombok.Data;
import sample.sparkstreamingetl.entity.Behavior;

@Data
public class AddedReview extends Behavior {
    private String product_id;
    private int rating;
    private String comment;

    public AddedReview(Behavior b) {
        super(b);
    }
}
