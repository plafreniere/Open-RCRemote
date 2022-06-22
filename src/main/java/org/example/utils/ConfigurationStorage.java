package org.example.utils;

import java.io.*;

public abstract class ConfigurationStorage {

    public Object loadFromFile(String filename) {
        try (FileInputStream fis = new FileInputStream(filename);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            //ex.printStackTrace();
            System.out.println("File incompatible : " + filename);
        }
        return null;
    }
    public boolean saveToFile(Object obj, String filename) {
        boolean done = false;

        try (FileOutputStream fos = new FileOutputStream(filename);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(obj);
            done = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return done;
    }
}
