package org.example.input;

import lombok.Data;
import net.java.games.input.Controller;
import org.example.utils.ConfigurationStorage;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Data
public class ControllerMapping extends ConfigurationStorage implements Serializable {
    //            private static final long serialVersionUID = 7L;
    private static final String filename = "mapping.conf";

    private List<Binding> bindings = new ArrayList<>();

    private String controller;

    public ControllerMapping() {
        if (loadFromFile()) {
            System.out.println("Loaded controller mappings");
        } else {
            addAction("throttle");
            addAction("brake");
            addAction("direction");
            addAction("left");
            addAction("right");

            addAction("headlights");
            saveToFile();
            System.out.println("Created controller mappings");
        }
    }

    public ControllerMapping(boolean temporary) {

    }

    public boolean loadFromFile() {
        ControllerMapping controllerMapping = (ControllerMapping) loadFromFile(filename);
        if (controllerMapping == null) {
            return false;
        }
        this.copy(controllerMapping);
        return true;
    }

    public void saveToFile() {
        saveToFile(this, filename);
    }

    public void copy(ControllerMapping controllerMapping) {
        this.bindings = new ArrayList<>();
        controllerMapping.getBindings().stream().forEach(binding -> {
            Binding nBinding = new Binding();
            nBinding.copy(binding);
            this.bindings.add(nBinding);
        });
        this.controller = controllerMapping.controller;
    }

    public void addAction(String action) {
        Binding binding = new Binding();
        binding.setAction(action);
        binding.setKey("");
        bindings.add(binding);
    }

    public Binding getBinding(String action) {
        Optional<Binding> binding = bindings
                .stream()
                .filter(bin -> bin.getAction().equals(action))
                .findFirst();
        return binding.orElse(null);
    }

    public void updateBinding(Binding bin) {
        Binding binding = getBinding(bin.getAction());
        binding.copy(bin);
    }

}
