package org.example.tline.functionality;

public class MainStart {

    public static void main(String[] args) {

        var startTracking = new Thread(new StartTracking());
        startTracking.start();

    }
}
