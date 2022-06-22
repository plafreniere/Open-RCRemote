package org.example.gui;

import org.example.utils.TorqueCurve;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.util.Locale;


public class GraphWindow {
    static private JFrame frame = new JFrame("Charts");

    public static void show(String title, XYDataset dataset, String x) {
        frame.setSize(600,400);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JFreeChart chart = ChartFactory.createXYLineChart(title,
                x, title.toLowerCase(Locale.ROOT), dataset, PlotOrientation.VERTICAL, true, true,
                false);

        ChartPanel cp = new ChartPanel(chart);

        frame.getContentPane().add(cp);
        frame.setVisible(true);
    }

    public static XYDataset convertAccelCurveToDataset(TorqueCurve torqueCurve) {
        int numberOfPoints = 10;
        DefaultXYDataset ds = new DefaultXYDataset();
        float maximumSpeed = torqueCurve.getMaximumRpm();
        int scale = (int) (maximumSpeed / numberOfPoints);
        double[][] data = new double[2][numberOfPoints];
        int step = 0;
        for(int a = 0; a < numberOfPoints; a++) {
            data[0][step] = scale * step;
            data[1][step] = torqueCurve.getTorque(scale * step);
            step++;
        }
        data[0][numberOfPoints -1] = maximumSpeed;
        data[1][numberOfPoints -1] = torqueCurve.getTorque(maximumSpeed - 1);

        ds.addSeries("series1", data);
        return ds;
    }
}
