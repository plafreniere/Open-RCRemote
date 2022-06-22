package org.example.utils;

public class BasicTorqueCurve {

    static public TorqueCurve getCurve() {

        TorqueCurve tc = new TorqueCurve();

        tc.addPoint(0,3);
        tc.addPoint(10,10);
        tc.addPoint(20, 12);
        tc.addPoint(25,14);
        tc.addPoint(30,15);
        tc.addPoint(40, 10);
        tc.addPoint(50, 9);
        tc.addPoint(70,6);

        return tc;
    }
}
