module hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires gson;
    requires com.googlecode.lanterna;

    exports IS24_LB11.gui;
    opens IS24_LB11.gui to javafx.fxml;
}