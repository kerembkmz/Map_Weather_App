module com.example.map_weather {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires jdk.jsobject;

    opens com.example.map_weather to javafx.fxml;
    exports com.example.map_weather;
}