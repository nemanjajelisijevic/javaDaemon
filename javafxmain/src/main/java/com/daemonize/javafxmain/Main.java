package com.daemonize.javafxmain;

import images.Image;
import images.JavaFXImage;
import javafx.application.Application;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import renderer.JavaFXRender;


public class Main{} extends Application {

    private Game game;

    @Override
    public void start(Stage primaryStage) {

        int borderX = 1000;
        //int borderY = 200;

        Canvas canvas = new Canvas(1200,1000);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        int rows = 10;
        int columns = 10;

        int width = borderX / (columns + 2);
        int height = width;

        int borderY = (rows + 2) * height;
        //        int width = 160;
        //        int height = 160;



        JavaFXRender renderer = new JavaFXRender(gc,borderX,borderY);
        renderer.setBackgroundImage(new JavaFXImage(new javafx.scene.image.Image("file:assets/maphi.jpg", borderX, borderY, false, false)));

        Image[] sprite = new Image[12];
        int i = 0;
        for (; i < 3; ++i)
            sprite[i] = new JavaFXImage(new javafx.scene.image.Image("file:assets/thebarnstar.png", width, height, false, false));

        for (; i < 6; ++i)
            sprite[i] = new JavaFXImage(new javafx.scene.image.Image("file:assets/thebarnstar90.png", width, height, false, false));


        for (; i < 9; ++i)
            sprite[i] = new JavaFXImage(new javafx.scene.image.Image("file:assets/thebarnstar180.png", width, height, false, false));

        for (; i < 12; ++i)
            sprite[i] = new JavaFXImage(new javafx.scene.image.Image("file:assets/thebarnstar270.png", width, height, false, false));

        int bulletSize = 20;
        Image [] bulletSprite = new Image[4];
        bulletSprite[0] = new JavaFXImage(new javafx.scene.image.Image("file:assets/thebarnstarRed.png", bulletSize, bulletSize, false, false));
        bulletSprite[1] = new JavaFXImage(new javafx.scene.image.Image("file:assets/thebarnstarRed.png", bulletSize, bulletSize, false, false));
        bulletSprite[2] = new JavaFXImage(new javafx.scene.image.Image("file:assets/thebarnstarRed.png", bulletSize, bulletSize, false, false));
        bulletSprite[3] = new JavaFXImage(new javafx.scene.image.Image("file:assets/thebarnstarRed.png", bulletSize, bulletSize, false, false));


        Image [] explosionSprite = new Image[33];

        explosionSprite[0] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion1.png", width, height, false, false));
        explosionSprite[1] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion2.png", width, height, false, false));
        explosionSprite[2] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion3.png", width, height, false, false));
        explosionSprite[3] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion4.png", width, height, false, false));
        explosionSprite[4] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion5.png", width, height, false, false));
        explosionSprite[5] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion6.png", width, height, false, false));
        explosionSprite[6] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion7.png", width, height, false, false));
        explosionSprite[7] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion8.png", width, height, false, false));
        explosionSprite[8] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion9.png", width, height, false, false));
        explosionSprite[9] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion10.png", width, height, false, false));

        explosionSprite[10] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion11.png", width, height, false, false));
        explosionSprite[11] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion12.png", width, height, false, false));
        explosionSprite[12] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion13.png", width, height, false, false));
        explosionSprite[13] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion14.png", width, height, false, false));
        explosionSprite[14] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion15.png", width, height, false, false));
        explosionSprite[15] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion16.png", width, height, false, false));
        explosionSprite[16] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion17.png", width, height, false, false));
        explosionSprite[17] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion18.png", width, height, false, false));
        explosionSprite[18] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion19.png", width, height, false, false));
        explosionSprite[19] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion20.png", width, height, false, false));

        explosionSprite[20] =  new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion21.png", width, height, false, false));
        explosionSprite[21] =  new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion22.png", width, height, false, false));
        explosionSprite[22] =  new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion23.png", width, height, false, false));
        explosionSprite[23] =  new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion24.png", width, height, false, false));
        explosionSprite[24] =  new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion25.png", width, height, false, false));
        explosionSprite[25] =  new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion26.png", width, height, false, false));
        explosionSprite[26] =  new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion27.png", width, height, false, false));
        explosionSprite[27] =  new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion28.png", width, height, false, false));
        explosionSprite[28] =  new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion29.png", width, height, false, false));
        explosionSprite[29] =  new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion30.png", width, height, false, false));

        explosionSprite[30] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion31.png", width, height, false, false));
        explosionSprite[31] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion32.png", width, height, false, false));
        explosionSprite[32] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Explosion33.png", width, height, false, false));

        Image [] towerSprite =new Image[36];
        towerSprite[0] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower0.png", width, height, false, false) );
        towerSprite[1] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower10.png", width, height, false, false));
        towerSprite[2] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower20.png", width, height, false, false));
        towerSprite[3] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower30.png", width, height, false, false));
        towerSprite[4] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower40.png", width, height, false, false));
        towerSprite[5] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower50.png", width, height, false, false));
        towerSprite[6] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower60.png", width, height, false, false));
        towerSprite[7] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower70.png", width, height, false, false));
        towerSprite[8] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower80.png", width, height, false, false));
        towerSprite[9] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower90.png", width, height, false, false));

        towerSprite[10] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower100.png", width, height, false, false));
        towerSprite[11] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower110.png", width, height, false, false));
        towerSprite[12] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower120.png", width, height, false, false));
        towerSprite[13] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower130.png", width, height, false, false));
        towerSprite[14] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower140.png", width, height, false, false));
        towerSprite[15] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower150.png", width, height, false, false));
        towerSprite[16] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower160.png", width, height, false, false));
        towerSprite[17] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower170.png", width, height, false, false));
        towerSprite[18] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower180.png", width, height, false, false));
        towerSprite[19] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower190.png", width, height, false, false));

        towerSprite[20] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower200.png", width, height, false, false));
        towerSprite[21] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower210.png", width, height, false, false));
        towerSprite[22] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower220.png", width, height, false, false));
        towerSprite[23] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower230.png", width, height, false, false));
        towerSprite[24] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower240.png", width, height, false, false));
        towerSprite[25] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower250.png", width, height, false, false));
        towerSprite[26] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower260.png", width, height, false, false));
        towerSprite[27] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower270.png", width, height, false, false));
        towerSprite[28] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower280.png", width, height, false, false));
        towerSprite[29] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower290.png", width, height, false, false));

        towerSprite[30] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower300.png", width, height, false, false));
        towerSprite[31] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower310.png", width, height, false, false));
        towerSprite[32] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower320.png", width, height, false, false));
        towerSprite[33] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower330.png", width, height, false, false));
        towerSprite[34] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower340.png", width, height, false, false));
        towerSprite[35] = new JavaFXImage(new javafx.scene.image.Image("file:assets/Tower350.png", width, height, false, false));

        int width_hp = width;
        int height_hp = height / 5;

        Image [] listHealthBarImg = new Image[10];
        listHealthBarImg[0] = new JavaFXImage(new javafx.scene.image.Image("file:assets/health_bar_10.png", width_hp, height_hp, false, false));
        listHealthBarImg[1] = new JavaFXImage(new javafx.scene.image.Image("file:assets/health_bar_20.png", width_hp, height_hp, false, false));
        listHealthBarImg[2] = new JavaFXImage(new javafx.scene.image.Image("file:assets/health_bar_30.png", width_hp, height_hp, false, false));
        listHealthBarImg[3] = new JavaFXImage(new javafx.scene.image.Image("file:assets/health_bar_40.png", width_hp, height_hp, false, false));
        listHealthBarImg[4] = new JavaFXImage(new javafx.scene.image.Image("file:assets/health_bar_50.png", width_hp, height_hp, false, false));
        listHealthBarImg[5] = new JavaFXImage(new javafx.scene.image.Image("file:assets/health_bar_60.png", width_hp, height_hp, false, false));
        listHealthBarImg[6] = new JavaFXImage(new javafx.scene.image.Image("file:assets/health_bar_70.png", width_hp, height_hp, false, false));
        listHealthBarImg[7] = new JavaFXImage(new javafx.scene.image.Image("file:assets/health_bar_80.png", width_hp, height_hp, false, false));
        listHealthBarImg[8] = new JavaFXImage(new javafx.scene.image.Image("file:assets/health_bar_90.png", width_hp, height_hp, false, false));
        listHealthBarImg[9] = new JavaFXImage(new javafx.scene.image.Image("file:assets/health_bar_100.png", width_hp, height_hp, false, false));

        // renderer.addSceneEventFilter(MouseEvent.MOUSE_PRESSED,event -> game.setTower((float) event.getSceneX(), (float) event.getSceneY()));

        game = new Game(renderer, rows, columns, width, height, width)
                .setFieldImage(new JavaFXImage(new javafx.scene.image.Image("file:assets/green.png", width, height,false, false)))
                .setFieldImageTower(new JavaFXImage(new javafx.scene.image.Image("file:assets/Exceptione.png", width, height,false, false)))
                .setFieldImageTowerDen(new JavaFXImage(new javafx.scene.image.Image("file:assets/red.png", width, height,false, false)))
                .setEnemySprite(sprite)
                .setBulletSprite(bulletSprite)
                .setExplodeSprite(explosionSprite)
                .setTowerSprite(towerSprite)
                .setHealthBarSprite(listHealthBarImg)
                .setBorders(borderX, borderY);


        game.run();

        Group root = new Group(canvas);
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED,event -> game.setTower((float) event.getSceneX(), (float) event.getSceneY()));
    }


    @Override
    public void stop() throws Exception {
        game.stop();
        super.stop();
    }



    public static void main(String[] args) {
        launch(args);
    }
}

