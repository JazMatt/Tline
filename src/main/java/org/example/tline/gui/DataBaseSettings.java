package org.example.tline.gui;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseSettings {

    public static int saveSettings(String oldName, String newName, String colorHEX) {

        if (oldName == null || newName == null) return -1;
        try (Connection connection = DriverManager.getConnection(
                "jdbc:sqlite:resources\\databases\\other-databases\\settings.db")) {

            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM settings WHERE format_name = '" + newName + "'";
            ResultSet results = statement.executeQuery(sql);

            if (results.next()) return -1;
            sql ="UPDATE settings SET color='" + colorHEX + "', format_name = '" +
                    newName + "' WHERE format_name = '" + oldName + "'";
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
