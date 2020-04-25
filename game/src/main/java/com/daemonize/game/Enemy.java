package com.daemonize.game;

import com.daemonize.daemonengine.utils.DaemonCountingSemaphore;
import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.AwaitedClosure;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.Exclude;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.imagemovers.RotatingSpriteImageMover;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.Arrays;


@Daemon(doubleDaemonize = true, className = "EnemyDoubleDaemon", implementPrototypeInterfaces = true)
public class Enemy extends CoordinatedImageTranslationMover implements Target<Enemy> , Paralyzable<Enemy>{

    private ImageView view;
    private ImageView hpView;
    private ImageView targetView;
    private ImageView paralyzedView;

    private volatile int hpMax;
    private volatile int hp;
    private volatile boolean shootable = true;
    private Image[] spriteHealthBarImage;

    private boolean paralyzed = false;

    private volatile Target target;
    private DaemonSemaphore targetSemaphore = new DaemonSemaphore().setName("Enemy Target semaphore");

    private PositionedImage hBar = new PositionedImage();
    private PositionedImage paralyzedPosImage = new PositionedImage();

    public Enemy(Image[] sprite, int hp, Pair<Float, Float> startingPos, float dXY) {
        super(Arrays.copyOf(sprite, 1), startingPos, dXY);
        this.hp = hp;
        this.hpMax = hp;
        this.rotationMover = new RotatingSpriteImageMover(sprite, animateSemaphore, startingPos, dXY);
    }

    public ImageView getParalyzedView() {
        return paralyzedView;
    }

    public void setParalyzedView(ImageView paralyzedView) {
        this.paralyzedView = paralyzedView;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;

        if (target == null)
            targetSemaphore.stop();
        else
            targetSemaphore.go();
    }

    @DedicatedThread
    @Daemonize
    public Target reload() throws InterruptedException {
        Thread.sleep(400);
        targetSemaphore.await();
        return getTarget();
    }

    @Override
    public void setVelocity(float velocity) {
        super.setVelocity(velocity);
    }

    @Override
    public boolean isParalyzed() {
        return paralyzed;
    }

    @Override
    public Enemy setParalyzed(boolean paralyzed) {
        this.paralyzed = paralyzed;
        return this;
    }

    private Pair<Integer, Integer> previousField;

    public Pair<Integer, Integer> getPreviousField() {
        return previousField;
    }

    public void setPreviousField(Pair<Integer, Integer> previousField) {
        this.previousField = previousField;
    }

    private RotatingSpriteImageMover rotationMover;

    @Exclude
    public Enemy setHealthBarImage(Image[] healthBarImage) {
        this.spriteHealthBarImage = healthBarImage;
        return this;
    }

    @Exclude
    public Enemy setParalyzedImage(Image paralyzedImage) {
        this.paralyzedPosImage.image = paralyzedImage;
        return this;
    }

    @Override
    public boolean isAttackable() {
        return shootable;
    }

    @Override
    public Enemy setAttackable(boolean attackable) {
        this.shootable = attackable;
        return this;
    }

    @Override
    public int getHp() {
        return hp;
    }

    @Override
    public int getMaxHp() {
        return hpMax;
    }

    @Override
    public Enemy setHp(int hp) {
        this.hp = hp;
        return this;
    }

    @Override
    public Enemy setMaxHp(int maxHp) {
        this.hpMax = maxHp;
        return this;
    }

    public ImageView getView() {
        return view;
    }

    public Enemy setView(ImageView view) {
        this.view = view;
        return this;
    }

    public ImageView getHpView() {
        return hpView;
    }

    public Enemy setHpView(ImageView hpView) {
        this.hpView = hpView;
        return this;
    }

    public ImageView getTargetView() {
        return targetView;
    }

    public Enemy setTargetView(ImageView targetView) {
        this.targetView = targetView;
        return this;
    }

    @AwaitedClosure
    @GenerateRunnable
    @Daemonize
    @Override
    public void pushSprite(Image [] sprite) throws InterruptedException {
        rotationMover.pushSprite(sprite);
    }

    @Daemonize
    public void rotate(int angle) throws InterruptedException {
        rotationMover.rotate(angle);
    }

    @Daemonize
    public void rotateTowards(float x, float y) throws InterruptedException {
        rotationMover.rotate((int)RotatingSpriteImageMover.getAngle(getLastCoordinates().getFirst(), getLastCoordinates().getSecond(), x, y));
    }

    @Daemonize
    public void rotateTowards(Pair<Float, Float> coords) throws InterruptedException {
        rotateTowards(coords.getFirst(), coords.getSecond());
    }

    @DedicatedThread(engineName = "goTo")
    @Daemonize
    @Override
    public boolean goTo(float x, float y, float velocityInt) throws InterruptedException {
        return super.goTo(x, y, velocityInt);
    }

    @DedicatedThread(engineName = "goTo")
    @Daemonize
    public void go(float x, float y, float velocityInt) throws InterruptedException {
        super.goTo(x, y, velocityInt);
    }

    @DedicatedThread(engineName = "goTo")
    @Daemonize
    public void rotAndGo(float x, float y, float velocityInt) throws InterruptedException {
        rotateTowards(x, y);
        goTo(x, y, velocityInt);
    }

    @DedicatedThread(engineName = "goTo")
    @Daemonize
    public void rotAndGo(Pair<Float, Float> coords, float velocityInt) throws InterruptedException {
        rotateTowards(coords.getFirst(), coords.getSecond());
        goTo(coords.getFirst(), coords.getSecond(), velocityInt);
    }

    @Daemonize
    public void redir(float x, float y) throws InterruptedException {
        rotateTowards(x, y);
        super.redirect(x, y);
    }

    @Override
    public Image iterateSprite() {
        return rotationMover.iterateSprite();
    }

    private GenericNode<Pair<PositionedImage, ImageView>> root = new GenericNode<>(Pair.create(null, null));
    private GenericNode<Pair<PositionedImage, ImageView>> hBarGN = new GenericNode<>(Pair.create(null, null));
    private GenericNode<Pair<PositionedImage, ImageView>> paralyzedGN = new GenericNode<>(Pair.create(null, null));

    {
        root.addChild(hBarGN);
    }

    @SideQuest(SLEEP = 25, blockingClosure = true)
    public GenericNode<Pair<PositionedImage, ImageView>> animateEnemy() throws InterruptedException {

        Pair<Float, Float> lastCoord = getLastCoordinates();
        PositionedImage enemyPosBmp = super.animate();

        if (enemyPosBmp == null)
            return null;

        root.getValue().setFirst(enemyPosBmp).setSecond(view);

        hBar.image = spriteHealthBarImage[(hp * 100 / hpMax - 1) / spriteHealthBarImage.length];
        hBar.positionX = lastCoord.getFirst();
        hBar.positionY = lastCoord.getSecond() - 2 * hBar.image.getHeight();
        hBarGN.getValue().setFirst(hBar).setSecond(hpView);

        if (paralyzed) {
            paralyzedPosImage.positionX = enemyPosBmp.positionX;
            paralyzedPosImage.positionY = enemyPosBmp.positionY;
            paralyzedGN.getValue().setFirst(paralyzedPosImage).setSecond(paralyzedView);
            root.addChild(paralyzedGN);
        } else {
            if(root.getChildren().size() >  1)
                root.getChildren().remove(1);
        }

        return root;
    }
}
