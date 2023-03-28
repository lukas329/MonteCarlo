package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.Random;

public class CircleCounter extends Application {
    private static final int CANVAS_WIDTH = 1400;
    private static final int CANVAS_HEIGHT = 700;

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Label lbl = new Label("POCET");
        lbl.setVisible(true);

        Group root = new Group(canvas);
        Button btnStop = new Button();
        Button btnResume = new Button();

        //Vykreslíme kruh a štvorec
        drawBoundaries(gc);

        //vlákno, ktoré generuje dáta
        DataGenerator dataGenerator = new DataGenerator();
        dataGenerator.start();

        //vlákno na kontrolu, či sa guličky nachádzajú v kruhu alebo štvorci
        Checker checker = new Checker(dataGenerator);
        checker.start();

        //vlákno, ktoré kreslí, guličky
        Painter painter = new Painter(gc, dataGenerator);
        painter.setDaemon(true);
        painter.start();

        primaryStage.setOnCloseRequest(windowEvent -> {
            dataGenerator.killThread();
            checker.killThread();
            painter.killThread();
        });

        //vypísanie pomeru guliečiek každú sekundu
        Timeline tm = new Timeline(new KeyFrame(Duration.millis(1000), e ->{lbl.setText("Počet guličiek v kruhu / počet guličiek vo štvorci: "
                + (float)checker.getOvalCount()/checker.getRectCount() + "  ----  Celkový počet guličiek: " + checker.getIterator());}));
        tm.setCycleCount(Animation.INDEFINITE);
        tm.play();

        //tlačidlo na zastavenie generovania dáts
        btnStop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //preruší vlákno
                dataGenerator.interrupt();
                System.out.println("dg: " + dataGenerator.getState() + "\n Painter: " + painter.getState() + "\n cr: " + checker.getState());
            }
        });

        //tlačidlo na pustenie generovania dát
        btnResume.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //Spúšťa generovanie dát
                dataGenerator.startAgain();
                //zobúdza vlákno na vykreslenie
                painter.wakeUp();
                checker.wakeUp();

                System.out.println("dg: " + dataGenerator.getState() + "\n Painter: " + painter.getState() + "\n cr: " + checker.getState());
            }
        });

        setButtons(btnResume, btnStop, root);
        root.getChildren().add(lbl);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void drawBoundaries(GraphicsContext gc){
        gc.setStroke(Color.RED);
        gc.strokeRect(220, 200, 300, 300);
        gc.strokeOval(630, 50, 600, 600);
    }

    private void setButtons(Button btnResume, Button btnStop, Group root){
        btnResume.setLayoutY(20);
        btnResume.setLayoutX(80);
        btnResume.setText("R E S U M E");
        btnResume.setStyle("-fx-background-color: #06d57b; ");

        btnStop.setLayoutY(20);
        btnStop.setLayoutX(20);
        btnStop.setText("S T O P");
        btnStop.setStyle("-fx-background-color: #990000; ");

        root.getChildren().addAll(btnStop, btnResume);
    }
}
