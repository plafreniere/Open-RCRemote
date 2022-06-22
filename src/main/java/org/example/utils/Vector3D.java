package org.example.utils;

import lombok.Data;
import java.util.Calendar;

@Data
public class Vector3D {
    private float x;
    private float y;
    private float z;
    private long updateTime;

    public Vector3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.updateTime = Calendar.getInstance().getTimeInMillis();
    }

    public Vector3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.updateTime = Calendar.getInstance().getTimeInMillis();
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.updateTime = Calendar.getInstance().getTimeInMillis();
    }

    public Vector3D copy(Vector3D v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.updateTime = v.updateTime;
        return this;
    }


    public void add(Vector3D vec) {
        this.x += vec.getX();
        this.y += vec.getY();
        this.z += vec.getZ();
    }

    public void sub(Vector3D vec) {
        this.x -= vec.getX();
        this.y -= vec.getY();
        this.z -= vec.getZ();
    }

    public double heading() {

        return Math.atan2(this.y, this.x); // x,y must be inverted
    }

    public double angleBetween(Vector3D v1) {
        Vector3D v2 = this;

        if (v1.x == 0 && v1.y == 0 && v1.z == 0) return 0.0;
        if (v2.x == 0 && v2.y == 0 && v2.z == 0) return 0.0;

        double dot = v1.x * v2.x + v1.y * v2.y;
        double v1mag = Math.sqrt(v1.x * v1.x + v1.y * v1.y);
        double v2mag = Math.sqrt(v2.x * v2.x + v2.y * v2.y);

        double amt = dot / (v1mag * v2mag);

        if (amt <= -1) {
            return Math.PI;
        } else if (amt >= 1) {
            return 0;
        }
        return Math.acos(amt);
    }

    public void setMag(float len) {
        this.normalize();
        this.mult(len);
    }

    public void normalize() {
        double m = this.mag();
        if (m != 0 && m != 1) {
            this.div(m);
        }
    }

    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    // completely incomplete
    public double cross(Vector3D v) {
        return x * v.y - v.x * y - v.z * z;
    }

    public void mult(float n) {
        this.x *= n;
        this.y *= n;
        this.z *= n;
    }

    public void div(double n) {
        this.x /= n;
        this.y /= n;
        this.z /= n;
    }
    public Vector3D div(long n) {
        this.x /= n;
        this.y /= n;
        this.z /= n;
        return this;
    }
}
