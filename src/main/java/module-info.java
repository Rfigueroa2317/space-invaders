module com.firstpractice.spaceinvaders {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.firstpractice.spaceinvaders to javafx.fxml;
    exports com.firstpractice.spaceinvaders;
}