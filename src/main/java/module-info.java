module com.example.paint_demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.paint_demo to javafx.fxml;
    exports com.example.paint_demo;
}