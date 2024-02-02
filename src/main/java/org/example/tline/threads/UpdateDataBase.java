package org.example.tline.threads;

import org.sqlite.SQLiteConnection;
import java.awt.*;
import java.sql.*;
import java.util.Set;
import java.util.TreeMap;

public class UpdateDataBase {

    private static final String DB_PATH = "jdbc:sqlite:resources\\databases\\time-data.db";
    private Statement statement;
    private final Connection connection;
    private TreeMap<String, Integer> usageData;

    public UpdateDataBase() {

        try (Connection con = DriverManager.getConnection(DB_PATH)) {
            this.connection = con;
            statement = connection.createStatement();
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS apps_time " +
                    "(process_name TEXT, " +
                    "name_formatted TEXT, " +
                    "time INTEGER)");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> getAppsInDB() {
        return usageData.keySet();
    }
}
