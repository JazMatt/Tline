package org.example.tline.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.scene.input.MouseEvent;


public class Controller1 implements Initializable {

    @FXML
    private VBox vBox;

    private ArrayList<String> datesAL;

    private ScaleTransition scaleTransition;
    private FadeTransition fadeTransition;
    private Label currentLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        datesAL = new ArrayList<>();

        // Create scale transition for hover effect
        scaleTransition = new ScaleTransition(Duration.millis(100));
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);

        // Create fade transition for click effect
        fadeTransition = new FadeTransition(Duration.millis(200));
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.5);

        // Add all files names from databases folder to array list
        String directoryPath = "resources/databases";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            // Format files names and add them to vBox
            for (File file : files) {
                String fileName = file.getName().replace("time-data-", "").replace(".db", "");
                Label label = new Label(fileName);
                label.getStyleClass().add("dateLabel"); // Add CSS class for styling
                label.setOnMouseEntered(event -> onMouseEntered(label));
                label.setOnMouseExited(event -> onMouseExited(label));
                label.setOnMouseClicked(event -> onMouseClicked(label));
                label.setContentDisplay(ContentDisplay.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                vBox.getChildren().add(label);
            }
        }
    }

    private void onMouseEntered(Label label) {
        scaleTransition.setNode(label);
        scaleTransition.play();
    }

    private void onMouseExited(Label label) {
        scaleTransition.stop();
        scaleTransition.setRate(-1);
        scaleTransition.setNode(label);
        scaleTransition.play();
    }

    private void onMouseClicked(Label label) {
        if (currentLabel != null && currentLabel != label) {
            currentLabel.setStyle("-fx-background-color: transparent;");
        }

        currentLabel = label;

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), label);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.5);
        fadeTransition.play();
    }

    public void onMouseEntered(MouseEvent mouseEvent) {
    }

    public void onMouseExited(MouseEvent mouseEvent) {
    }

    public void onMouseClicked(MouseEvent mouseEvent) {
    }
}
