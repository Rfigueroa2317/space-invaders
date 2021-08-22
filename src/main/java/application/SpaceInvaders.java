package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

// CODE IS FINISHED BUT WON'T RUN. NEED TO FIGURE OUT HOW TO RUN IT \\

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

    private double mouseX;
    private int score;


    //start
    public void start(Stage stage) throws Exception {
        Canvas canvas = new Canvas(Width, Height);
        gc = canvas.getGraphicsContext2D();
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> run(gc)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        canvas.setCursor(Cursor.MOVE);
        canvas.setOnMouseMoved(e -> mouseX = e.getX());
        canvas.setOnMouseClicked(e -> {
            if (shots.size() < Max_shots) shots.add(player.shoot());
            if (gameOver) {
                gameOver = false;
                setup();
            }
        });
        setup();
        stage.setScene(new Scene(new StackPane(canvas)));
        stage.setTitle("Space invaders");
        stage.show();
    }

    //setup the game
    private void setup() {
        univ = new ArrayList<>();
        shots = new ArrayList<>();
        Bombs = new ArrayList<>();
        player = new Rocket(Width / 2, Height - Player_Size, Player_Size, Player_Img);
        score = 0;
        IntStream.range(0, Max_Bombs).mapToObj(i -> this.newBomb()).forEach(Bombs::add);
    }

    //run Graphics
    private void run(GraphicsContext gc) {
        gc.setFill(Color.grayRgb(20));
        gc.fillRect(0, 0, Width, Height);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font(20));
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 60, 20);

        if (gameOver) {
            gc.setFont(Font.font(35));
            gc.setFill(Color.YELLOW);
            gc.fillText("Game Over \n Your Score is: " + score + "\n Click to play again",
                    Width / 2, Height / 2.5);
        }
        univ.forEach(Universe::draw);

        player.update();
        player.draw();
        player.posX = (int) mouseX;

        Bombs.stream().peek(Rocket::draw).forEach(e -> {
            if (player.colide(e) && !player.exploding) {
                player.explode();
            }
        });

        for (int i = shots.size() - 1; i >= 0; i--) {
            Shot shot = shots.get(i);
            if (shot.posY < 0 || shot.toRemove) {
                shots.remove(i);
                continue;
            }
            shot.update();
            shot.draw();
            for (Bomb bomb : Bombs) {
                if (shot.colide(bomb) && !bomb.exploding) {
                    score++;
                    bomb.explode();
                    shot.toRemove = true;
                }
            }
        }

        for (int i = Bombs.size() - 1; i >= 0; i--) {
            if (Bombs.get(i).destroyed) {
                Bombs.set(i, newBomb());
            }
        }

        gameOver = player.destroyed;
        if (Rand.nextInt(10) > 2) {
            univ.add(new Universe());
        }
        for (int i = 0; i < univ.size(); i++) {
            if (univ.get(i).posY > Height)
                univ.remove(i);
        }
    }


    //player
    public class Rocket {
        int posX, posY, size;
        boolean exploding, destroyed;
        Image img;
        int explosionStep = 0;

        //cons
        public Rocket(int posX, int posY, int size, Image image) {
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

        public void draw() {
            if (exploding) {
                gc.drawImage(Explosion_Img, explosionStep % Explosion_col * Explosion_W,
                        (explosionStep / Explosion_Rows) * Explosion_h + 1, Explosion_W, Explosion_h, posX, posY, size, size);
            } else {
                gc.drawImage(img, posX, posY, size, size);
            }
        }

        public boolean colide(Rocket other) {
            int d = distance(this.posX + size / 2, this.posY + size / 2,
            other.posX + other.size / 2, other.posY + other.size / 2);
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
            super(posX, posY, size, image);
        }

        public void update(){
            if (!exploding && !destroyed) posY += Speed;
            if (posY > Height) destroyed = true;
        }
    }

    //bullets
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
            return distance < rocket.size / 2 + size / 2;
        }
    }

    //environment
    public class Universe {
        int posX, posY;
        private int h, w, r, g, b;
        private double opacity;

        public Universe() {
            posX = Rand.nextInt(Width);
            posY = 0;
            w = Rand.nextInt(5) + 1;
            h = Rand.nextInt(5) + 1;
            r = Rand.nextInt(100) + 150;
            g = Rand.nextInt(100) + 150;
            b = Rand.nextInt(100) + 150;
            opacity = Rand.nextFloat();
            if (opacity < 0) opacity *= -1;
            if (opacity > 0.5) opacity = 0.5;
        }

        public void draw() {
            if (opacity > 0.8) opacity -= 0.01;
            if (opacity < 0.1) opacity += 0.01;
            gc.setFill(Color.rgb(r, g, b, opacity));
            gc.fillOval(posX, posY, w, h);
            posY += 20;
        }
    }

    Bomb newBomb() {
        return new Bomb(50 + Rand.nextInt(Width - 100), 0, Player_Size,
                Bombs_img[Rand.nextInt(Bombs_img.length)]);
    }

    int distance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static void main(String[] args) {
        launch();
    }
}
