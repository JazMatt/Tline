package org.example.tline.threads;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class DataBase {

    volatile private TreeMap<String, String[]> usageData;
    private static final String DB_PATH = "jdbc:sqlite:resources\\databases\\time-data.db";

    public DataBase() {

        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            Statement statement = connection.createStatement();
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS apps_time " +
                            "(exe_name TEXT, " +
                            "format_name TEXT, " +
                            "usage_time INTEGER);");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void uploadDataToDB(TreeMap<String, String[]> map) {

        usageData = map;

        for (var exeName : map.keySet()) {

            // Format key (exeName) and detect weird exeName value
            String[] formatResults = format(exeName);
            String formatName = formatResults[0];
            exeName = formatResults[1];

            int totalTime;
            try (Connection connection = DriverManager.getConnection(DB_PATH)) {
                Statement statement = connection.createStatement();
                int timeFromMap = Integer.parseInt(usageData.get(exeName)[0]); // Get time from current session

                // Update time in database if app data is already there
                try {
                    ResultSet record = statement.executeQuery("SELECT * FROM apps_time WHERE exe_name = '" + exeName + "';");
                    int timeFromDB = record.getInt(3);
                    System.out.println(exeName + " time: " + timeFromMap);
                    totalTime = timeFromDB + timeFromMap;
                    statement.execute("UPDATE apps_time SET usage_time = " + totalTime +
                            " WHERE exe_name = '" + exeName + "';");

                // Create new record if app data is not in database
                } catch (SQLException e) {
                    totalTime = timeFromMap;
                    statement.execute("INSERT INTO apps_time (exe_name, format_name, usage_time) VALUES " +
                            "('" + exeName + "', '" + formatName + "', " + totalTime + ");");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String[] format(String name) {

        ArrayList<String> systemNames = new ArrayList<>(List.of(
                "explorer.exe", "searchapp.exe", "shellexperiencehost.exe",
                "startmenuexperiencehost.exe"));

        String formatName = name;

        // Detect weird names
        if (systemNames.contains(name) || name.isBlank() || name.isEmpty()) {

            String[] removedValues = usageData.remove(name);
            formatName = "System And Others";
            name = "others";
            usageData.put(name, new String[]{removedValues[0], formatName});

        } else {
            // Remove ".exe"
            if (name.contains(".exe")) formatName = formatName.replace(".exe", "");
            // Capitalize
            char firstChar = Character.toUpperCase(formatName.charAt(0));
            formatName = firstChar + formatName.substring(1);
            // Remove "64" and "32" from the end of name
            if (formatName.endsWith("64") || formatName.endsWith("32")) {
                formatName = formatName.substring(0, formatName.length() - 2);
            }
        }

        return new String[] {formatName, name};
    }
}