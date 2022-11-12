module com.david.spirograph {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.david.spirograph to javafx.fxml;
    exports com.david.spirograph;
}