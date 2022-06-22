package org.example.gui;

import net.java.games.input.Controller;
import net.java.games.input.Event;
import org.example.input.Binding;
import org.example.input.ControllerMapping;
import org.example.input.Input;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ButtonsGUI extends JFrame implements ActionListener {
    private final JPanel buttonPanel = new JPanel();
    private final JButton refreshBtn = new JButton("Refresh");
    private final JButton applyBtn = new JButton("Apply");
    private final JButton cancelBtn = new JButton("Cancel");
    private final JButton acceptBtn = new JButton("Ok");
    private final JComboBox<String> controllersBox = new JComboBox<>();
    private int selectedControllerIndex = 0;
    private final Input input;


    private final ControllerMapping controllerMapping;

    public ButtonsGUI(Input input) {
        setTitle("Controllers settings");

        this.input = input;
        controllerMapping = new ControllerMapping(true);
        controllerMapping.copy(input.getControllerMapping());

        populateControllers();

        JPanel controllerPanel = new JPanel();
        controllerPanel.add(controllersBox);
        controllerPanel.add(refreshBtn);
        refreshBtn.addActionListener(this);

        acceptBtn.addActionListener(this);
        applyBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        controllerMapping.getBindings().forEach(binding -> buttonPanel.add(buttonConfig(binding)));

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));

        JPanel savePanel = new JPanel();
        savePanel.add(acceptBtn);
        savePanel.add(applyBtn);
        savePanel.add(cancelBtn);
        savePanel.setLayout(new FlowLayout());


        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(padding);
        contentPanel.add(controllerPanel);
        contentPanel.add(buttonPanel);
        contentPanel.add(savePanel);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));

        add(contentPanel);
        pack();
        setResizable(false);
        setVisible(true);
    }
    private JPanel buttonConfig(Binding binding) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton button = new JButton("Edit");
        JCheckBox inverted = new JCheckBox("Inverted");
        inverted.setSelected(binding.isInverted());
        inverted.addActionListener( new AbstractAction("add") {
            @Override
            public void actionPerformed( ActionEvent e ) {
                binding.setInverted(inverted.isSelected());
            }
        });

        JCheckBox negative = new JCheckBox("Negative");
        negative.setSelected(binding.isNegative());
        negative.addActionListener( new AbstractAction("add") {
            @Override
            public void actionPerformed( ActionEvent e ) {
                binding.setNegative(negative.isSelected());
            }
        });

        JLabel label = new JLabel(binding.getAction().substring(0,1).toUpperCase() + binding.getAction().substring(1));
        label.setPreferredSize(new Dimension(130,16));

        JTextField field = new JTextField(binding.getKey());
        field.setColumns(5);
        field.setEditable(false);

        button.addActionListener( new AbstractAction("add") {
            @Override
            public void actionPerformed( ActionEvent e ) {

                updateSelectedController();
                Controller controller = input.getControllers()[selectedControllerIndex];

                Event event = input.processInputs(controller);

                while(event.getComponent() == null) {
                    event = input.processInputs(controller);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                String key = event.getComponent().getName();
                field.setText(key);
                controllerMapping
                        .getBinding(binding.getAction())
                        .setKey(event.getComponent().getName());

            }
        });

        panel.add(label);
        panel.add(field);
        panel.add(button);
        panel.add(negative);
        panel.add(inverted);
        return panel;
    }

    private void populateControllers() {
        List<String> controllers = Arrays.stream(input.getControllers()).map(Controller::getName).collect(Collectors.toList());
        controllers.forEach(controllersBox::addItem);
        selectedControllerIndex = controllers.indexOf(input.getControllerMapping().getController());
        controllersBox.setSelectedIndex(selectedControllerIndex);
    }
    private void updateSelectedController() {
        selectedControllerIndex = controllersBox.getSelectedIndex();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==applyBtn)
            applySettings();
        if(e.getSource()==acceptBtn) {
            applySettings();
            closeWindow();
        }
        if(e.getSource() == cancelBtn)
            closeWindow();
        if(e.getSource() == refreshBtn) {
            refreshControllerList();
        }
    }
    private void refreshControllerList() {
        input.refreshControllers();
        controllersBox.removeAllItems();
        populateControllers();
    }

    private void applySettings() {
        String controllerStr = (String) controllersBox.getSelectedItem();
        input.getControllerMapping().setController(controllerStr);
        input.setSelectedController(input.findController(controllerStr));

        controllerMapping.getBindings().forEach(binding -> input.getControllerMapping().updateBinding(binding));

        input.getControllerMapping().saveToFile();
    }

    private void closeWindow() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        dispose();
    }
}
