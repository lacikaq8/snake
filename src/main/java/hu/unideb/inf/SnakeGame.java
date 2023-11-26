package hu.unideb.inf;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.tinylog.Logger;

public class SnakeGame extends Application {

    private static final int SZÉLESSÉG = 700;
    private static final int MAGASSÁG = 700;
    private static final int CELLA_MÉRET = 25;

    @Override
    public void start(Stage primaryStage) {

        Canvas canvas = new Canvas(SZÉLESSÉG, MAGASSÁG); // canvas ures felulet
        GraphicsContext gc = canvas.getGraphicsContext2D(); //meghivom hogy tudjak rajzolni
        StackPane root = new StackPane(canvas); //canvas a gyermekenek allitom
        Scene scene = new Scene(root); //es keszitek egy scenet stackpane altal

        primaryStage.setTitle("Snake Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        Logger.info("Elindult a játék");
        // elinditom a jatekot
        SnakeGameLogic game = new SnakeGameLogic(SZÉLESSÉG, MAGASSÁG, CELLA_MÉRET);
        game.start();

        // felallitom a game loopot
        new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 100_000_000) { // 100ms-onként frissitem a jatekot
                    gc.clearRect(0, 0, SZÉLESSÉG, MAGASSÁG); //tisztitom az ablakom
                    game.update(); // ellenorzom hogy nem halt e meg a kigyo
                    game.render(gc); //ujra rajzolom az ablakom
                    lastUpdate = now;
                }
            }
        }.start();
        //atadom a scenemen leadott gombokat a es ha a megfelelo gombok lettek lenyomva tovabbadom egy osztalynak
        scene.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            if (keyCode == KeyCode.UP || keyCode == KeyCode.DOWN || keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT) {
                game.handleKeyPress(keyCode);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}