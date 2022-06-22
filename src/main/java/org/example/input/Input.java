package org.example.input;

import net.java.games.input.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Input {
    private Controller[] controllers;

    private Controller selectedController;
    private final ControllerMapping controllerMapping;


    public Input() {
        controllerMapping = new ControllerMapping();

        refreshControllers();

        if (controllerMapping.getController() != null) {
            Optional<Controller> found = Arrays.stream(controllers)
                    .filter(controller -> controller.getName().equals(controllerMapping.getController()))
                    .findFirst();
            if (found.isPresent()) {
                System.out.println("Previous controller found : " + found.get().getName());
                selectedController = found.get();
                return;
            }
        }


        if (controllers.length == 0) {
            System.out.println("Found no controllers.");
            return;
        }
        if (selectByType(Controller.Type.WHEEL)) {
            return;
        }
        if (selectByType(Controller.Type.GAMEPAD)) {
            return;
        }
        if (selectByType(Controller.Type.STICK)) {
            return;
        }
        if (selectByType(Controller.Type.MOUSE)) {
            return;
        }

        System.out.println("Found no suitable controller");
        System.exit(0);
    }

    public void processInputs() {
        processInputs(selectedController);
    }

    public Event processInputs(Controller controller) {
        controller.poll();

        EventQueue queue = controller.getEventQueue();
        Event event = new Event();
        while (queue.getNextEvent(event)) {
            Component comp = event.getComponent();
            controllerMapping.getBindings().forEach(binding -> {
                if (comp.getName().equals(binding.getKey())) {
                    float value = event.getValue();
                    if(!binding.isNegative())
                        value = (value + 1) / 2;
                    if(binding.isInverted())
                        value = 1 - value;
                    binding.setValue(value);
                }
            });

        }
        return event;
    }

    public void refreshControllers() {
        DirectAndRawInputEnvironmentPlugin directEnv = new DirectAndRawInputEnvironmentPlugin();
        controllers = Stream.of(directEnv.getControllers())
                .filter(controller -> controller.getType().equals(Controller.Type.WHEEL) ||
                        controller.getType().equals(Controller.Type.GAMEPAD) ||
                        controller.getType().equals(Controller.Type.MOUSE) ||
                        controller.getType().equals(Controller.Type.STICK) ||
                        controller.getType().equals(Controller.Type.KEYBOARD)).toArray(Controller[]::new);
    }

    boolean selectByType(Controller.Type type) {
        List<Controller> list = Stream.of(controllers)
                .filter(controller -> controller.getType().equals(type))
                .collect(Collectors.toList());

        if (list.size() > 0) {
            selectedController = list.get(0);
            controllerMapping.setController(selectedController.getName());
            controllerMapping.saveToFile();
            return true;
        }
        return false;
    }

    public Controller[] getControllers() {
        return controllers;
    }

    public Binding getBinding(String action) {
        return controllerMapping.getBinding(action);
    }

    public void setSelectedController(Controller selectedController) {
        this.selectedController = selectedController;
    }

    public ControllerMapping getControllerMapping() {
        return controllerMapping;
    }
    public Controller findController(String controllerStr) {
        if (controllerMapping.getController() != null) {
            Optional<Controller> found = Arrays.stream(controllers)
                    .filter(controller -> controller.getName().equals(controllerStr))
                    .findFirst();
            if (found.isPresent()) {
                System.out.println("Previous controller found : " + found.get().getName());
                return found.get();
            }
        }
        return null;
    }
}
