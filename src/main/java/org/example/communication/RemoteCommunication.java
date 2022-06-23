package org.example.communication;

import org.example.input.Binding;
import org.example.processors.MotionProcessor;
import org.example.utils.Vector3D;
import org.example.vehicle.config.VehicleConfiguration;

import java.awt.*;

public interface RemoteCommunication {
    void sendMotorValue(float value);

    void setRemoteConfiguration(RemoteConfiguration remoteConfiguration);

    RemoteConfiguration getRemoteConfiguration();

    void sendSteeringValue(float value);
    Vector3D receiveMotion();

    Image receiveVideoFrame();

    boolean isVideoCompatible();

    boolean newFrameAvailable();

    boolean connect();

    boolean disconnect();

    boolean isConnected();

    void sendConfiguration(VehicleConfiguration vehicleConfiguration);

    void setMotionProcessor(MotionProcessor motionProcessor);

    void setBrakeBinding(Binding binding);

    Binding getBrakeBinding();
}
