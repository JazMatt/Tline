package org.example.tline.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.scene.input.MouseEvent;


public class Controller1 implements Initializable {
    private int counter = 0;

    @FXML
    private VBox vBox; // VBox for labels (except the 'total' label)
    @FXML
    private Label total; // 'total' label
    @FXML
    private Label logo;
    private ArrayList<String> datesAL; // Array list of databases names
    private ScaleTransition scaleTransition; // animation
    private ArrayList<Label> labels = new ArrayList<>();
    // Array list for labels. Required to delete 'dateClicked' style when another label is clicked

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        datesAL = new ArrayList<>();
        labels.add(total);
        total.getStyleClass().add("dateClicked");
        logo.getStyleClass().add("""
                .animated-gradient {
                  animation: animateBg 14s linear infinite;
                  background-image: linear-gradient(90deg,#ffea00,#fffb94,#ffcf66,#ffea00,#fffb94);
                  background-size: 400% 100%;
                }
                @keyframes animateBg {
                  0% { background-position: 100% 0%; }
                  100% { background-position: 0% 0%; }
                }""");

        // Create scale transition for hover effect
        scaleTransition = new ScaleTransition(Duration.millis(100));

        // Add all files names from databases folder to array list
        String directoryPath = "resources/databases";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            // Format files names, create labels from them, add properties and add to VBox
            for (File file : files) {

                String fileName = file.getName().replace("time-data-", "").replace(".db", "");
                Label label = new Label(fileName);

                label.getStyleClass().add("dateLabel");
                label.setOnMouseEntered(event -> onMouseEntered(label));
                label.setOnMouseExited(event -> onMouseExited(label));
                label.setOnMouseClicked(event -> onMouseClicked(label));
                label.setContentDisplay(ContentDisplay.LEFT);
                label.setTextAlignment(TextAlignment.LEFT);

                vBox.getChildren().add(label);
                labels.add(label);
            }
        }
    }

    private void onMouseEntered(Label label) {
        label.getStyleClass().add("dateHovered");
    }

    private void onMouseExited(Label label) {
        label.getStyleClass().remove("dateHovered");
        label.getStyleClass().add("dateLabel");
    }

    private void onMouseClicked(Label label) {

        // remove the 'dateClicked' style from all labels (works better than saving previously clicked
        // label in private field and removing style from it).
        for (Label currentLabel : labels) {
            currentLabel.getStyleClass().remove("dateClicked");
            currentLabel.getStyleClass().add("dateLabel");
            currentLabel.setScaleX(1.0);
            currentLabel.setScaleY(1.0);
        }

        if (label != logo) {
            // add style to clicked label
            label.getStyleClass().remove("dateLabel");
            label.getStyleClass().remove("dateHovered");
            label.getStyleClass().add("dateClicked");
        }

        scaleTransition.stop();
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.15);
        scaleTransition.setToY(1.15);
        scaleTransition.setFromX(1.15);
        scaleTransition.setFromY(1.15);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        if (label == logo) {
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
        }
        scaleTransition.setNode(label);
        scaleTransition.play();
        if (label == logo) {
            // Redirect to website
            System.out.println("Should be redirected to website");
        }
    }


    // Methods for 'total' and 'logo' labels only
    public void onMouseEntered(MouseEvent mouseEvent) {
        if (mouseEvent.getSource().toString().contains("Total")){
            onMouseEntered(total);
        } else {
            logo.getStyleClass().add("logoHovered");
        }
    }
    public void onMouseExited(MouseEvent mouseEvent) {
        if (mouseEvent.getSource().toString().contains("Total")){
            onMouseExited(total);
        } else {
            logo.getStyleClass().remove("logoHovered");
        }
    }
    public void onMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getSource().toString().contains("Total")){
            onMouseClicked(total);
        } else {
            onMouseClicked(logo);
        }
    }
}
