package org.example.tline.gui;

import javafx.scene.chart.BarChart;
import javafx.scene.control.Label;

import java.io.File;
import java.sql.*;
import java.util.*;

public class DataBaseGUI {

    private String dbPath;
    private ArrayList<String[]> usageData;
    private BarChart<String, Number> barChart;

    public DataBaseGUI(String dbRelativePath) {
        dbPath = dbRelativePath;
    }

    void connectToDB() {

        // Get the chosen database
        File currentDatabase = new File("resources\\databases\\" + dbPath);

        // Connect to the chosen database
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + currentDatabase)) {
            // Get all records from the chosen database
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM apps_time");
            usageData = getResultsList(results);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private static ArrayList<String[]> getResultsList(ResultSet results) throws SQLException {

        // ArrayList<[exe_name, format_name, time_usage]>
        ArrayList<String[]> resultsList = new ArrayList<>();
        while (results.next()) {
            resultsList.add(new String[] {
                    results.getString(1),   // exe_name
                    results.getString(2),   // format_name
                    results.getString(3)}); // time_usage

        }
        resultsList.sort(new RecordComparator());
        return  resultsList;

    }

    private static class RecordComparator implements Comparator<String[]> {

        // order by usage_time descending
        @Override
        public int compare(String[] o1, String[] o2) {

            // compare time_usage
            int int1 = Integer.parseInt(o1[2]);
            int int2 = Integer.parseInt(o2[2]);

            if (int1 > int2) return -1;
            if (int1 < int2) return 1;

            // if usage_time is the same, compare format_name
            return o1[1].compareTo(o2[1]);
        }
    }

    public ArrayList<String[]> getUsageData() {
        return usageData;
    }
}
