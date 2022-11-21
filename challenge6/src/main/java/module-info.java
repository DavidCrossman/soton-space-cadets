module com.david.circledetector {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires webcam.capture;

    opens com.david.challenge6 to javafx.fxml;
    exports com.david.challenge6;
}