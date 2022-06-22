package org.example.gui;

import org.example.communication.RemoteCommunication;
import org.example.input.Input;
import org.example.processors.MotionProcessor;
import org.example.vehicle.Vehicle;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class MainWindow extends JFrame implements Runnable, ActionListener {
    private final RemoteCommunication remoteCommunication;
    private final MotionProcessor motionProcessor;
    private final Input input;
    private final Vehicle vehicle;
    private final JLabel picLabel = new JLabel();
    private final JPanel panel = new JPanel();
    private final JLabel speed = new JLabel("Speed");
    private final JProgressBar commanded = new JProgressBar(JProgressBar.VERTICAL);
    private final JProgressBar accel = new JProgressBar(JProgressBar.VERTICAL);
    private JMenu settingsMenu, chartMenu, remoteMenu;
    private JMenuItem controllersSettings, torqueCurve, pidSettings,
            vehicleSettings, remoteConnect, remoteDisconnect, remoteConfig;
    private boolean closed = false;
    public MainWindow(RemoteCommunication remoteCommunication, MotionProcessor motionProcessor, Vehicle vehicle, Input input) {
        this.remoteCommunication = remoteCommunication;
        this.motionProcessor = motionProcessor;
        this.vehicle = vehicle;
        this.input = input;

        createMenu();
        setTitle("RC car");

        FlowLayout layout = new FlowLayout();
        layout.setVgap(0);
        setLayout(layout);


        //setLocationRelativeTo();
        //setDefaultCloseOperation(); //JFrame.EXIT_ON_CLOSE
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closed = true;
                System.exit(1);
            }
        });

        LayoutManager overlay = new OverlayLayout(panel);
        panel.setLayout(overlay);

        speed.setForeground(Color.darkGray);
        speed.setFont(new Font("SansSerif", Font.BOLD, 16));
        speed.setAlignmentX(-1.0f);
        speed.setAlignmentY(0.5f);
        picLabel.setAlignmentY(0.5f);
        commanded.setMaximum(255);
        commanded.setMinimum(0);
        commanded.setValue(0);
        accel.setMaximum(1000);
        accel.setMinimum(0);
        accel.setValue(0);
        //accel.setStringPainted(true);
        accel.setForeground(Color.red);
        panel.add(speed);
        panel.add(picLabel);
        add(accel);
        add(commanded);
        add(panel);

        setSize(800, 600);
        centreWindow(this);
        setVisible(true);
        if(remoteCommunication.isVideoCompatible()) {
            new Thread(this).start();
        }
    }
    public static void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    private void createMenu() {
        JMenuBar mb = new JMenuBar();

        settingsMenu = new JMenu("Settings");
        chartMenu = new JMenu("Charts");
        remoteMenu = new JMenu("Remote");

        controllersSettings = new JMenuItem("Controllers settings");
        controllersSettings.addActionListener(this);
        torqueCurve = new JMenuItem("Torque curve");
        torqueCurve.addActionListener(this);
        pidSettings = new JMenuItem("PID configuration");
        pidSettings.addActionListener(this);
        vehicleSettings = new JMenuItem("Vehicle Settings");
        vehicleSettings.addActionListener(this);

        remoteConnect = new JMenuItem("Connect");
        remoteConnect.addActionListener(this);
        remoteDisconnect = new JMenuItem("Disconnect");
        remoteDisconnect.addActionListener(this);
        remoteConfig = new JMenuItem("Configuration");
        remoteConfig.addActionListener(this);


        settingsMenu.add(controllersSettings);
        settingsMenu.add(pidSettings);
        settingsMenu.add(vehicleSettings);

        remoteMenu.add(remoteConnect);
        remoteMenu.add(remoteDisconnect);
        remoteMenu.add(remoteConfig);

        chartMenu.add(torqueCurve);

        mb.add(settingsMenu);
        mb.add(remoteMenu);
        mb.add(chartMenu);

        setJMenuBar(mb);
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==remoteConnect)
            remoteConnect();
        if(e.getSource()==remoteDisconnect)
            remoteDisconnect();
        if(e.getSource()==remoteConfig)
            remoteConfig();
        if(e.getSource()==torqueCurve) {
            XYDataset dataset = GraphWindow.convertAccelCurveToDataset(vehicle.getTorqueCurve());
            GraphWindow.show("Torque", dataset, "rpm");
        }
        if(e.getSource()==controllersSettings)
            controllerConfig();
        if(e.getSource()==pidSettings)
            pidSettings();
        if(e.getSource() == vehicleSettings) {
            vehicleSettings();
        }
    }
    private void vehicleSettings() {
        new VehicleGUI(vehicle);
    }
    private void remoteConnect() {
        if(!remoteCommunication.isConnected()) {
            if(!remoteCommunication.connect()) {
                JOptionPane.showMessageDialog(this,"Couldn't connect.");
            }
            new Thread(vehicle).start();
        }

    }

    private void pidSettings() {
        new PIDEditorGUI(vehicle);
    }
    private void remoteDisconnect() {
        if(remoteCommunication.isConnected()) {
            vehicle.stop();
            if(remoteCommunication.disconnect()) {
                JOptionPane.showMessageDialog(this,"Couldn't disconnect.");
            }
        }

    }
    private void remoteConfig() {
        new RemoteSettingsGUI();
    }

    private void controllerConfig() {
        new ButtonsGUI(input);
    }

    public void run() {
        DecimalFormat df = new DecimalFormat("0.00");
        while(!closed) {
            BufferedImage originalImage = (BufferedImage) remoteCommunication.receiveVideoFrame();
            float originalRatio = (float) originalImage.getWidth() / (float) originalImage.getHeight();

            Dimension actualSize = getContentPane().getSize();
            float screenRatio = (float) actualSize.getWidth() / (float) actualSize.getHeight();
            Image image;
            if(screenRatio < originalRatio) {
                image = originalImage
                        .getScaledInstance(
                                (int) actualSize.getWidth(),
                                (int) (actualSize.getWidth() / originalRatio),
                                Image.SCALE_SMOOTH);
            } else {
                image = originalImage
                        .getScaledInstance(
                                (int) (actualSize.getHeight() * originalRatio),
                                (int) actualSize.getHeight(),
                                Image.SCALE_SMOOTH);
            }
            picLabel.setIcon(new ImageIcon(image));
            accel.setValue((int) absolute(motionProcessor.getAccel().getX() * 100));
            commanded.setValue((int)vehicle.getEngineRequestedPower());
            speed.setText("Speed " + df.format(motionProcessor.getSpeed().getX()));
            //speed.setText(Float.toString(vehicle.getEngineRequestedPower()));
            while(!remoteCommunication.newFrameAvailable()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    private float absolute(float value) {
        if(value < 0) {
            value = value * -1;
        }
        return value;
    }
}
