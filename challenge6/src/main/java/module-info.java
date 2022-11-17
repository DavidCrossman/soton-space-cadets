module com.david.circledetector {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.david.challenge6 to javafx.fxml;
    exports com.david.challenge6;
}