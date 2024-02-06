package org.example.tline.gui;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.*;

import javafx.scene.input.MouseEvent;


public class Controller1 implements Initializable {

    @FXML
    private VBox vBox; // VBox for labels (except the 'total' label)
    @FXML
    private Label total; // 'total' label
    @FXML
    private Label logo; // Logo label at the bottom
    @FXML
    private Label customLabel;
    @FXML
    private BarChart<Number, String> barChart; // Bar chart for usage time statistics
    @FXML
    private NumberAxis xAxis;
    @FXML
    private CategoryAxis yAxis;
    @FXML
    private VBox chartVbox; // Vbox for barChart
    @FXML
    private Label settingsLabel;
    private ArrayList<String> datesAL; // Array list of databases names
    private ScaleTransition scaleTransition; // animation
    private ArrayList<Label> labels = new ArrayList<>();
    // Array list for labels. Required to delete 'dateClicked' style when another label is clicked

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        datesAL = new ArrayList<>();
        labels.add(total);
        labels.add(settingsLabel);

        // Create scale transition for hover effect
        scaleTransition = new ScaleTransition(Duration.millis(100));

        // Add all files names from databases folder to array list
        String directoryPath = "resources/databases";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            // Format files names, create labels from them, add properties and add to VBox
            for (File file : files) {
                if (!file.isDirectory()) {
                    String fileName = file.getName().replace("time-data-", "").replace(".db", "");
                    Label label = new Label(fileName);
                    setupLabel(label); // setup label's properties
                }
            }
        }
    }

    private void onMouseEntered(Label label) {
        if (!label.getStyleClass().toString().contains("dateClicked")) {
            label.getStyleClass().add("dateHovered");
            if (label != settingsLabel) return;
            settingsLabel.setStyle("-fx-font-size: 19px");
        }
    }

    private void onMouseExited(Label label) {
        label.getStyleClass().remove("dateHovered");
        settingsLabel.setStyle("-fx-font-size: 18px");
    }

    private void onMouseClicked(Label label) {

        // remove the 'dateClicked' style from all labels (works better than saving previously clicked
        // label in private field and removing style from it).
        for (Label currentLabel : labels) {
            currentLabel.getStyleClass().remove("dateClicked");
            currentLabel.getStyleClass().add("dateLabel");
            settingsLabel.getStyleClass().remove("settingsClicked");
            currentLabel.setScaleX(1.0);
            currentLabel.setScaleY(1.0);
        }

        if (label != logo) {
            // add style to clicked label
            label.getStyleClass().remove("dateLabel");
            label.getStyleClass().remove("dateHovered");

            if (label != settingsLabel) label.getStyleClass().add("dateClicked");
            else label.getStyleClass().add("settingsClicked");
        }

        // 'POP' animation
        popAnimation(label);

        if (label == logo) {
            // Redirect to website
            System.out.println("Should be redirected to website");
            return;
        }

        if (label == settingsLabel) {
            showSettings();
            return;
        }

        customLabel.setText(label.getText() + " - Screen Time");

        if (label != total) {

            String dbPath = switch(label.getText().toLowerCase()) {
                case "total" -> "other-databases\\total-time.db"
            }

            // get data for chosen database
            DataBaseGUI database = new DataBaseGUI(label);
            database.connectToDB();

            // array list sorted by usage_time descending
            // [exe_name, format_name, usage_time]
            var usageData = database.getUsageData();

            // remove the previous chart
            if (barChart != null) {
                chartVbox.getChildren().clear();

                // add the title
                chartVbox.getChildren().add(customLabel);
            }


            // setup new barChart
            Charts charts = new Charts();
            charts.createChart(usageData);
            barChart = charts.getBarChart();
            xAxis = charts.getxAxis();
            yAxis = charts.getyAxis();
            chartVbox.getChildren().add(barChart);
            Charts.setupChart(barChart, xAxis, yAxis);

            // show label with additional statistics
            charts.showSummaryLabel(chartVbox);
        }
    }


    // Methods for 'total', 'logo' and 'settings' labels only
    public void onMouseEntered(MouseEvent mouseEvent) {

        if (mouseEvent.getSource().toString().contains("Total")) {
            onMouseEntered(total);
        } else if (mouseEvent.getSource().toString().contains("⚙")) {
            onMouseEntered(settingsLabel);
        } else {
            logo.getStyleClass().add("logoHovered");
        }
    }

    public void onMouseExited(MouseEvent mouseEvent) {
        if (mouseEvent.getSource().toString().contains("Total")){
            onMouseExited(total);
        } else if (mouseEvent.getSource().toString().contains("⚙")) {
            onMouseExited(settingsLabel);
        } else {
            logo.getStyleClass().remove("logoHovered");
        }
    }
    public void onMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getSource().toString().contains("Total")){
            onMouseClicked(total);
        } else if (mouseEvent.getSource().toString().contains("⚙")) {
            onMouseClicked(settingsLabel);
        } else {
            onMouseClicked(logo);
        }
    }


    // Setup label properties
    private void setupLabel(Label label) {

        label.getStyleClass().add("dateLabel");
        label.setOnMouseEntered(event -> onMouseEntered(label));
        label.setOnMouseExited(event -> onMouseExited(label));
        label.setOnMouseClicked(event -> onMouseClicked(label));
        label.setContentDisplay(ContentDisplay.LEFT);
        label.setTextAlignment(TextAlignment.LEFT);

        vBox.getChildren().add(label);
        labels.add(label);
    }

    private void popAnimation(Label label) {
        scaleTransition.stop();
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.15);
        scaleTransition.setToY(1.15);
        scaleTransition.setFromX(1.15);
        scaleTransition.setFromY(1.15);
        if (label == total) {
            scaleTransition.setToX(0.95);
            scaleTransition.setToY(1.0);
        }
        else {
            scaleTransition.setToX(1.1);
            scaleTransition.setToY(1.1);
        }
        if (label == logo) {
            scaleTransition.setFromX(1.1);
            scaleTransition.setFromY(1.1);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
        }
        scaleTransition.setNode(label);
        scaleTransition.play();
    }

    private static void showSettings() {


    }
}
