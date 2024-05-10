@SuppressWarnings("requires-automatic")
module ing.sw {
    requires com.googlecode.lanterna;
    requires gson;
    requires java.sql;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // Export IS24_LB11.gui package to javafx.fxml module
    opens IS24_LB11.gui to javafx.fxml;

    // Export IS24_LB11.gui package to javafx.graphics module
    exports IS24_LB11.gui to javafx.graphics;
    exports IS24_LB11.gui.scenesControllers to javafx.graphics;
    opens IS24_LB11.gui.scenesControllers to javafx.fxml;
}
