module rushhour {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    opens rushhour to javafx.fxml;
    exports rushhour;
    exports rushhour.lib;
}