package sample.sparkstreamingetl.entity;

import lombok.Data;

@Data
public class EcommEvent extends Behavior {
    protected String fieldKey;
    protected String fieldValue;

    public EcommEvent(Behavior b) {
        super(b);
    }
}
