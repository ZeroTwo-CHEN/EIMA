module EIMA {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.graphics;
    requires org.controlsfx.controls;
    requires rxcontrols;
    requires thumbnailator;

    opens top.remake to javafx.fxml;
    opens top.remake.controller to javafx.fxml;
    exports top.remake;
}