package org.example.vehicle;

import org.example.communication.RemoteCommunication;
import org.example.input.Binding;
import org.example.input.Input;
import org.example.processors.MiniPID;
import org.example.processors.MotionProcessor;
import org.example.processors.PIDConfiguration;
import org.example.utils.TorqueCurve;
import org.example.vehicle.config.VehicleConfiguration;

import java.text.DecimalFormat;

public class Vehicle implements Runnable {

    private float throttle = 0;

    private float maxTorque = 0;

    MotionProcessor motionProcessor;
    RemoteCommunication remoteCommunication;

    Input input;
    private boolean started = false;

    private long refreshRate = 10;

    private final MiniPID accelPID = new MiniPID(0.5,0,0.05);
    private final MiniPID speedPID = new MiniPID(0.2,0.01,0.001);

    private TorqueCurve torqueCurve;
    private long sleepTime;

    private Binding throttleBinding;

    private float engineRequestedPower = 0;

    private PIDConfiguration accelConfig;
    private PIDConfiguration speedConfig;
    private VehicleConfiguration configuration;
    public Vehicle() {
        System.out.println("Vehicle initialized");
        accelPID.setOutputLimits(0,1);
        speedPID.setOutputLimits(0,1);
    }

    @Override
    public void run() {

        accelPID.reset();
        speedPID.reset();
        System.out.println("Vehicle thread started");
        System.out.println("Speed pid " + speedPID);
        System.out.println("Accel pid " + accelPID);
        System.out.println("Speed conf pid " + speedConfig);
        System.out.println("Accel conf pid " + accelConfig);

        started = true;
        DecimalFormat df = new DecimalFormat("0.00");
        sleepTime = 1000 / refreshRate;
        while(started && remoteCommunication.isConnected()) {
            input.processInputs();
            motionProcessor.process();
            setThrottle(throttleBinding.getValue());
            double accelOutput = accelPID.getOutput(motionProcessor.getAccel().mag());
            double speedOutput = speedPID.getOutput(motionProcessor.getSpeed().mag());


            float rpm = motionProcessor.getSpeed().getX();
            if(rpm < 0) {
                rpm = 0;
            }

            this.throttle = clamp(throttle);
            float torque = 1;
            if(torqueCurve != null) {
                if(maxTorque == 0) {
                    maxTorque = torqueCurve.getMaximumTorque();
                }
                torque = (torqueCurve.getTorque(rpm) / maxTorque);
            }

            engineRequestedPower = (float) (255 * accelOutput * speedOutput * this.throttle * torque);
            //System.out.println("----------------");
            System.out.println(
                            "AccelOutput: " + df.format(accelOutput) +
                            "; SpeedOutput: " + df.format(speedOutput) +
                            "; Throttle: " + df.format(this.throttle) +
                            "; CalcTorque: " + df.format(torque) +
                            "; RPM: " + df.format(rpm) +
                            "; Accel: " + df.format(motionProcessor.getAccel().getX()) +
                            "; EnginePower: " + df.format(engineRequestedPower));
            remoteCommunication.sendMotorValue(engineRequestedPower);

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stop() {
        started = false;
    }

    float clamp(float value) {
        if(value > 1) {
            return 1;
        } else if (value < 0) {
            return 0;
        } else {
            return value;
        }
    }

    public TorqueCurve getTorqueCurve() {
        return torqueCurve;
    }

    public void setTorqueCurve(TorqueCurve torqueCurve) {
        this.torqueCurve = torqueCurve;
    }

    public void setMotionProcessor(MotionProcessor motionProcessor) {
        this.motionProcessor = motionProcessor;
    }

    public void setRemoteCommunication(RemoteCommunication remoteCommunication) {
        this.remoteCommunication = remoteCommunication;
    }

    public long getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(long refreshRate) {
        this.refreshRate = refreshRate;
        this.sleepTime = 1000 / refreshRate;
    }

    public boolean isStarted() {
        return started;
    }

    public void setThrottle(float throttle) {
        this.throttle = throttle;
    }

    public float getThrottle() {
        return throttle;
    }

    public float getMaxTorque() {
        return maxTorque;
    }

    public void setMaxTorque(float maxTorque) {
        this.maxTorque = maxTorque;
    }

    public float getEngineRequestedPower() {
        return engineRequestedPower;
    }


    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
        this.throttleBinding = input.getBinding("throttle");
    }

    public void setAccelPID(PIDConfiguration accelConfig) {
        this.accelConfig = accelConfig;
        accelConfig.configure(accelPID);
    }

    public void setSpeedPID(PIDConfiguration speedConfig) {
        this.speedConfig = speedConfig;
        speedConfig.configure(speedPID);
    }

    public MiniPID getAccelPID() {
        return accelPID;
    }

    public MiniPID getSpeedPID() {
        return speedPID;
    }

    public PIDConfiguration getAccelConfig() {
        return accelConfig;
    }

    public PIDConfiguration getSpeedConfig() {
        return speedConfig;
    }

    public void setAccelConfig(PIDConfiguration accelConfig) {
        this.accelConfig = accelConfig;
    }

    public void setSpeedConfig(PIDConfiguration speedConfig) {
        this.speedConfig = speedConfig;
    }

    public VehicleConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(VehicleConfiguration configuration) {
        this.configuration = configuration;
    }
}
