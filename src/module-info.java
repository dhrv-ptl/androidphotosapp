module PhotosXX {
    requires javafx.controls;
    requires javafx.fxml;

    exports app;
    exports controller;
    exports model;

    opens app to javafx.graphics;
    opens controller to javafx.fxml;
}
