package application;

import javafx.application.Application;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.Random;

public class SpaceInvaders extends Application {

    private static final Random Rand = new Random();
    private static final int Width = 800;
    private static final int Height = 600;
    private static final int Player_Size = 60;

    static final Image Player_Img = new Image("file:![](../../../../../../Desktop/Saved imgs/25-254127_spaceship-v-enemy-ship-pixel-art-png.png)");
    static final Image Explosion_Img = new Image("file:![](../../../../../../Desktop/Saved imgs/25-254127_spaceship-v-enemy-ship-pixel-art-png.png)");

    static final int Explosion_W = 128;
    static final int Explosion_Rows = 3;
    static final int Explosion_col = 3;
    static final int Explosion_h = 128;
    static final int Explosion_steps = 15;

    static final Image Bombs_img[] = {
            new Image("file:![](../../../../../../Desktop/Saved imgs/25-254127_spaceship-v-enemy-ship-pixel-art-png.png)")
    };

    final int Max_Bombs = 10, Max_shots = Max_Bombs * 2;
    boolean gameOver = false;
    private GraphicsContext gc;

    Rocket player;
    List<Shot> shots;
    List<Universe> univ;
    List<Bomb> Bombs;


    @Override
    public void start(Stage stage) throws Exception {
    }

    //player
    public class Rocket {
        int posX, posY, size;
        boolean exploding, destroyed;
        Image img;
        int explosionStep = 0;

        //cons
        public Rocket(int posX, int posY, int size, Image Image) {
            this.posX = posX;
            this.posY = posY;
            this.size = size;
            img = image;
        }

        public Shot shoot() {
            return new Shot(posX + size / 2 - Shot.size / 2, posY - Shot.size);
        }

        public void update() {
            if (exploding) explosionStep++;
            destroyed = explosionStep > Explosion_steps;
        }

        public draw() {
            if (exploding) {
                gc.drawImage(Explosion_Img, explosionStep % Explosion_col * Explosion_W,
                        (explosionStep / Explosion_Rows) * Explosion_h + 1, Explosion_W, Explosion_h, posX, posY, size, size);
            } else {
                gc.drawImage(img, posX, posY, size, size);
            }
        }

        public boolean colide(Rocket other) {
            int d = distance(this.posX + size / 2, this.posY + size / 2),
            other.posX + other.site / 2, other.posY + other.size / 2);
            return d < other.size / 2 + this.size / 2;
        }

        public void explode() {
            exploding = true;
            explosionStep = -1;
        }
    }

    //computer player
    public class Bomb extends Rocket {
        int Speed = (score / 5) + 2;

        public Bomb(int posX, int posY, int size, Image image) {
            super.update();
            if (!exploding && !destroyed) posY += Speed;
            if (posY > Height) destroyed = true;
        }
    }

    public class Shot {

        public boolean toRemove;

        int posX, posY, speed = 10;
        static final int size = 6;

        public Shot(int posX, int posY) {
            this.posX = posX;
            this.posY = posY;
        }

        public void update() {
            posY -= speed;
        }

        public void draw() {
            gc.setFill(Color.RED);
            if (score >= 50 && score <= 70 || score >= 120) {
                gc.setFill(Color.YELLOWGREEN);
                speed = 50;
                gc.fillRect(posX - 5, posY - 10, size + 10, size + 30);
            } else {
                gc.fillOval(posX, posY, size, size);
            }
        }

        public boolean colide(Rocket rocket) {
            int distance = distance(this.posX + size / 2, this.posY + size / 2,
                    rocket.posX + rocket.size / 2, rocket.posY + rocket.size / 2);
            return distance < Rocket.size / 2 + size / 2;
        }
    }
}
