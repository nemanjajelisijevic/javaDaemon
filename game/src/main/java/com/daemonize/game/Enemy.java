package com.daemonize.game;

import com.daemonize.game.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.game.imagemovers.RotatingSpriteImageMover;
import com.daemonize.game.images.Image;
import com.daemonize.game.scene.views.ImageView;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.Arrays;


@Daemonize(doubleDaemonize = true, daemonizeBaseMethods = true, className = "EnemyDoubleDaemon")
public class Enemy extends CoordinatedImageTranslationMover {

    private ImageView view;
    private ImageView hpView;
    private int hpMax;
    private volatile int hp;
    private volatile boolean shootable = true;
    private Image[] spriteHealthBarImage;

    private Pair<Integer, Integer> previousField;

    @CallingThread
    public Pair<Integer, Integer> getPreviousField() {
        return previousField;
    }

    @CallingThread
    public void setPreviousField(Pair<Integer, Integer> previousField) {
        this.previousField = previousField;
    }

    private RotatingSpriteImageMover rotationMover;

    public Enemy setHealthBarImage(Image[] healthBarImage) {
        this.spriteHealthBarImage = healthBarImage;
        return this;
    }

    @CallingThread
    public boolean isShootable() {
        return shootable;
    }

    @CallingThread
    public void setShootable(boolean shootable) {
        this.shootable = shootable;
    }

    @CallingThread
    public int getHp() {
        return hp;
    }

    @CallingThread
    public void setHp(int hp) {
        this.hp = hp;
    }

    @CallingThread
    public void setMaxHp(int maxHp) {
        this.hpMax = maxHp;
    }

    @CallingThread
    public ImageView getView() {
        return view;
    }

    @CallingThread
    public Enemy setView(ImageView view) {
        this.view = view;
        return this;
    }

    @CallingThread
    public ImageView getHpView() {
        return hpView;
    }

    public Enemy setHpView(ImageView hpView) {
        this.hpView = hpView;
        return this;
    }

    public Enemy(Image[] sprite, float velocity, int hp, Pair<Float, Float> startingPos, float dXY) {
        super(Arrays.copyOf(sprite, 1), velocity, startingPos, dXY);
        this.hp = hp;
        this.hpMax = hp;
        this.rotationMover = new RotatingSpriteImageMover(sprite, animateSemaphore, velocity, startingPos, dXY);
    }

    @GenerateRunnable
    @Override
    public void pushSprite(Image [] sprite, float velocity) throws InterruptedException {
        rotationMover.pushSprite(sprite, velocity);
    }

    public void rotate(int angle) throws InterruptedException {
        rotationMover.rotate(angle);
    }

    @DedicatedThread
    @Override
    public boolean goTo(float x, float y, float velocityInt) throws InterruptedException {
        return super.goTo(x, y, velocityInt);
    }

    @Override
    public Image iterateSprite() {
        return rotationMover.iterateSprite();
    }

    @SideQuest(SLEEP = 25)
    public GenericNode<Pair<PositionedImage, ImageView>> animateEnemy() throws InterruptedException {

        Pair<Float, Float> lastCoord = getLastCoordinates();

        PositionedImage enemyPosBmp = super.animate();

        if (enemyPosBmp == null)
            return null;

        GenericNode<Pair<PositionedImage, ImageView>> root = new GenericNode<>(Pair.create(enemyPosBmp, view));
        PositionedImage hBar = new PositionedImage();
        hBar.image = spriteHealthBarImage[(hp * 100 / hpMax - 1) / spriteHealthBarImage.length];
        hBar.positionX = lastCoord.getFirst();
        hBar.positionY = lastCoord.getSecond() - 2 * hBar.image.getHeight();
        root.addChild(new GenericNode<>(Pair.create(hBar, hpView)));

        return root;
    }
}
