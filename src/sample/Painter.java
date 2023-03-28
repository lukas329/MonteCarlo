package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Painter extends Thread{

    private final GraphicsContext gc;
    private final DataGenerator dg;
    double radius;
    int countAll;
    public Painter(GraphicsContext gc, DataGenerator dg) {
        this.gc = gc;
        radius = 5;
        this.dg = dg;
        countAll = 0;
    }

    @Override
    public void run() {
        gc.setFill(Color.BLACK);
        while (true) {
            //Zaistíme, že toto vlákno vlastní monitor pomocou kľúčového slova keyword
            synchronized (this) {
                //ide, kým sú dáta, ktoré neboli skontrolované
                if (countAll < dg.getListX().size()){
                    gc.fillOval(dg.getListX().get(countAll), dg.getListY().get(countAll), radius, radius);
                    countAll++;
                    System.out.println("Vykresľujem guličky!");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    //ak už nie sú dáta čaká
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    //zobudí vlákno
    public void wakeUp(){
        synchronized (this){notify();}
    }
}
