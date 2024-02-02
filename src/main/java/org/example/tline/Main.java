package org.example.tline;

import org.example.tline.threads.GetCurrentApp;

public class Main {

    public static void main(String[] args) {

        var currentApp = new Thread(new GetCurrentApp());
        currentApp.start();

    }
}
