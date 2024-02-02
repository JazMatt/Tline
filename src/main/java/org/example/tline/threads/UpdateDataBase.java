package org.example.tline.threads;

import org.sqlite.SQLiteConnection;
import java.awt.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeMap;

public class UpdateDataBase {

    private static final String DB_PATH = "jdbc:sqlite:resources\\databases\\time-data.db";
    private TreeMap<String, String[]> usageData = new TreeMap<>(); // exe_name {usage_time, format_name}

    public UpdateDataBase() {

        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            Statement statement = connection.createStatement();
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS apps_time " +
                    "(exe_name TEXT, " +
                    "format_name TEXT, " +
                    "usage_time INTEGER);");

            // Put values in usageData
            ResultSet data = statement.executeQuery("SELECT * FROM apps_time;");
            while (data.next()) {
                usageData.put(data.getString(1),
                        new String[] {data.getString(2), data.getString(3)});
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public TreeMap<String, String[]> getUsageData() {
        return usageData;
    }

    public void uploadDataToDB(TreeMap<String, String[]> map) {


    }
}
