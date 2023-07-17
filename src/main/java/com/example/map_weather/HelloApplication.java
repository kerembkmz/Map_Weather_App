package com.example.map_weather;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class HelloApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {

        // Create the TextField for user input
        TextField locationInput = new TextField();
        locationInput.setPromptText("Enter location");

        // Create the Button to retrieve weather data and add marker to the map
        Button retrieveButton = new Button("Retrieve Weather Data");
        retrieveButton.setOnAction(e -> {
            String location = locationInput.getText();
            retrieveWeatherData(location);

        });

        // Create the root layout
        VBox root = new VBox(10, locationInput, retrieveButton);
        root.setSpacing(10);

        // Set the Scene with the root layout
        Scene scene = new Scene(root);

        // Set the title and show the Stage
        stage.setTitle("Weather App With Map");
        stage.setScene(scene);
        stage.show();
    }

    // Retrieve weather data from OpenWeatherMap API
    private void retrieveWeatherData(String location) {
        System.out.println("Will get the weather data for " + location);
    }
}
