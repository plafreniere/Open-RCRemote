package org.example.gui;

import org.example.vehicle.Vehicle;
import org.example.vehicle.config.VehicleConfiguration;
import org.example.vehicle.config.parameters.VehicleParameter;
import org.example.vehicle.config.parameters.VehicleParameterType;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleGUI extends JFrame implements ActionListener {
    private final JButton applyBtn = new JButton("Apply");
    private final JButton cancelBtn = new JButton("Cancel");
    private final JButton acceptBtn = new JButton("Ok");

    private final JMenuItem configImport = new JMenuItem("Import");
    private final JMenuItem configExport = new JMenuItem("Export");
    private final VehicleConfiguration configuration = new VehicleConfiguration();
    private final JFileChooser fc = new JFileChooser();
    private final Vehicle vehicle;

    private final List<ParamPanel> paramPanels = new ArrayList<>();

    private final JComboBox<VehicleParameterType> typeParam = new JComboBox<>();
    private final JTextField paramName = new JTextField();
    private final JTextField paramString = new JTextField();
    private final JComboBox<String> paramBoolean = new JComboBox<>();
    private final JSpinner paramNumber = new JSpinner();
    private final JPanel paramPanel = new JPanel();

    public VehicleGUI(Vehicle vehicle) {
        this.vehicle = vehicle;
        configuration.copy(vehicle.getConfiguration());
        JMenuBar mb = new JMenuBar();
        JMenu fileMenu = new JMenu("File");


        fileMenu.add(configImport);
        fileMenu.add(configExport);

        mb.add(fileMenu);
        setJMenuBar(mb);

        setTitle("Vehicle Configuration [" + configuration.getName() + "]");
        JPanel contentPanel = new JPanel();

        JPanel savePanel = new JPanel();
        JPanel newParamPanel = new JPanel();

        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Parameters");

        paramPanel.setBorder(border);
        paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setBorder(padding);
        Box box = Box.createHorizontalBox();
        JButton deleteParam = new JButton("Delete");
        box.add(deleteParam);
        box.add(Box.createGlue());
        box.add(newParamPanel);
        paramPanel.add(box);

        configuration.getParameters().forEach(param -> {
            ParamPanel panel = new ParamPanel(param);
            paramPanel.add(panel);
            paramPanels.add(panel);
        });

        newParamPanel.setLayout(new FlowLayout());

        for (VehicleParameterType type : VehicleParameterType.values()) {
            typeParam.addItem(type);
        }
        paramNumber.setVisible(false);
        paramName.setPreferredSize(new Dimension(20,20));
        paramString.setVisible(false);
        paramString.setColumns(10);
        paramBoolean.setVisible(false);
        paramBoolean.addItem("True");
        paramBoolean.addItem("False");
        paramBoolean.setMaximumSize(new Dimension(40, 20));
        typeParam.addActionListener(e -> {
            VehicleParameterType type = (VehicleParameterType) typeParam.getSelectedItem();
            if (type != null) {
                if (type == VehicleParameterType.BOOLEAN) {
                    paramNumber.setVisible(false);
                    paramString.setVisible(false);
                    paramBoolean.setVisible(true);
                } else if (type == VehicleParameterType.NUMBER) {
                    paramNumber.setVisible(true);
                    paramString.setVisible(false);
                    paramBoolean.setVisible(false);
                } else if (type == VehicleParameterType.TEXT) {
                    paramNumber.setVisible(false);
                    paramString.setVisible(true);
                    paramBoolean.setVisible(false);
                }
            }
        });
        paramName.setPreferredSize(new Dimension(40, 22));
        border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Add parameter");
        newParamPanel.setBorder(border);
        newParamPanel.add(paramName);
        newParamPanel.add(typeParam);
        newParamPanel.add(paramNumber);
        newParamPanel.add(paramBoolean);
        newParamPanel.add(paramString);
        JButton addParam = new JButton("Add");
        newParamPanel.add(addParam);
        addParam.addActionListener(e -> {
            if(typeParam.getSelectedItem() == VehicleParameterType.BOOLEAN) {
                VehicleParameter newParam = new VehicleParameter(
                        paramName.getText(),
                        (paramBoolean.getSelectedIndex() == 0) ? "true" : "false",
                        VehicleParameterType.BOOLEAN);
                addParamPanel(newParam);
            }
            if(typeParam.getSelectedItem() == VehicleParameterType.NUMBER) {
                addParamPanel(new VehicleParameter(paramName.getText(),
                        paramNumber.getValue().toString(),
                        VehicleParameterType.NUMBER));
            }
            if(typeParam.getSelectedItem() == VehicleParameterType.TEXT) {
                addParamPanel(new VehicleParameter(paramName.getText(),
                        paramString.getText(),
                        VehicleParameterType.TEXT));
            }
        });


        deleteParam.addActionListener(e -> paramPanels.forEach(panel -> {
            if(panel.deleteButton.isSelected()) {
                removeParamPanel(panel);
            }
        }));


        acceptBtn.addActionListener(this);
        applyBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        configExport.addActionListener(this);
        configImport.addActionListener(this);

        savePanel.add(acceptBtn);
        savePanel.add(applyBtn);
        savePanel.add(cancelBtn);
        savePanel.setLayout(new FlowLayout());

        contentPanel.add(paramPanel);
        contentPanel.add(savePanel);

        add(contentPanel);
        pack();
        //setResizable(false);
        setVisible(true);
    }

    private void removeParamPanel(ParamPanel panel) {
        panel.setVisible(false);
        panel.deleteButton.setSelected(false);
        configuration.removeParameter(panel.param.getName());
    }

    private void addParamPanel(VehicleParameter param) {
        ParamPanel newPanel = new ParamPanel(param);
        paramPanels.add(newPanel);
        paramPanel.add(newPanel);
        configuration.addParameter(param);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == acceptBtn) {
            apply();
            close();
        }
        if (e.getSource() == cancelBtn) {
            close();
        }
        if (e.getSource() == applyBtn) {
            apply();
        }
        if (e.getSource() == configImport) {
            importFile();
        }
        if (e.getSource() == configExport) {
            exportFile();
        }
    }

    public void importFile() {
        int state = fc.showOpenDialog(this);

        if (state == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                configuration.copy((VehicleConfiguration) ois.readObject());
                configuration.saveToFile();
                System.out.println(configuration);
            } catch (IOException | ClassNotFoundException ex) {
                //ex.printStackTrace();
                System.out.println("File incompatible : " + file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "File not compatible.");
            }
        }
    }

    public void exportFile() {
        int state = fc.showSaveDialog(this);
        System.out.println("Path : " + fc.getSelectedFile().getPath());
        if (state == JFileChooser.APPROVE_OPTION) {
            try (FileOutputStream fos = new FileOutputStream(fc.getSelectedFile().getPath() + ".vehicle");
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(configuration);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void apply() {
        vehicle.setConfiguration(configuration);
        configuration.saveToFile();
    }

    public void close() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        dispose();
    }
}

class ParamPanel extends JPanel {
    final VehicleParameter param;
    JCheckBox deleteButton = new JCheckBox();
    public ParamPanel(VehicleParameter param) {
        this.param = param;
        Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        setBorder(loweredEtched);
        JLabel label = new JLabel(firstCapital(param.getName()));


        //label.setPreferredSize(new Dimension(70,16));
        setLayout(new BorderLayout());
        Box box = Box.createHorizontalBox();
        box.add(deleteButton);
        box.add(label);
        box.add(Box.createGlue());


        if (param.getType() == VehicleParameterType.BOOLEAN) {
            JComboBox<String> cbox = new JComboBox<>();
            cbox.addItem("True");
            cbox.addItem("False");
            cbox.setMaximumSize(new Dimension(40, 20));
            cbox.setSelectedIndex(param.toBoolean() ? 0 : 1);
            cbox.addActionListener(e -> param.setValue((cbox.getSelectedIndex() == 0) ? "true" : "false"));
            box.add(cbox);
        }
        if (param.getType() == VehicleParameterType.NUMBER) {
            JSpinner spinner = new JSpinner();
            spinner.setValue(param.toDouble());
            spinner.setMaximumSize(new Dimension(40, 20));
            spinner.setPreferredSize(new Dimension(40, 20));
            spinner.addChangeListener(e -> param.setValue(spinner.getValue().toString()));
            box.add(spinner);
        }
        if (param.getType() == VehicleParameterType.TEXT) {
            JTextField field = new JTextField();
            field.setText(param.getString());
            field.setColumns(10);
            field.setMaximumSize(new Dimension(40, 20));
            field.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    param.setValue(field.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    param.setValue(field.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {

                }
            });
            box.add(field);
        }
        add(box);
    }

    public static String firstCapital(String str) {
        String out = str.toLowerCase();
        out = out.substring(0, 1).toUpperCase() + out.substring(1);
        return out;
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
