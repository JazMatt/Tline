package org.example.tline.gui;

import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.control.Label;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.TreeMap;

public class Charts {

    private NumberAxis xAxis = new NumberAxis();
    private CategoryAxis yAxis = new CategoryAxis();
    private BarChart<Number, String> barChart;
    private int totalTime;


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
        // Add all record to series
        for (int i = 0; i < usageData.size(); i++) {

            String[] array = usageData.get(i); // [exe_name, format_name, usage_time]
            String[] revArray = reversedData.get(i); // [usage_time, format_name, exe_name]

            // Create new bar and add proper color to it
            var newBar = new XYChart.Data(Integer.parseInt(revArray[2])/3600d, revArray[1]);
            newBar.nodeProperty().addListener((observable, oldNode, newNode) -> {
                if (newNode != null) {
                    String colorHEX = "#FFCD00";
                    if (colorsSettings.keySet().contains(array[1])) {
                        colorHEX = colorsSettings.get(array[1]);
                    }
                    ((Region) newNode).getStyleClass().add("bar");
                    ((Region) newNode).setStyle("-fx-bar-fill: " + colorHEX);
                }
            });

            // sum time from all apps
            totalTime += Integer.parseInt(array[2]);

            dataSeries.getData().add(newBar);

        }
        // Add series to chart
        barChart.setBarGap(1);
        barChart.setCategoryGap(1);
        barChart.setAnimated(true);
        barChart.getData().add(dataSeries);
        barChart.setMinHeight(100);
        barChart.setPadding(new Insets(0, 30, 0, 15));
        // Adjust prefHeight to number of bars
        int size = barChart.getData().get(0).getData().size();
        barChart.setPrefHeight(size * 40 + 40);
    }

    public static void setupChart(BarChart chart, NumberAxis xAxis, CategoryAxis yAxis) {

        chart.getStyleClass().add("chart");
        yAxis.setTickLabelFill(Paint.valueOf("white"));
        xAxis.setTickLabelFill(Paint.valueOf("white"));
    }

    private static TreeMap<String, String> getColorSettings() {

        TreeMap<String, String> colorSettings = new TreeMap<>(); // <format_name, color>

        try (Connection connection = DriverManager.getConnection(
                "jdbc:sqlite:resources\\databases\\other-databases\\colors-settings.db")) {

            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM settings");
            // add all records to colorSettings
            while (results.next()) {
                colorSettings.put(
                        results.getString(1), results.getString(2));
            }

        } catch (SQLException ignore) {}

        return colorSettings;
    }

    void showSummaryLabel(VBox vbox) {

        // format total time
        int hours = totalTime / 3600;
        int minutes = totalTime % 60;
        Label summaryLabel = new Label();

        if (hours == 0) {
            summaryLabel.setText("Total Time: " + minutes+"min");
        } else {
            summaryLabel.setText("Total Time: " + hours+"h" + minutes+"min");
        }
        summaryLabel.getStyleClass().add("summaryLabel");
        vbox.getChildren().add(summaryLabel);
    }

}



