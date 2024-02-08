package org.example.tline.gui;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseSettings {

    public static int saveSettings(String oldName, String newName, String colorHEX) {

        System.out.printf("%20s%20s%20s", oldName, newName, colorHEX);
        try (Connection connection = DriverManager.getConnection(
                "\"jdbc:sqlite:resources\\databases\\other-databases\\settings.db")) {

            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(
                    "SELECT * FROM settings WHERE format_name = '" + newName + "'");

            if (results.next()) return -1;
            statement.execute("UPDATE settings SET color='" + colorHEX + "', format_name = '" +
                            newName + "' WHERE format_name = '" + oldName + "'");
        } catch (SQLException ignore) {}
        return 1;
    }
}
