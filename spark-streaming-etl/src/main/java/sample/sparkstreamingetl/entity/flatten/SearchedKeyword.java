package sample.sparkstreamingetl.entity.flatten;

import lombok.Data;
import sample.sparkstreamingetl.entity.Behavior;

@Data
public class SearchedKeyword extends Behavior {
    private String keyword;
    private int result_count;

    public SearchedKeyword(Behavior b) {
        super(b);
    }
}
