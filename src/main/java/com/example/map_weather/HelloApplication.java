package com.example.map_weather;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

//Added the fasterxml.jackson.databind to be able to extract the needed information from the response
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



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
        stage.setTitle("Weather App with Map");
        stage.setScene(scene);
        stage.show();
    }

    // Retrieve weather data from OpenWeatherMap API
    private void retrieveWeatherData(String location) {
        try {
            // Encode the location to be URL-safe
            String encodedLocation = URLEncoder.encode(location, "UTF-8");

            // Make an API request to OpenWeatherMap
            String apiUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + encodedLocation + "&appid=" + "da2bccf21d94fc2ad1dbbfb9ab756a1b";
            //Got the line above from OpenWeather's site, tested it with Postman.

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //Should be GET since it was GET in postman.

            //System.out.println(connection.getResponseCode());
                //Returns HTTP Status Code
                    //Returns 200 for Antalya, gives file not found error for a.

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(connection.getInputStream());

            JsonNode coordNode = jsonResponse.get("coord");
            double lon = coordNode.get("lon").asDouble();
            double lat = coordNode.get("lat").asDouble();

            //System.out.println("lon: " + lon + ", lat: " + lat);
                //Printed longitude and latitude to make sure they are working.
                    //Will use them later to get to the location using the Google Maps API.

            JsonNode weatherNode = jsonResponse.get("weather");
            String description = weatherNode.get(0).get("description").asText();

            JsonNode mainNode = jsonResponse.get("main");
            double tempInKelvin = mainNode.get("temp").asDouble();
            double temp = tempInKelvin - 273.15; //Converted Kelvin to Celcius
            double feelsLikeInKelvin = mainNode.get("feels_like").asDouble();
            double feelsLike = feelsLikeInKelvin - 273.15; //Converted Kelvin to Celcius
            int humidity = mainNode.get("humidity").asInt();

            JsonNode windNode = jsonResponse.get("wind");
            double speed = windNode.get("speed").asDouble();



            //System.out.println("Description: " + description);
            //System.out.println("Temperature: " + temp);
            //System.out.println("Feels Like: " + feelsLike);
            //System.out.println("Humidity: " + humidity);
            //System.out.println("Wind Speed: " + speed);
                //Tested to see if the outputs are correct.

            // Create a TextArea to display the weather data
            TextArea textArea = new TextArea();

            // Create a VBox to hold the TextAreas
            VBox vbox = new VBox(10);

            TextArea DescriptionTextArea = createTextArea(description,150, 100);
            vbox.getChildren().add(DescriptionTextArea);

            TextArea TemperatureTextArea = createTextArea("Temperature is: " + Double.toString(temp),150, 100);
            vbox.getChildren().add(TemperatureTextArea);

            TextArea FeelsLikeTextArea = createTextArea("Temperature feels like: " + Double.toString(feelsLike),150, 100);
            vbox.getChildren().add(FeelsLikeTextArea);

            TextArea HumidityTextArea = createTextArea("Humidity is: " + Integer.toString(humidity),150, 100);
            vbox.getChildren().add(HumidityTextArea);

            TextArea WindSpeedTextArea = createTextArea("Wind Speed is: " + Double.toString(speed),150, 100);
            vbox.getChildren().add(WindSpeedTextArea);

            // Create and configure the alert pop-up
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Weather Data");
            alert.setHeaderText(location);
            alert.getDialogPane().setContent(vbox);

            // Add padding to the alert window
            alert.getDialogPane().setPadding(new Insets(10, 20, 10, 20));

            // Show the alert pop-up
            alert.showAndWait();

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private TextArea createTextArea(String text,double prefWidth, double prefHeight) {
        TextArea textArea = new TextArea();
        textArea.setText(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(prefWidth);
        textArea.setPrefHeight(prefHeight);
        return textArea;
    }


}
