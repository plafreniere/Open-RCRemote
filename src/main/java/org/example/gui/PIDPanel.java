package org.example.gui;

import org.example.processors.PIDConfiguration;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Arrays;

public class PIDPanel extends JPanel {
    private final PIDConfiguration pidConfiguration;

    public PIDPanel(PIDConfiguration pidConfiguration, String title) {
        this.pidConfiguration = pidConfiguration;

        TitledBorder border;
        border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), title);
        setBorder(border);

        add(parameter("P", pidConfiguration.getP()));
        add(parameter("I", pidConfiguration.getI()));
        add(parameter("D", pidConfiguration.getD()));
        add(parameter("F", pidConfiguration.getF()));
        add(parameter("Setpoint", pidConfiguration.getSetPoint()));
        add(parameter("Setpoint range", pidConfiguration.getSetPointRange()));
        add(parameter("Output filter", pidConfiguration.getOutputFilter()));
        add(parameter("Max I", pidConfiguration.getMaxIOutput()));
        add(parameter("Ramp rate", pidConfiguration.getOutputRampRate()));

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    private JPanel parameter(String name, double value) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel label = new JLabel(name);
        label.setPreferredSize(new Dimension(90, 16));

        JTextField field = new JFormattedTextField();
        field.setColumns(7);
        field.setText(String.valueOf(value));
        field.setActionCommand(name);
        field.setName(name);
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }
            public void removeUpdate(DocumentEvent e) {
                propertyChange(field);
            }
            public void insertUpdate(DocumentEvent e) {
                propertyChange(field);
            }
        });

        panel.add(field);
        panel.add(label);

        return panel;
    }

    public void propertyChange(JTextField field) {
        String text = field.getText();
        if(!isNumeric(text)) {
            return;
        }
        double value = Double.parseDouble(text);
        if(Double.isNaN(value)) {
            return;
        }
        String name = field.getName();

        if(name.equals("P"))
            pidConfiguration.setP(value);
        if(name.equals("I"))
            pidConfiguration.setI(value);
        if(name.equals("D"))
            pidConfiguration.setD(value);
        if(name.equals("F"))
            pidConfiguration.setF(value);
        if(name.equals("Setpoint"))
            pidConfiguration.setSetPoint(value);
        if(name.equals("Setpoint range"))
            pidConfiguration.setSetPointRange(value);
        if(name.equals("Output filter"))
            pidConfiguration.setOutputFilter(value);
        if(name.equals("Max I"))
            pidConfiguration.setMaxIOutput(value);
        if(name.equals("Ramp rate"))
            pidConfiguration.setOutputRampRate(value);
    }
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
