package org.example.tline.gui;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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
    private Label saveLabel; // 'Save' button in settings
    private ArrayList<String[]> usageData = new ArrayList<>(); // sorted by usage_time
    private ChoiceBox oldName;
    private TextField newName;
    private ColorPicker colorPicker;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        saveLabel = new Label();
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
        if (label == saveLabel) {
            label.setStyle("-fx-font-size: 17px;");
            return;
        }
        if (!label.getStyleClass().toString().contains("dateClicked")) {
            label.getStyleClass().add("dateHovered");
            if (label != settingsLabel) return;
            settingsLabel.setStyle("-fx-font-size: 19px");
        }
    }

    private void onMouseExited(Label label) {
        if (label == saveLabel) {
            label.setStyle("-fx-font-size: 16px;");
            return;
        }
        label.getStyleClass().remove("dateHovered");
        settingsLabel.setStyle("-fx-font-size: 18px");
    }

    private void onMouseClicked(Label label) {

        if (label == saveLabel) {
            popAnimation(label);
            onMouseClicked(total);
            int result = DataBaseSettings.saveSettings(oldName.getValue().toString(),
                    newName.getText(), toRGBCode(colorPicker.getValue()));
            if (result == -1) {
                System.out.println("Error window here");
            }
            if (result == -2) {
                System.out.println("not found");
            }
            return;
        }

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
            // clear current screen
            chartVbox.getChildren().clear();
            chartVbox.getChildren().add(customLabel);

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

        String text = label == total ? "Total Screen Time" : label.getText() + " - Screen Time";
        customLabel.setText(text);

        String dbPath = switch(label.getText().toLowerCase()) {
            case "total" -> "other-databases\\total-time.db";
            default -> "time-data-" + label.getText() + ".db";
        };

        // get data for chosen database
        DataBaseGUI database = new DataBaseGUI(dbPath);
        database.connectToDB();

        // array list sorted by usage_time descending
        // [exe_name, format_name, usage_time]
        usageData = database.getUsageData();

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


    // Methods for 'total', 'logo', 'settings' and 'save' labels only
    public void onMouseEntered(MouseEvent mouseEvent) {

        if (mouseEvent.getSource().toString().contains("Total")) {
            onMouseEntered(total);
        } else if (mouseEvent.getSource().toString().contains("⚙")) {
            onMouseEntered(settingsLabel);
        } else if (mouseEvent.getSource().toString().contains("Save")) {
            onMouseEntered(saveLabel);
        } else {
            logo.getStyleClass().add("logoHovered");
        }
    }

    public void onMouseExited(MouseEvent mouseEvent) {
        if (mouseEvent.getSource().toString().contains("Total")){
            onMouseExited(total);
        } else if (mouseEvent.getSource().toString().contains("⚙")) {
            onMouseExited(settingsLabel);
        } else if (mouseEvent.getSource().toString().contains("Save")) {
            onMouseExited(saveLabel);
        } else {
            logo.getStyleClass().remove("logoHovered");
        }
    }
    public void onMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getSource().toString().contains("Total")){
            onMouseClicked(total);
        } else if (mouseEvent.getSource().toString().contains("⚙")) {
            onMouseClicked(settingsLabel);
        } else if (mouseEvent.getSource().toString().contains("Save")) {
            onMouseClicked(saveLabel);
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
            scaleTransition.setToX(0.95);   // Specific for 'total' label
            scaleTransition.setToY(1.0);
        }
        else {
            scaleTransition.setToX(1.1);    // For all date labels
            scaleTransition.setToY(1.1);
        }
        if (label == logo || label == saveLabel) {
            scaleTransition.setFromX(1.1);  // If logo was clicked, reset it to normal size after animation
            scaleTransition.setFromY(1.1);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
        }
        scaleTransition.setNode(label);
        scaleTransition.play();
    }

    private void showSettings() {

        // Remove old elements from screen
        chartVbox.getChildren().clear();

        showChoiceBox();
        showNameChangePanel();
        showColorChangePanel();
        showButton();
    }

    private void showChoiceBox() {

        // Change title
        Label settingsTitle = new Label();
        settingsTitle.setText("Settings");
        settingsTitle.getStyleClass().add("settingsTitle");
        chartVbox.getChildren().add(settingsTitle);
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        // List of all program's name detected by screen time tracker
        ArrayList<String> names = new ArrayList<>();
        usageData.forEach(array -> names.add(array[1]));
        ObservableList<String> namesList = FXCollections.observableList(names);
        choiceBox.setItems(namesList);

        // New HBox ('Program's name' and ChoiceBox)
        HBox hBox = setupBoxAndLabel(choiceBox);

        // Adjust the size of ChoiceBox if selected option's width is greater than prefWidth
        choiceBox.setOnAction(event -> {
            String choice = choiceBox.getValue();
            choiceBox.setMinWidth(choice.length() * 8.1);
        });
        oldName = choiceBox;

        chartVbox.getChildren().add(hBox);
    }

    private static HBox setupBoxAndLabel(ChoiceBox<String> choiceBox) {

        HBox hBox = new HBox();
        Label label = new Label();
        label.setText("Program's name ");
        label.getStyleClass().add("choiceLabel");

        hBox.getChildren().add(label);
        hBox.getChildren().add(choiceBox);
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.setPadding(new Insets(-15, 0, 0, 0));
        return hBox;
    }


    private void showNameChangePanel() {

        // Title label 2
        Label changeNameLabel = new Label();
        changeNameLabel.setText("Change Displayed Name");
        changeNameLabel.getStyleClass().add("settingsTitle");
        chartVbox.setAlignment(Pos.TOP_CENTER);
        chartVbox.getChildren().add(changeNameLabel);

        Label label = new Label();
        label.setText("New name ");
        label.getStyleClass().add("choiceLabel");

        TextField nameInput = new TextField();
        nameInput.getStyleClass().add("nameInput");
        newName = nameInput;

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.setPadding(new Insets(-15, 0, 0, 0));
        hBox.getChildren().addAll(label, nameInput);
        chartVbox.getChildren().add(hBox);

    }

    private void showColorChangePanel() {

        // Title label 2
        Label changeColorLabel = new Label();
        changeColorLabel.setText("Change Color On Chart");
        changeColorLabel.getStyleClass().add("settingsTitle");
        chartVbox.getChildren().add(changeColorLabel);

        Label label = new Label();
        label.setText("New color ");
        label.getStyleClass().add("choiceLabel");

        ColorPicker colorPicker = new ColorPicker();
        this.colorPicker = colorPicker;

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.setPadding(new Insets(-15, 0, 50, 0));
        hBox.getChildren().addAll(label, colorPicker);
        chartVbox.getChildren().add(hBox);

    }

    private void showButton() {

        saveLabel.setText("Save");
        saveLabel.getStyleClass().add("saveLabel");
        saveLabel.setOnMouseEntered(this::onMouseEntered);
        saveLabel.setOnMouseExited(this::onMouseExited);
        saveLabel.setOnMouseClicked(this::onMouseClicked);

        chartVbox.getChildren().add(saveLabel);
    }

    private static String toRGBCode( Color color )  {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }
}
