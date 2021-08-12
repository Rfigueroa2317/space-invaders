package application;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Random;

public class SpaceInvaders extends Application {

    private static final Random Rand = new Random();
    private static final int Width = 800;
    private static final int Height = 600;
    private static final int Player_Size = 60;

    static final Image Player_Img = new Image("file:");

    @Override
    public void start(Stage stage) throws Exception {

    }
}
