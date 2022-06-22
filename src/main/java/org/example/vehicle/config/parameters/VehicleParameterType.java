package org.example.vehicle.config.parameters;

public enum VehicleParameterType {
    TEXT("Text"), NUMBER("Number"), BOOLEAN("Boolean");

    final String name;

    VehicleParameterType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
