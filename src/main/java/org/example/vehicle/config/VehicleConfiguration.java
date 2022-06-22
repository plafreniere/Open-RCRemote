package org.example.vehicle.config;

import lombok.Data;
import org.example.processors.PIDConfiguration;
import org.example.utils.ConfigurationStorage;
import org.example.vehicle.config.parameters.VehicleParameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class VehicleConfiguration extends ConfigurationStorage implements Serializable {
    private PIDConfiguration accelPID;
    private PIDConfiguration speedPID;
    private String name;
    private List<VehicleParameter> parameters = new ArrayList<>();
    private String filename;

    public VehicleConfiguration(String filename) {
        this.filename = filename;
        if(loadFromFile()) {
            System.out.println("Loaded vehicle config " + filename);
        } else {
            saveToFile();
            System.out.println("Created vehicle config " + filename);
        }
    }
    public VehicleConfiguration() {

    }
    public void addParameter(VehicleParameter param) {
        if(parameters.stream().noneMatch(value -> param.getName().equals(value.getName()))) {
            this.parameters.add(param);
            System.out.println("add: " + param);
        }
    }
    public VehicleParameter getParameter(String name) {
        Optional<VehicleParameter> param = parameters.stream().filter(value -> value.getName().equals(name)).findAny();
        return param.orElse(null);
    }

    public boolean loadFromFile() {
        Object obj =  loadFromFile(filename);
        if(obj == null) {
            return false;
        }
        this.copy((VehicleConfiguration) obj);
        return true;
    }

    public void saveToFile() {
        saveToFile(this, filename);
    }
    public void copy(VehicleConfiguration config) {
        this.accelPID = config.getAccelPID();
        this.speedPID = config.getSpeedPID();
        this.name = config.getName();
        this.filename = config.getFilename();
        this.parameters = new ArrayList<>();
        config.getParameters().forEach(parameter -> {
            VehicleParameter nParameter = new VehicleParameter();
            nParameter.copy(parameter);
            this.parameters.add(nParameter);
        });
    }

    public void removeParameter(String name) {
        VehicleParameter param = getParameter(name);
        parameters.remove(param);
    }
}
