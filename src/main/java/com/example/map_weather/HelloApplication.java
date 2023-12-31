package com.example.map_weather;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import com.example.map_weather.API_Keys;


//Added the fasterxml.jackson.databind to be able to extract the needed information from the response
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//To show the location on a map
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;



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
        String weatherAPIKey = API_Keys.getWeatherAPIKey();
        String positionStackAPIKey = API_Keys.getPositionStackAPIKey();
        try {
            // Encode the location to be URL-safe
            String encodedLocation = URLEncoder.encode(location, "UTF-8");

            // Make an API request to OpenWeatherMap
            String apiUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + encodedLocation + "&appid=" + weatherAPIKey;
            //Got the line above from OpenWeather's site, tested it with Postman.



            URL url = new URL(apiUrl);



            HttpURLConnection connection = (HttpURLConnection) url.openConnection();



            connection.setRequestMethod("GET");

            //Should be GET since it was GET in postman.

            //System.out.println(connection.getResponseCode());
                //Returns HTTP Status Code
                    //Returns 200 for Antalya, gives file not found error for a.

            if (connection.getResponseCode() == 200) {
                //Do nothing
            } else {
                showErrorMessage(connection.getResponseCode());
            }


            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(connection.getInputStream());

            JsonNode coordNode = jsonResponse.get("coord");
            double lon = coordNode.get("lon").asDouble();
            double lat = coordNode.get("lat").asDouble();

            String apiUrl2 = "http://api.positionstack.com/v1/reverse?access_key=" + positionStackAPIKey + "&query="+ lat + "," + lon;
            URL url2 = new URL(apiUrl2);
            HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
            connection2.setRequestMethod("GET");

            JsonNode positionURL = objectMapper.readTree(connection2.getInputStream());

            //String mapUrl = positionURL.get("data").get("results").get(0).get("map_url").toString();



            //System.out.println(connection2.getResponseCode());
                //Checked to see if there were any connection issues, but got 200.








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


            String label = positionURL.get("data").get(0).get("label").asText();
            Hyperlink googleMapsLink = new Hyperlink("Open in Google Maps");
            googleMapsLink.setCursor(Cursor.HAND);
            googleMapsLink.setOnAction(event -> {
                try {
                    String googleMapsUrl = "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(label, "UTF-8");
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create(googleMapsUrl));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            vbox.getChildren().add(googleMapsLink);;




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

    private void showErrorMessage(int statusCode) {
        String errorMessage = null;
        if (String.valueOf(statusCode).startsWith("3")){
            errorMessage = "Redirection";
        }
        if (String.valueOf(statusCode).startsWith("4")) {
            errorMessage = "Client-side error";
        } else if (String.valueOf(statusCode).startsWith("5")) {
            errorMessage = "Server-side error";
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("HTTP ERROR");
        alert.setHeaderText("HTTP Error Occured");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }


}
