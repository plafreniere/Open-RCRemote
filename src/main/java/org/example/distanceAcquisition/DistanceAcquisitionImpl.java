package org.example.distanceAcquisition;

import org.example.communication.RemoteCommunication;
import org.example.utils.Vector3D;

public class DistanceAcquisitionImpl implements DistanceAcquisition {

    private final RemoteCommunication remoteCommunication;

    public DistanceAcquisitionImpl(RemoteCommunication remoteCommunication) {
        this.remoteCommunication = remoteCommunication;
    }
    @Override
    public Vector3D getDistance() {
        return remoteCommunication.receiveMotion();
    }
}
