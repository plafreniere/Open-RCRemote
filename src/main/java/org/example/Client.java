package org.example;


import org.example.communication.FakeRemoteCommunication;
import org.example.communication.RemoteCommunication;
import org.example.distanceAcquisition.DistanceAcquisitionImpl;
import org.example.gui.MainWindow;
import org.example.gui.VehicleGUI;
import org.example.input.Input;
import org.example.processors.MotionProcessor;
import org.example.processors.PIDConfiguration;
import org.example.utils.BasicTorqueCurve;
import org.example.utils.TorqueCurve;
import org.example.vehicle.Vehicle;
import org.example.vehicle.config.VehicleConfiguration;
import org.example.vehicle.config.parameters.VehicleParameter;

public class Client {

    public static void main(String[] args) {


        VehicleConfiguration vconf = new VehicleConfiguration("vehicle.conf");
        System.out.println(vconf);
        //createBasicVehicleConfig();

        PIDConfiguration accelConfig = vconf.getAccelPID();
        PIDConfiguration speedConfig = vconf.getSpeedPID();

        Input input = new Input();
        Vehicle car = new Vehicle();


        TorqueCurve tc = BasicTorqueCurve.getCurve();
        RemoteCommunication remote = new FakeRemoteCommunication();
        MotionProcessor motionProcessor = new MotionProcessor(new DistanceAcquisitionImpl(remote));
        remote.setMotionProcessor(motionProcessor);
        remote.setBrakeBinding(input.getBinding("brake"));

        car.setInput(input);
        car.setRefreshRate(5); // default 10
        car.setMotionProcessor(motionProcessor);
        car.setRemoteCommunication(remote);
        //car.setMaxTorque(30);
        car.setTorqueCurve(tc);
        car.setConfiguration(vconf);


        car.setAccelPID(accelConfig);
        car.setSpeedPID(speedConfig);

        new MainWindow(remote, motionProcessor, car, input);
    }

    static void createBasicVehicleConfig() {
        VehicleConfiguration vconf = new VehicleConfiguration("vehicle.conf");
//        PIDConfiguration accelConfig = new PIDConfiguration(0.2, 0, 0.001);
        PIDConfiguration accelConfig = new PIDConfiguration("accel.pid");
//        //PIDConfiguration speedConfig = new PIDConfiguration(0.2, 0.01, 0.001);
        PIDConfiguration speedConfig = new PIDConfiguration("speed.pid");
        vconf.setAccelPID(accelConfig);
        vconf.setSpeedPID(speedConfig);
        vconf.setName("FakeVehicle");
        vconf.addParameter(new VehicleParameter("headlights", true));
        vconf.addParameter(new VehicleParameter("weight", 150F));
        vconf.addParameter(new VehicleParameter("name", "RCCAR"));
        vconf.saveToFile();
        System.exit(1337);
    }
}
