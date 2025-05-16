module rush.hour {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    opens rush.hour to javafx.fxml;
    exports rush.hour;
}
