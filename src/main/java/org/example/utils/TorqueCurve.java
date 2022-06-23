package org.example.utils;

import java.util.ArrayList;

public class TorqueCurve {

    private final ArrayList<Vector3D> points = new ArrayList<>();
    private float maximumRpm = 0;
    private float maximumTorque = 0;
    public TorqueCurve() {

    }

    public float getTorque(float rpm) {
        if(rpm <= 0) {
            return points.get(0).getY();
        }
        if(rpm >= maximumRpm) {
            return points.get(points.size() - 1).getY();
        }

        int index = 0;
        for(Vector3D point : points) {

            if(point.getX() > rpm) {
                break;
            }
            index++;
        }
        if(index > points.size() -1 ) {
            index = points.size() - 1;
        }
        return map(rpm,
                points.get(index -1 ).getX(),
                points.get(index).getX(),
                points.get(index - 1).getY(),
                points.get(index).getY());
    }

    public ArrayList<Vector3D> getPoints() {
        return points;
    }

    public void addPoint(float x, float y) {
        points.add(new Vector3D(x,y,0));
        processMaximumRpm();
        processMaximumTorque();
    }

    public float getMaximumRpm() {
        return maximumRpm;
    }

    public float getMaximumTorque() {
        return maximumTorque;
    }

    public void processMaximumRpm() {
        float biggest = 0;
        for (Vector3D point : points) {
            if (point.getX() > biggest)
                biggest = point.getX();
        }
        this.maximumRpm = biggest;
    }

    public void processMaximumTorque() {
        float biggest = 0;
        for (Vector3D point : points) {
            if (point.getY() > biggest)
                biggest = point.getY();
        }
        this.maximumTorque = biggest;
    }

    float map(float x, float in_min, float in_max, float out_min, float out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
