package org.example.tline.gui;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Region;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeMap;

public class Charts {

    private NumberAxis xAxis = new NumberAxis();
    private CategoryAxis yAxis = new CategoryAxis();
    private BarChart<Number, String> barChart;


    public Charts() {
        barChart = new BarChart<>(xAxis, yAxis);
    }

    public NumberAxis getxAxis() {
        return xAxis;
    }

    public CategoryAxis getyAxis() {
        return yAxis;
    }

    public BarChart<Number, String> getBarChart() {
        return barChart;
    }

    void createChart(ArrayList<String[]> usageData) {
        // usageData: [exe_name, format_name, usage_time]

        // Create series and add data
        XYChart.Series dataSeries = new XYChart.Series<>();
        dataSeries.setName("HI");
        ArrayList<String[]> reversedData = new ArrayList<>(usageData);
        Collections.reverse(reversedData); // Reversed array list for setting colors to bars

        TreeMap<String, String> colorsSettings = getColorSettings();
        System.out.println(colorsSettings);
        usageData.forEach(s -> System.out.print(Arrays.toString(s) + "    "));
        // Add all record to series
        for (int i = 0; i < usageData.size(); i++) {

            String[] array = usageData.get(i); // [exe_name, format_name, usage_time]
            String[] revArray = reversedData.get(i); // [usage_time, format_name, exe_name]

            // Create new bar and add proper color to it
            var newBar = new XYChart.Data(Integer.parseInt(revArray[2]), revArray[1]);
            newBar.nodeProperty().addListener((observable, oldNode, newNode) -> {
                if (newNode != null) {
                    String colorHEX = "#FFCD00";
                    if (colorsSettings.keySet().contains(array[1])) {
                        colorHEX = colorsSettings.get(array[1]);
                        System.out.println("hello");
                    }
                    ((Region) newNode).setStyle("-fx-bar-fill: " + colorHEX);
                }
            });
            dataSeries.getData().add(newBar);
        }
        // Add series to chart
        barChart.setBarGap(1); // Increase the gap between bars
        barChart.setCategoryGap(1);
        barChart.setAnimated(true);
        barChart.getData().add(dataSeries);
    }

    public static void setupChart(BarChart chart, NumberAxis xAxis, CategoryAxis yAxis) {

        chart.setTitle("Chart");
    }

    private static TreeMap<String, String> getColorSettings() {

        TreeMap<String, String> colorSettings = new TreeMap<>();

        try (Connection connection = DriverManager.getConnection(
                "jdbc:sqlite:resources\\databases\\other-databases\\colors-settings.db")) {

            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM settings");
            while (results.next()) {
                colorSettings.put(
                        results.getString(1), results.getString(2));
            }

        } catch (SQLException ignore) {}

        return colorSettings;
    }

}



