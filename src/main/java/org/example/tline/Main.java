package org.example.tline;

import org.example.tline.threads.StartTracking;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        var startTracking = new Thread(new StartTracking());
        startTracking.start();

    }
}
