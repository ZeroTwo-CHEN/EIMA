module EIMA {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    opens top.remake to javafx.fxml;
    opens top.remake.controller to javafx.fxml;
    exports top.remake;
}