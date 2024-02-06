package org.example.tline.functionality;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class DataBase {

    private TreeMap<String, String[]> usageData; // <exe_name, [usage_time, format_name]
    private TreeMap<String, String[]> usageDataTotal; // usageData for total-time database
    private final String dbPath; // database path
    private String dbPathTotal; // database with all-time statistics

    public DataBase() {

        String localDate = LocalDate.now().toString();
        dbPath = "jdbc:sqlite:resources\\databases\\time-data-" + localDate + ".db";
        dbPathTotal = "jdbc:sqlite:resources\\databases\\other-databases\\total-time.db";

        createTableIfNeeded(dbPath);
        createTableIfNeeded(dbPathTotal);
    }

    private static void createTableIfNeeded(String dbPath) {
        try (Connection connection = DriverManager.getConnection(dbPath);
            Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS apps_time " +
                            "(exe_name TEXT, " +
                            "format_name TEXT, " +
                            "usage_time INTEGER);");
        } catch (SQLException e) {
            throw new RuntimeException("Error creating table: " + e.getMessage());
        }
    }

    public void uploadDataToDB(TreeMap<String, String[]> map) {
        // Make copy of received tree map
        usageData = new TreeMap<>(map);
        usageDataTotal = new TreeMap<>(map);
        try (Connection connection = DriverManager.getConnection(dbPath)) {
            // Iterate through exeName from usageData
            for (var exeName : map.keySet()) {
                updateOrInsertRecord(connection, exeName);
            }
        } catch (SQLException ignore) {}

        try (Connection connection = DriverManager.getConnection(dbPathTotal)) {
            // Update total-time database
            for (var exeName : map.keySet()) {
                updateOrInsertRecord(connection, exeName);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error uploading data to DB: " + e.getMessage());
        }
    }

    private void updateOrInsertRecord(Connection connection, String exeName) throws SQLException {
        String[] formatResults = format(exeName);
        String formatName = formatResults[0];
        exeName = formatResults[1];
        // get time from the last minute
        int timeFromMap = Integer.parseInt(usageData.get(exeName)[0]);

        try (Statement statement = connection.createStatement()) {

            // get record for current exeName
            ResultSet record = statement.executeQuery("SELECT * FROM apps_time WHERE exe_name = '" + exeName + "'");
            int totalTime = timeFromMap;

            // update time_usage if record is not null
            if (record.next()) {
                int timeFromDB = record.getInt(3);
                totalTime = timeFromDB + timeFromMap;
                statement.execute("UPDATE apps_time SET usage_time = " + totalTime +
                        " WHERE exe_name = '" + exeName + "'");
            } else {
                // insert new record
                statement.execute("INSERT INTO apps_time (exe_name, format_name, usage_time) VALUES " +
                        "('" + exeName + "', '" + formatName + "', " + totalTime + ")");
            }
        }
    }

    private String[] format(String name) {

        // weird names (settings, start, taskbar, etc)
        ArrayList<String> systemNames = new ArrayList<>(List.of(
                "explorer.exe", "searchapp.exe", "shellexperiencehost.exe",
                "startmenuexperiencehost.exe, applicationframehost.exe",
                "applicationframehost.exe"));

        String formatName = name;

        // Detect weird names
        if (systemNames.contains(name) || name.isBlank() || name.isEmpty()) {

            String[] removedValues = usageData.remove(name);
            if (removedValues == null) {
                removedValues = usageDataTotal.remove(name);
                usageDataTotal.put("others", new String[]{removedValues[0], "System And Others"});
                name = "others";
            } else {
                usageData.put("others", new String[]{removedValues[0], "System And Others"});
                name = "others";
            }

        } else {
            // Remove ".exe"
            if (name.contains(".exe")) formatName = formatName.replace(".exe", "");
            // Remove "64" and "32" from the end of name
            if (formatName.endsWith("64") || formatName.endsWith("32")) {
                formatName = formatName.substring(0, formatName.length() - 2);
            }
            // Capitalize
            String capitalized = "";
            for (var e : Arrays.stream(formatName.split("\\s+")).toArray(String[]::new)) {
                char firstChar = Character.toUpperCase(e.charAt(0));
                capitalized = capitalized.concat(firstChar + e.substring(1) + " ");
            }
            formatName = capitalized;
        }

        return new String[] {formatName, name}; // {format_name, exe_name}
    }
}