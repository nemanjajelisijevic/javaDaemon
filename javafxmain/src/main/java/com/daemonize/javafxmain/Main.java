package com.daemonize.javafxmain;

import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.game.Game;

import com.daemonize.game.images.Image;
import com.daemonize.game.images.imageloader.ImageLoader;
import com.daemonize.game.renderer.Renderer2D;

import java.io.IOException;

import javafx.application.Application;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


public class Main extends Application {

    private Game game;

    @Override
    public void start(Stage primaryStage) {

        int borderX = 1200;
        //int borderY = 200;

        int rows = 6;
        int columns = 9;

        int gridWidth = (borderX * 70) / 100;

        int width = gridWidth/columns;
        int height = width; //160

        int borderY = (rows + 2) * height;

        Canvas canvas = new Canvas(borderX, borderY);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Renderer2D renderer = new JavaFXRenderer(gc);
        ImageLoader imageLoader = new JavaFxImageLoader("javafxmain/assets/");

        try {
            //init enemy sprite
            Image[] sprite = new Image[36];

            for (int i = 0; i < 36; i++)
                sprite[i] = imageLoader.loadImageFromAssets("plane" + Integer.toString(i) + "0.png", width, height);

            //bullet sprite
            int bulletSize0 = width / 8;//20;
            Image[] bulletSprite = new Image[4];
            bulletSprite[0] = imageLoader.loadImageFromAssets("thebarnstarRed.png", bulletSize0, bulletSize0);
            bulletSprite[1] = imageLoader.loadImageFromAssets("thebarnstarRed90.png", bulletSize0, bulletSize0);
            bulletSprite[2] = imageLoader.loadImageFromAssets("thebarnstarRed180.png", bulletSize0, bulletSize0);
            bulletSprite[3] = imageLoader.loadImageFromAssets("thebarnstarRed270.png", bulletSize0, bulletSize0);

            int bulletSize = width / 3;//60;
            Image[] spriteRocket = new Image[36];

            for (int i = 0; i < 36; i++)
                spriteRocket[i] = imageLoader.loadImageFromAssets("rocket" + Integer.toString(i) + "0.png", bulletSize, bulletSize);

            //explosion sprite
            Image[] explosionSprite = new Image[33];
            explosionSprite[0] = imageLoader.loadImageFromAssets("Explosion1.png", width, height);
            explosionSprite[1] = imageLoader.loadImageFromAssets("Explosion2.png", width, height);
            explosionSprite[2] = imageLoader.loadImageFromAssets("Explosion3.png", width, height);
            explosionSprite[3] = imageLoader.loadImageFromAssets("Explosion4.png", width, height);
            explosionSprite[4] = imageLoader.loadImageFromAssets("Explosion5.png", width, height);
            explosionSprite[5] = imageLoader.loadImageFromAssets("Explosion6.png", width, height);
            explosionSprite[6] = imageLoader.loadImageFromAssets("Explosion7.png", width, height);
            explosionSprite[7] = imageLoader.loadImageFromAssets("Explosion8.png", width, height);
            explosionSprite[8] = imageLoader.loadImageFromAssets("Explosion9.png", width, height);
            explosionSprite[9] = imageLoader.loadImageFromAssets("Explosion10.png", width, height);

            explosionSprite[10] = imageLoader.loadImageFromAssets("Explosion11.png", width, height);
            explosionSprite[11] = imageLoader.loadImageFromAssets("Explosion12.png", width, height);
            explosionSprite[12] = imageLoader.loadImageFromAssets("Explosion13.png", width, height);
            explosionSprite[13] = imageLoader.loadImageFromAssets("Explosion14.png", width, height);
            explosionSprite[14] = imageLoader.loadImageFromAssets("Explosion15.png", width, height);
            explosionSprite[15] = imageLoader.loadImageFromAssets("Explosion16.png", width, height);
            explosionSprite[16] = imageLoader.loadImageFromAssets("Explosion17.png", width, height);
            explosionSprite[17] = imageLoader.loadImageFromAssets("Explosion18.png", width, height);
            explosionSprite[18] = imageLoader.loadImageFromAssets("Explosion19.png", width, height);
            explosionSprite[19] = imageLoader.loadImageFromAssets("Explosion20.png", width, height);

            explosionSprite[20] = imageLoader.loadImageFromAssets("Explosion21.png", width, height);
            explosionSprite[21] = imageLoader.loadImageFromAssets("Explosion22.png", width, height);
            explosionSprite[22] = imageLoader.loadImageFromAssets("Explosion23.png", width, height);
            explosionSprite[23] = imageLoader.loadImageFromAssets("Explosion24.png", width, height);
            explosionSprite[24] = imageLoader.loadImageFromAssets("Explosion25.png", width, height);
            explosionSprite[25] = imageLoader.loadImageFromAssets("Explosion26.png", width, height);
            explosionSprite[26] = imageLoader.loadImageFromAssets("Explosion27.png", width, height);
            explosionSprite[27] = imageLoader.loadImageFromAssets("Explosion28.png", width, height);
            explosionSprite[28] = imageLoader.loadImageFromAssets("Explosion29.png", width, height);
            explosionSprite[29] = imageLoader.loadImageFromAssets("Explosion30.png", width, height);

            explosionSprite[30] = imageLoader.loadImageFromAssets("Explosion31.png", width, height);
            explosionSprite[31] = imageLoader.loadImageFromAssets("Explosion32.png", width, height);
            explosionSprite[32] = imageLoader.loadImageFromAssets("Explosion33.png", width, height);

            int miniWidth = width / 3;
            int miniHeight = height / 3;

            Image[] miniExplosionSprite = new Image[20];
            miniExplosionSprite[0] = imageLoader.loadImageFromAssets("Bild-000001.png", miniWidth, miniHeight);
            miniExplosionSprite[1] = imageLoader.loadImageFromAssets("Bild-000002.png", miniWidth, miniHeight);
            miniExplosionSprite[2] = imageLoader.loadImageFromAssets("Bild-000003.png", miniWidth, miniHeight);
            miniExplosionSprite[3] = imageLoader.loadImageFromAssets("Bild-000004.png", miniWidth, miniHeight);
            miniExplosionSprite[4] = imageLoader.loadImageFromAssets("Bild-000005.png", miniWidth, miniHeight);
            miniExplosionSprite[5] = imageLoader.loadImageFromAssets("Bild-000006.png", miniWidth, miniHeight);
            miniExplosionSprite[6] = imageLoader.loadImageFromAssets("Bild-000007.png", miniWidth, miniHeight);
            miniExplosionSprite[7] = imageLoader.loadImageFromAssets("Bild-000008.png", miniWidth, miniHeight);
            miniExplosionSprite[8] = imageLoader.loadImageFromAssets("Bild-000009.png", miniWidth, miniHeight);
            miniExplosionSprite[9] = imageLoader.loadImageFromAssets("Bild-0000010.png", miniWidth, miniHeight);

            miniExplosionSprite[10] = imageLoader.loadImageFromAssets("Bild-000011.png", miniWidth, miniHeight);
            miniExplosionSprite[11] = imageLoader.loadImageFromAssets("Bild-000012.png", miniWidth, miniHeight);
            miniExplosionSprite[12] = imageLoader.loadImageFromAssets("Bild-000013.png", miniWidth, miniHeight);
            miniExplosionSprite[13] = imageLoader.loadImageFromAssets("Bild-000014.png", miniWidth, miniHeight);
            miniExplosionSprite[14] = imageLoader.loadImageFromAssets("Bild-000015.png", miniWidth, miniHeight);
            miniExplosionSprite[15] = imageLoader.loadImageFromAssets("Bild-000016.png", miniWidth, miniHeight);
            miniExplosionSprite[16] = imageLoader.loadImageFromAssets("Bild-000017.png", miniWidth, miniHeight);
            miniExplosionSprite[17] = imageLoader.loadImageFromAssets("Bild-000018.png", miniWidth, miniHeight);
            miniExplosionSprite[18] = imageLoader.loadImageFromAssets("Bild-000019.png", miniWidth, miniHeight);
            miniExplosionSprite[19] = imageLoader.loadImageFromAssets("Bild-000020.png", miniWidth, miniHeight);

            // blue tower
            Image[] blueTowerI = new Image[36];
            for (int i = 0; i < 36; i++)
                blueTowerI[i] = imageLoader.loadImageFromAssets("mg" + i + "0.png", width, height);

            Image[] blueTowerII = new Image[36];
            for (int i = 0; i < 36; i++)
                blueTowerII[i] = imageLoader.loadImageFromAssets("bgII" + i + "0.png", width, height);

            Image[] blueTowerIII = new Image[36];
            for (int i = 0; i < 36; i++)
                blueTowerIII[i] = imageLoader.loadImageFromAssets("bgIII" + i + "0.png", width, height);

            //green tower
            Image[] greenTowerI = new Image[36];
            for (int i = 0; i < 36; i++)
                greenTowerI[i] = imageLoader.loadImageFromAssets("greenLS00" + i + "0.png", width, height);

            Image[] greenTowerII = new Image[36];
            for (int i = 0; i < 36; i++)
                greenTowerII[i] = imageLoader.loadImageFromAssets("lsII" + i + "0.png", width, height);

            Image[] greenTowerIII = new Image[36];
            for (int i = 0; i < 36; i++)
                greenTowerIII[i] = imageLoader.loadImageFromAssets("lsIII" + i + "0.png", width, height);

            //red tower
            Image[] redTowerI = new Image[36];
            for (int i = 0; i < 36; i++)
                redTowerI[i] = imageLoader.loadImageFromAssets("rmI" + i + "0.png", width, height);

            Image[] redTowerII = new Image[36];
            for (int i = 0; i < 36; i++)
                redTowerII[i] = imageLoader.loadImageFromAssets("rmII" + i + "0.png", width, height);

            Image[] redTowerIII = new Image[36];
            for (int i = 0; i < 36; i++)
                redTowerIII[i] = imageLoader.loadImageFromAssets("rmIII" + i + "0.png", width, height);

            int width_hp = (width * 3) / 4; //120;
            int height_hp = height / 5;//30;

            Image[] listHealthBarImg = new Image[10];
            listHealthBarImg[0] = imageLoader.loadImageFromAssets("health_bar_10.png", width_hp, height_hp);
            listHealthBarImg[1] = imageLoader.loadImageFromAssets("health_bar_20.png", width_hp, height_hp);
            listHealthBarImg[2] = imageLoader.loadImageFromAssets("health_bar_30.png", width_hp, height_hp);
            listHealthBarImg[3] = imageLoader.loadImageFromAssets("health_bar_40.png", width_hp, height_hp);
            listHealthBarImg[4] = imageLoader.loadImageFromAssets("health_bar_50.png", width_hp, height_hp);
            listHealthBarImg[5] = imageLoader.loadImageFromAssets("health_bar_60.png", width_hp, height_hp);
            listHealthBarImg[6] = imageLoader.loadImageFromAssets("health_bar_70.png", width_hp, height_hp);
            listHealthBarImg[7] = imageLoader.loadImageFromAssets("health_bar_80.png", width_hp, height_hp);
            listHealthBarImg[8] = imageLoader.loadImageFromAssets("health_bar_90.png", width_hp, height_hp);
            listHealthBarImg[9] = imageLoader.loadImageFromAssets("health_bar_100.png", width_hp, height_hp);

            Image score = imageLoader.loadImageFromAssets("SmallBox.png", 300, 150);
            Image titleScore = imageLoader.loadImageFromAssets("HealthBar.png", 300, 70);

            int numWidth = width / 3;//50;
            int numHeight = width / 2;//70;

            Image[] listNumberImg = new Image[10];

            listNumberImg[0] = imageLoader.loadImageFromAssets("0.png", numWidth, numHeight);
            listNumberImg[1] = imageLoader.loadImageFromAssets("1.png", numWidth, numHeight);
            listNumberImg[2] = imageLoader.loadImageFromAssets("2.png", numWidth, numHeight);
            listNumberImg[3] = imageLoader.loadImageFromAssets("3.png", numWidth, numHeight);
            listNumberImg[4] = imageLoader.loadImageFromAssets("4.png", numWidth, numHeight);
            listNumberImg[5] = imageLoader.loadImageFromAssets("5.png", numWidth, numHeight);
            listNumberImg[6] = imageLoader.loadImageFromAssets("6.png", numWidth, numHeight);
            listNumberImg[7] = imageLoader.loadImageFromAssets("7.png", numWidth, numHeight);
            listNumberImg[8] = imageLoader.loadImageFromAssets("8.png", numWidth, numHeight);
            listNumberImg[9] = imageLoader.loadImageFromAssets("9.png", numWidth, numHeight);

            Image[] dialogUpgradeTower1 = new Image[3];

            dialogUpgradeTower1[0] = imageLoader.loadImageFromAssets("mgleve2.png", 800, 530);
            dialogUpgradeTower1[1] = imageLoader.loadImageFromAssets("mgleve3.png", 800, 530);
            dialogUpgradeTower1[2] = imageLoader.loadImageFromAssets("mgleveTOP.png", 800, 530);

            Image[] dialogUpgradeTower2 = new Image[3];
            dialogUpgradeTower2[0] = imageLoader.loadImageFromAssets("rcleve2.png", 800, 530);
            dialogUpgradeTower2[1] = imageLoader.loadImageFromAssets("rcleve3.png", 800, 530);
            dialogUpgradeTower2[2] = imageLoader.loadImageFromAssets("rcleveTOP.png", 800, 530);

            Image[] dialogUpgradeTower3 = new Image[3];
            dialogUpgradeTower3[0] = imageLoader.loadImageFromAssets("lsleve2.png", 800, 530);
            dialogUpgradeTower3[1] = imageLoader.loadImageFromAssets("lsleve3.png", 800, 530);
            dialogUpgradeTower3[2] = imageLoader.loadImageFromAssets("lsleveTOP.png", 800, 530);

            game = new Game(renderer, rows, columns,50,50, width)
                    .setBackgroundImage(imageLoader.loadImageFromAssets("maphi.jpg", borderX, borderY))
                    .setFieldImage(imageLoader.loadImageFromAssets("green.png", width, height))
                    .setFieldImageTower(imageLoader.loadImageFromAssets("Exceptione.png", width, height))
                    .setFieldImageTowerDen(imageLoader.loadImageFromAssets("red.png", width, height))
                    .setEnemySprite(sprite)
                    .setBulletSprite(bulletSprite)
                    .setBulletSpriteRocket(spriteRocket)
                    .setLaserSprite(new Image[] {imageLoader.loadImageFromAssets("greenPhoton.png", 10, 10)})
                    .setExplodeSprite(explosionSprite)
                    .setMiniExplodeSprite(miniExplosionSprite)
                    .setRedTower(redTowerI, redTowerII, redTowerIII)
                    .setBlueTower(blueTowerI, blueTowerII, blueTowerIII)
                    .setGreenTower(greenTowerI, greenTowerII, greenTowerIII)
                    .setHealthBarSprite(listHealthBarImg)
                    .setBorders(borderX, borderY)
                    .setUpgradeTowerDialogue(dialogUpgradeTower1, dialogUpgradeTower2, dialogUpgradeTower3)
                    .setUpgradeButtonImage(imageLoader.loadImageFromAssets("ButtonUpgrade.png",350,90))
                    .setCloseButtonImage(imageLoader.loadImageFromAssets("ButtonX.png",110,120))
                    .setSaleButtonImage(imageLoader.loadImageFromAssets("ButtonSale.png",250,90))
                    .setSelectionImage(imageLoader.loadImageFromAssets("green.png", 200, 200))
                    .setDeselectionImage(imageLoader.loadImageFromAssets("red.png", 200, 200))
                    .setScoreBackGrImage(score)
                    .setScorenumbersImages(listNumberImg);


            Group root = new Group(canvas);
            primaryStage.setTitle("Tower Defense");
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

            scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> game.onTouch((float) event.getSceneX(), (float) event.getSceneY()));

            if(!game.isRunning())
                game.run();

        } catch (IOException ex) {
            System.err.println(DaemonUtils.tag() + "Could not init game!");
            ex.printStackTrace();
        }
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

