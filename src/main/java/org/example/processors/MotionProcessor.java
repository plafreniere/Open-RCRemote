package org.example.processors;

import org.example.distanceAcquisition.DistanceAcquisition;
import org.example.utils.Vector3D;

import java.util.Calendar;

public class MotionProcessor {

    private final Vector3D accel = new Vector3D();
    private final Vector3D speed = new Vector3D();
    private final Vector3D position = new Vector3D();
    private final DistanceAcquisition distanceAcquisition;

    private final Vector3D lastDistance = new Vector3D();
    private final Vector3D lastSpeed = new Vector3D();
    private final Vector3D lastAccel = new Vector3D();

    public MotionProcessor(DistanceAcquisition distanceAcquisition) {
        this.distanceAcquisition = distanceAcquisition;
    }

    public void process() {
        Vector3D distance = acquireData();
        speed.copy(processSpeed(lastDistance, distance));

        accel.copy(processAcceleration(lastSpeed, speed));

        lastDistance.copy(distance);
        lastSpeed.copy(speed);
    }

    private Vector3D acquireData() {
        Vector3D distance = distanceAcquisition.getDistance();
        position.add(distance);
        return distance;
    }

    private Vector3D processDistance(Vector3D v1, Vector3D v2) {
        Vector3D distance = new Vector3D();

        distance.copy(v2);
        distance.sub(v1);

        return distance;
    }

    private Vector3D processSpeed(Vector3D v1, Vector3D v2) {
        long elapsedTime = v2.getUpdateTime() - v1.getUpdateTime();

        this.speed.copy(processDistance(v1, v2).div(elapsedTime));
        this.speed.copy(v2.div(elapsedTime));
        return speed;
    }

    private Vector3D processAcceleration(Vector3D speed1, Vector3D speed2) {
        long elapsedTime = speed2.getUpdateTime() - speed1.getUpdateTime();

        Vector3D processedValue = new Vector3D();
        processedValue.copy(speed2).sub(speed1);

        accel.copy(processedValue);
        return accel;
    }

    public Vector3D getAccel() {
        return accel;
    }

    public Vector3D getSpeed() {
        return speed;
    }

    public Vector3D getPosition() {
        return position;
    }
}
