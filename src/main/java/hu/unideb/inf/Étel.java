package hu.unideb.inf;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Étel {

    private int x;
    private int y;
    private int méret;

    public Étel(int x, int y, int méret) {
        this.x = x;
        this.y = y;
        this.méret = méret;
    }

    public void ÉtelFill(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillRect(x, y, méret, méret);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getMéret() {
        return méret;
    }
}