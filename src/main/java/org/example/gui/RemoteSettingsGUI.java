package org.example.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class RemoteSettingsGUI extends JFrame {

    private final JButton save = new JButton("Ok");
    private final JButton cancel = new JButton("Cancel");
    private final JButton apply = new JButton("Apply");
    private final JLabel ipLabel = new JLabel("IP");
    private final JLabel portLabel = new JLabel("Port");
    private final JTextField ip = new JTextField(10);
    private final JTextField port = new JTextField(10);
    private final JLabel type = new JLabel("Connection protocol");
    private final JRadioButton internet = new JRadioButton("Internet");
    private final JRadioButton serial = new JRadioButton("Serial");
    private final JPanel contentPanel = new JPanel();
    private final JPanel buttonsPanel = new JPanel(new FlowLayout());

    public RemoteSettingsGUI() {
        setTitle("Remote configuration");

        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));

        fieldPanel.add(ipLabel);
        fieldPanel.add(ip);
        fieldPanel.add(portLabel);
        fieldPanel.add(port);
        fieldPanel.add(type);
        fieldPanel.add(internet);
        fieldPanel.add(serial);
        buttonsPanel.add(apply);
        buttonsPanel.add(save);
        buttonsPanel.add(cancel);


        contentPanel.add(fieldPanel);
        contentPanel.add(buttonsPanel);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        setSize(300,250);
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);

        contentPanel.setBorder(padding);
        setContentPane(contentPanel);
        //frame.pack();
        setResizable(false);
        setVisible(true);
    }
}
