package org.example.processors;

import lombok.Data;
import org.example.utils.ConfigurationStorage;

import java.io.Serializable;

@Data
public class PIDConfiguration extends ConfigurationStorage implements Serializable {
    private double P = 0;
    private double I = 0;
    private double D = 0;
    private double F = 0;
    private double setPoint = 0;
    private double setPointRange = 0;
    private double outputFilter = 0;
    private double maxIOutput = 0;
    private double outputRampRate = 0;
    private String filename;

    public void configure(MiniPID pid) {
        pid.setPID(P,I,D,F);
        pid.setSetpoint(setPoint);
        pid.setOutputFilter(outputFilter);
        pid.setMaxIOutput(maxIOutput);
        pid.setOutputRampRate(outputRampRate);
        pid.setSetpointRange(setPointRange);
    }

    public PIDConfiguration(double p, double i, double d) {
        this.P = p;
        this.I = i;
        this.D = d;
    }

    public PIDConfiguration(String filename) {
        this.filename = filename;
        if(loadFromFile()) {
            System.out.println("Loaded PID config " + filename);
        } else {
            saveToFile();
            System.out.println("Created PID config " + filename);
        }
    }
    public PIDConfiguration() {

    }

    public boolean loadFromFile() {
        Object obj =  loadFromFile(filename);
        if(obj == null) {
            return false;
        }
        this.copy((PIDConfiguration) obj);
        return true;
    }

    public void saveToFile() {
        saveToFile(this, filename);
    }
    public void copy(PIDConfiguration pid) {
        this.P = pid.P;
        this.I = pid.I;
        this.D = pid.D;
        this.F = pid.F;
        this.setPoint = pid.setPoint;
        this.setPointRange = pid.setPointRange;
        this.outputFilter = pid.outputFilter;
        this.maxIOutput = pid.maxIOutput;
        this.outputRampRate = pid.outputRampRate;
        this.filename = pid.filename;
    }

}
