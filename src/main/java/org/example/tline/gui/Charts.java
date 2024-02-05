package org.example.tline.gui;

import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

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
        dataSeries.setName("usage");

        // Add all record to series
        for (String[] array : usageData) {
            dataSeries.getData().add(new XYChart.Data(
                    Integer.parseInt(array[2]),
                    array[1]));
        }

        // Add series to chart
        barChart.getData().add(dataSeries);
    }

    public static void setupChart(BarChart chart, NumberAxis xAxis, CategoryAxis yAxis) {

        chart.setTitle("Chart");

    }
}


