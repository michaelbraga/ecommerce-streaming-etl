package sample.sparkstreamingetl.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Behavior extends Entity {
    protected Timestamp timestamp;
    protected String date;
    protected String country;
    protected String event_type;
    protected String session_id;
    protected String user_id;
    protected String language;
    protected String fields;

    public Behavior() {}

    public Behavior(Behavior b) {
        this.processed_at = b.getProcessed_at();
        this.dbed_at = b.getDbed_at();
        this.timestamp = b.getTimestamp();
        this.date = b.getDate();
        this.country = b.getCountry();
        this.event_type = b.getEvent_type();
        this.session_id = b.getSession_id();
        this.user_id = b.getUser_id();
        this.language = b.getLanguage();
    }
}
