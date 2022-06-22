package org.example.communication;

import com.github.sarxos.webcam.Webcam;
import org.example.processors.MotionProcessor;
import org.example.utils.Vector3D;
import org.example.vehicle.config.VehicleConfiguration;

import java.awt.*;

public class FakeRemoteCommunication implements RemoteCommunication {
    private final Webcam webcam;
    private float lastPosition = 0;
    private float motorValue = 0;

    boolean connected;

    RemoteConfiguration remoteConfiguration;

    private MotionProcessor motionProcessor;
    public FakeRemoteCommunication() {
        remoteConfiguration = new RemoteConfiguration();
        remoteConfiguration.setIp("localhost");
        remoteConfiguration.setPort("12111");
        remoteConfiguration.setVideoCompatible(true);

        webcam = Webcam.getDefault();
        Dimension[] dimensions = webcam.getViewSizes();

        webcam.setViewSize(dimensions[dimensions.length - 1]);
        webcam.open();

    }

    @Override
    public void sendMotorValue(float value) {
        this.motorValue = value;
    }

    @Override
    public void sendSteeringValue(float value) {

    }

    @Override
    public Vector3D receiveMotion() {
        float loss = (motionProcessor.getSpeed().getX() * 5f) + 2;
        float accel = (motorValue * 20f);
        float newPosition = lastPosition + (accel- loss);
        if(newPosition < 0) {
            newPosition = 0;
        }
        float filter = 0.5F;
        newPosition = lastPosition*filter+newPosition*(1-filter);
        lastPosition = newPosition;
        System.out.println("Accel: " + accel + "; Loss: " + loss + "; Last: " + lastPosition + "; New: " + newPosition);
        return new Vector3D(newPosition, 0, 0);

    }

    @Override
    public Image receiveVideoFrame() {
        return webcam.getImage();
    }

    @Override
    public void sendConfiguration(VehicleConfiguration vehicleConfiguration) {

    }

    @Override
    public boolean isVideoCompatible() {
        return remoteConfiguration.isVideoCompatible();
    }

    public boolean newFrameAvailable() {
        return webcam.isImageNew();
    }

    public void setMotionProcessor(MotionProcessor motionProcessor) {
        this.motionProcessor = motionProcessor;
    }

    @Override
    public boolean connect() {
        connected = true;
        return connected;
    }

    @Override
    public boolean disconnect() {
        connected = false;
        return connected;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void setRemoteConfiguration(RemoteConfiguration remoteConfiguration) {
        this.remoteConfiguration = remoteConfiguration;
    }

    @Override
    public RemoteConfiguration getRemoteConfiguration() {
        return remoteConfiguration;
    }
}
