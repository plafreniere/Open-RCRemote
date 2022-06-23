package org.example.utils;

public class BasicTorqueCurve {

    static public TorqueCurve getCurve() {

        TorqueCurve tc = new TorqueCurve();

        tc.addPoint(0,9);
        tc.addPoint(20,10);
        tc.addPoint(40, 12);
        tc.addPoint(60,18);
        tc.addPoint(80,22);
        tc.addPoint(100, 23);
        tc.addPoint(120, 21);
        tc.addPoint(140,20);
        tc.addPoint(160,19);
        tc.addPoint(180,18.5F);
        tc.addPoint(200,18);
        return tc;
    }
}
