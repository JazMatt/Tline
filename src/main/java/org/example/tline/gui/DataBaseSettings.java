package org.example.tline.gui;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseSettings {

    public static ArrayList<String> getSettings() {

        // Connect to database with colors settings
        try (Connection connection = DriverManager.getConnection(
                "jdbc:sqlite:resources\\databases\\other-databases\\settings.db")) {

            ArrayList<String> names = new ArrayList<>();

            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM settings");
            while (results.next()) {
                names.add(results.getString(2));
            }
            return names;

        } catch (SQLException ignore) {}

        return new ArrayList<>(List.of(""));
    }
}
