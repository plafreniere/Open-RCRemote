package org.example.gui;

import org.example.processors.MiniPID;
import org.example.processors.PIDConfiguration;
import org.example.vehicle.Vehicle;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class PIDEditorGUI extends JFrame implements ActionListener {
    private final JButton applyBtn = new JButton("Apply");
    private final JButton cancelBtn = new JButton("Cancel");
    private final JButton acceptBtn = new JButton("Ok");
    private final PIDConfiguration accelConfig = new PIDConfiguration();
    private final PIDConfiguration speedConfig = new PIDConfiguration();
    private final Vehicle vehicle;

    public PIDEditorGUI(Vehicle vehicle) {
        setTitle("PID Editor");

        this.vehicle = vehicle;
        accelConfig.copy(vehicle.getAccelConfig());
        speedConfig.copy(vehicle.getSpeedConfig());

        acceptBtn.addActionListener(this);
        applyBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        JPanel accelPanel = new PIDPanel(accelConfig, "Acceleration PID");
        JPanel speedPanel = new PIDPanel(speedConfig, "Speed PID");

        JPanel pidPanel = new JPanel();
        pidPanel.add(accelPanel);
        pidPanel.add(speedPanel);
        pidPanel.setLayout(new FlowLayout());

        JPanel savePanel = new JPanel();
        savePanel.add(acceptBtn);
        savePanel.add(applyBtn);
        savePanel.add(cancelBtn);
        savePanel.setLayout(new FlowLayout());

        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(padding);
        contentPanel.add(pidPanel);
        contentPanel.add(savePanel);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));

        add(contentPanel);

        pack();
        setResizable(false);
        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == applyBtn)
            applySettings();
        if(e.getSource() == cancelBtn)
            closeWindow();
        if(e.getSource() == acceptBtn) {
            applySettings();
            closeWindow();
        }
    }

    private void applySettings() {
        vehicle.setAccelPID(accelConfig);
        vehicle.setSpeedPID(speedConfig);
        vehicle.getConfiguration().setAccelPID(accelConfig);
        vehicle.getConfiguration().setSpeedPID(speedConfig);
        vehicle.getConfiguration().saveToFile();
        accelConfig.saveToFile();
        speedConfig.saveToFile();
    }
    private void closeWindow() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        dispose();
    }
}
