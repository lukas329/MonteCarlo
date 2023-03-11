package sample;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

public class DataGenerator extends Thread{
    private static final int CANVAS_WIDTH = 1400;
    private static final int CANVAS_HEIGHT = 700;
    private boolean threadGo;
    public ArrayList<Double>listX;
    public ArrayList<Double>listY;

    public DataGenerator(){
        listX = new ArrayList<>();
        listY = new ArrayList<>();
        threadGo = true;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            //vygenerujeme x a y suradnicu a pridáme do listu
            double x = random.nextDouble() * CANVAS_WIDTH;
            double y = 40 + random.nextDouble() * (CANVAS_HEIGHT-40);
            listX.add(x);
            listY.add(y);
            try {
                sleep(90);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Zaistíme, že toto vlákno vlastní monitor pomocou kľúčového slova keyword
            synchronized (this){
                while (!threadGo){
                    try {
                        System.out.println("cakam");
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //preruší generovanie dát
    @Override
    public void interrupt() {
       threadGo  = false;
    }

    @Override
    public boolean isInterrupted() {
        return !threadGo;
    }

    //znova začne generovať a zobudí vlákno
    public void startAgain(){
        threadGo = true;
        synchronized (this){notify();}

    }

    public ArrayList<Double> getListX(){
        return listX;
    }
    public ArrayList<Double> getListY(){
        return listY;
    }
}
