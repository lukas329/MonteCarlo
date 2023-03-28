package sample;

public class Checker extends Thread{

    DataGenerator dg;
    int rectCount, ovalCount, iterator;
    private boolean isAlive;

    public Checker(DataGenerator dg){
        this.dg = dg;
        rectCount = ovalCount = 0;
        iterator = 0;
        isAlive = true;
    }

    @Override
    public void run() {
        while (isAlive){
            //Zaistíme, že toto vlákno vlastní monitor pomocou kľúčového slova keyword
            synchronized (this){
                //kým sú dáta, ktoré sme ešte nevykreslili tak pokračujeme
                if (iterator < dg.getListX().size()){
                    if (isInRect(dg.getListX().get(iterator), dg.getListY().get(iterator))) rectCount++;
                    else if (isInCircle(dg.getListX().get(iterator), dg.getListY().get(iterator))) ovalCount++;
                    iterator++;
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //ak už nie sú dáta, vlákno čaká
                else {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    //kontrola, či je gulička vo štvorci
    private Boolean isInRect(double x, double y){
        if (x-2.5 >=220 && x+2.5 < 520 && y-2.5 >= 200 && y+2.5 <= 500)return true;
        return false;
    }
    //kontrola, či je gulička v kruhu
    private Boolean isInCircle(double x, double y){
        if (Math.sqrt((x-930)*(x-930) + (y-350)*(y-350)) < 300) return true;
        return false;
    }
    public void killThread(){
        isAlive = false;
    }
    public int getRectCount(){return rectCount;}
    public int getOvalCount(){return ovalCount;}
    public int getIterator(){return iterator;}
    //zobudíme vlákno
    public void wakeUp(){synchronized (this){notify();}}
}
