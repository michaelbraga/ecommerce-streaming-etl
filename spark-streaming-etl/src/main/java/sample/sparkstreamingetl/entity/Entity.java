package sample.sparkstreamingetl.entity;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class Entity implements Serializable {
    // timestamp when data arrived in ETL
    protected Timestamp processed_at;
    // timestamp when data was saved in DB
    protected Timestamp dbed_at;
}
