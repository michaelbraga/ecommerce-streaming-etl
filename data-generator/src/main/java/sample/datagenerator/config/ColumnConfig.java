package sample.datagenerator.config;

import lombok.Data;

import java.util.List;

@Data
public class ColumnConfig {
    private String name;
    private String type;
    private List<String> values;

    public ColumnConfig() {};
    public ColumnConfig(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
