package org.example.vehicle.config.parameters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleParameter implements Serializable {
    private String name;
    private String value;
    private VehicleParameterType type;
    public void copy(VehicleParameter params) {
        this.name = params.getName();
        this.value = params.getValue();
        this.type = params.getType();
    }

    public <T extends Object> VehicleParameter(String name, T value) {
        this.name = name;
        this.value = String.valueOf(value);
        switch (value.getClass().getName()) {
            case "java.lang.Float" :
            case "java.lang.Double" :
            case "java.lang.Integer" :
            case "java.lang.Long" :
                this.type = VehicleParameterType.NUMBER;
                break;
            case "java.lang.Boolean" :
                this.type = VehicleParameterType.BOOLEAN;
                break;
            default :
                this.type = VehicleParameterType.TEXT;
        }
    }

    public String getString() {
        return value;
    }
    public double toDouble() {
        return Double.parseDouble(value);
    }
    public boolean toBoolean() {
        return (value.equals("true"));
    }
}
