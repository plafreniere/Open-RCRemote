package org.example.input;

import lombok.Data;

import java.io.Serializable;

@Data
public class Binding implements Serializable {
    private String action;
    private String key;
    private float value;

    private boolean inverted;

    private boolean negative;

    public void copy(Binding binding) {
        this.action = binding.getAction();
        this.key = binding.getKey();
        this.value = binding.getValue();
        this.inverted = binding.isInverted();
        this.negative = binding.isNegative();
    }
}
