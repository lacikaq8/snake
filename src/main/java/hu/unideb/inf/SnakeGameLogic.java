package hu.unideb.inf;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.Random;

import org.tinylog.Logger;

public class SnakeGameLogic {

    private int szélesség;
    private int magasság;
    private int cellaméret;
    private int pontszám;
    private int kityofejenekakordinatajaX;
    private int kityofejenekakordinatajaY;
    private int kityosebbesegXiranyba;
    private int kityosebbessegYiranba;
    private Étel étel;
    private Random random;
    private LinkedList<Cella> kityoteste;
    private KeyCode JelenlegIrány = KeyCode.RIGHT;


    public SnakeGameLogic(int szélesség, int magasság, int cellaméret) {
        kityoteste = new LinkedList<>();
        this.szélesség = szélesség;
        this.magasság = magasság;
        this.cellaméret = cellaméret;
        this.kityofejenekakordinatajaX = szélesség / 2;
        this.kityofejenekakordinatajaY = magasság / 2;
        this.kityosebbesegXiranyba = 0;
        this.kityosebbessegYiranba = 0;
        this.pontszám = 0;
        random = new Random();
        SpawnÉtelEgyrandomlokációba();
        kityoteste.add(new Cella(kityofejenekakordinatajaX, kityofejenekakordinatajaY));
    }

    private void SpawnÉtelEgyrandomlokációba() {
        int x = RandomKordináta(szélesség);
        int y = RandomKordináta(magasság);
        étel = new Étel(x, y, cellaméret);
        Logger.info("kaja spawn "+x+" kordináta "+y+" kordináta");
    }

    private int RandomKordináta(int limit) {
        return random.nextInt(limit / cellaméret) * cellaméret;
    }

    public void start() {
        kityosebbesegXiranyba = cellaméret;
    }

    public void update() {
        kityofejenekakordinatajaX += kityosebbesegXiranyba;
        kityofejenekakordinatajaY += kityosebbessegYiranba;

        // nekimegy-e a falnak a kigyo
        if (kityofejenekakordinatajaX < 0 || kityofejenekakordinatajaY < 0 || kityofejenekakordinatajaX >= szélesség || kityofejenekakordinatajaY >= magasság) {
            gameOver(pontszám);
            return;
        }

        // ha kajat megette megnovelem a pontszámot és krealok meg kajat
        if (Eszikeakigyo()) {
            pontszám++;
            SpawnÉtelEgyrandomlokációba();
        } else {
            // ha nem eltavolitok a farok cellam
            kityoteste.removeFirst();
        }

        //ha nekimegyek onmagamnak akkor game over
        for (Cella cella : kityoteste) {
            if (cella.getX() == kityofejenekakordinatajaX && cella.getY() == kityofejenekakordinatajaY) {
                gameOver(pontszám);
                return;
            }
        }

        // adunk egy uj fejet a kigyo testere
        kityoteste.addLast(new Cella(kityofejenekakordinatajaX, kityofejenekakordinatajaY));
        //ellenorzom hogy vettem e fel kajat
        checkCollisionWithFood();
    }

    //a kityom eszik e jelenleg
    private boolean Eszikeakigyo() {
        return kityofejenekakordinatajaX == étel.getX() && kityofejenekakordinatajaY == étel.getY();
    }
    //ha vége a játéknak meghivom az osztalyt
    private void gameOver(int pontszám) {
        // delayelem a detektálást
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Game Over");
            alert.setContentText("A végső eredményed az: "+pontszám+" pont");
            alert.showAndWait();
            Logger.info("végső pontszám: "+pontszám);
            Logger.info("vége a játéknak");
        });
        resetGame();
    }
    //minden változót visszállítok
    private void resetGame() {
        kityofejenekakordinatajaX = szélesség / 2;
        kityofejenekakordinatajaY = magasság / 2;
        kityosebbesegXiranyba = 0;
        kityosebbessegYiranba = 0;
        kityoteste.clear();
        kityoteste.add(new Cella(kityofejenekakordinatajaX, kityofejenekakordinatajaY));
        pontszám = 0;
        SpawnÉtelEgyrandomlokációba();
    }

    private void checkCollisionWithFood() {
        if (kityofejenekakordinatajaX == étel.getX() && kityofejenekakordinatajaY == étel.getY()) {
            // ha a kigyo kajanak meg akkor megnovelem a pontszamot és megnovelem a kigyot
            pontszám++;
            growSnake();
            SpawnÉtelEgyrandomlokációba();
        }
    }

    private void growSnake() {
        //kinyerjuk az utolso a kigyonak az utolso cellajat
        Cella lastCella = kityoteste.getLast();

        // nyinálunk uj cellakat a kigyo iranyanyatol fuggoen
        int newCellaX = lastCella.getX() + kityosebbesegXiranyba;
        int newCellaY = lastCella.getY() + kityosebbessegYiranba;
        Cella newCella = new Cella(newCellaX, newCellaY);

        // hozzadjuk a uj cellat a kigyo testéhez
        kityoteste.addLast(newCella);
    }

    public void render(GraphicsContext gc) {
        // itt rajzoltatjuk a kigyot
        for (Cella cella : kityoteste) {
            cella.kigyorajzolása(gc);
        }
        // Megrajzoljuk az ételt
        étel.ÉtelFill(gc);

    }
    //ha legalis iranyt kapunk akkor atalitjuk arra
    public void handleKeyPress(KeyCode keyCode) {
        if (IllegálisIrányváltás(keyCode)) {
            JelenlegIrány = keyCode;
            updateIrany();
        }
    }

    //itt ellenőröm hogy a kítyó ne tudjon magába menni
    private boolean IllegálisIrányváltás(KeyCode newDirection) {
        if (JelenlegIrány == KeyCode.UP && newDirection == KeyCode.DOWN) {
            return false;
        } else if (JelenlegIrány == KeyCode.DOWN && newDirection == KeyCode.UP) {
            return false;
        } else if (JelenlegIrány == KeyCode.LEFT && newDirection == KeyCode.RIGHT) {
            return false;
        } else if (JelenlegIrány == KeyCode.RIGHT && newDirection == KeyCode.LEFT) {
            return false;
        }
        return true;
    }

    private void updateIrany() {
        switch (JelenlegIrány) {
            case UP:
                kityosebbesegXiranyba = 0;
                kityosebbessegYiranba = -cellaméret;
                break;
            case DOWN:
                kityosebbesegXiranyba = 0;
                kityosebbessegYiranba = cellaméret;
                break;
            case LEFT:
                kityosebbesegXiranyba = -cellaméret;
                kityosebbessegYiranba = 0;
                break;
            case RIGHT:
                kityosebbesegXiranyba = cellaméret;
                kityosebbessegYiranba = 0;
                break;
        }

    }
    private class Cella {
        private int x;
        private int y;

        public Cella(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        //draw
        public void kigyorajzolása(GraphicsContext gc) {
            gc.setFill(Color.BLUE);
            gc.fillRect(x, y, cellaméret, cellaméret);
        }
    }



}

