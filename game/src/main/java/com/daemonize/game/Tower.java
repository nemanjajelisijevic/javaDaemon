package com.daemonize.game;


import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.BlockingClosure;
import com.daemonize.daemonprocessor.annotations.ConsumerArg;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.imagemovers.RotatingSpriteImageMover;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.views.ImageView;

import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Daemon(doubleDaemonize = true, implementPrototypeInterfaces = true)
public class Tower extends RotatingSpriteImageMover implements Target<Tower>, Shooter {

    public static class TowerLevel {

        public int currentLevel;
        public int bulletDamage;
        public int reloadInterval;

        public TowerLevel(int currentLevel, int bulletDamage, int reloadInterval) {
            this.currentLevel = currentLevel;
            this.bulletDamage = bulletDamage;
            this.reloadInterval = reloadInterval;
        }

        @Override
        public String toString() {
            return "Tower Level: " + currentLevel + ", bullet damage: " + bulletDamage + ", reload interval: " + reloadInterval + " ms.";
        }
    }

    private TowerLevel towerLevel = new TowerLevel(0,0,Integer.MAX_VALUE);

    protected ImageView view;
    private ImageView hpView;

    private volatile int hpMax;
    private volatile int hp;

    private Image[] spriteHealthBarImage;

    private boolean shootable = true;

    public enum TowerType {
        TYPE1,
        TYPE2,
        TYPE3
    }

    private TowerType towertype;

    protected volatile Target target;
    protected Lock targetLock;
    protected Condition targetCondition;

    protected volatile float range;

    @FunctionalInterface
    public interface TargetTester {
        public boolean test(Target target);
    }

    protected TargetTester targetTester = target -> (target.isShootable()
            && (Math.abs(target.getLastCoordinates().getFirst() - getLastCoordinates().getFirst()) < range
            && Math.abs(target.getLastCoordinates().getSecond() - getLastCoordinates().getSecond()) < range));

    public Tower(Image[] rotationSprite, Image[] healthBarImage, Pair<Float, Float> startingPos, float range, TowerType type, float dXY, int hp) {
        super(rotationSprite, rotationSprite[18], 0, startingPos, dXY);
        this.ret.positionX = startingPos.getFirst();
        this.ret.positionY = startingPos.getSecond();
        this.range = range;
        this.towertype = type;
        this.hp = hp;
        this.hpMax = hp;
        this.targetLock = new ReentrantLock();
        this.targetCondition = targetLock.newCondition();
        this.animateSemaphore.stop();
        this.spriteBuffer.setCurrentAngle(180);
        this.spriteHealthBarImage = healthBarImage;
        hBar.image = spriteHealthBarImage[0];
        hBar.positionX = ret.positionX;
        hBar.positionY = ret.positionY - 2 * hBar.image.getHeight();
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return hpMax;
    }

    public Tower setHp(int hp) {
        if (hp <= hpMax)
            this.hp = hp;
        return this;
    }

    public Tower setMaxHp(int maxHp) {
        this.hpMax = maxHp;
        return this;
    }

    public ImageView getHpView() {
        return hpView;
    }

    public Tower setHpView(ImageView hpView) {
        this.hpView = hpView;
        return this;
    }

    public Tower setHealthBarImage(Image[] healthBarImage) {
        this.spriteHealthBarImage = healthBarImage;
        return this;
    }

    @Override
    public boolean isShootable() {
        return shootable;
    }

    @Override
    public Tower setShootable(boolean shootable) {
        this.shootable = shootable;
        return this;
    }

    @Override
    public Tower setParalyzed(boolean paralyzed) {
        return this;
    }

    @Override
    public boolean isParalyzed() {
        return false;
    }

    public float getRange() {
        return range;
    }

    public void levelUp(){
        switch (++towerLevel.currentLevel) {
            case 2:{//midle level
                towerLevel.bulletDamage += 3;
                towerLevel.reloadInterval -=500;
                break;
            }
            case 3: {//top level
                towerLevel.bulletDamage += 5;
                towerLevel.reloadInterval -= 800;
                break;
            }
            default: {
                towerLevel.currentLevel = 3; // TODO currentLevel should be equal max level
            }
        }
    }

    public TowerLevel getTowerLevel() {
        return towerLevel;
    }

    public Tower setTowerLevel(TowerLevel towerLevel) {
        this.towerLevel = towerLevel;
        return this;
    }

    public ImageView getView() {
        return view;
    }

    public void setView(ImageView view) {
        this.view = view;
    }

    @GenerateRunnable
    @Daemonize
    public void reload(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }

    @Override
    public void setRotationSprite(Image[] rotationSprite) {
        super.setRotationSprite(rotationSprite);
    }

    @BlockingClosure
    @Daemonize
    @GenerateRunnable
    @Override
    public void pushSprite(Image[] sprite, float velocity) throws InterruptedException {
        super.pushSprite(sprite, velocity);
    }

    public void addTarget(Target target) {
        targetLock.lock();
        if (this.target == null) {
            this.target = target;
            targetCondition.signalAll();
        }
        targetLock.unlock();
    }

    private Pair<TowerType, Target> scanRet = Pair.create(null, null);

    @DedicatedThread(name = "scan")
    @Daemonize
    public Pair<TowerType, Target> scan() throws InterruptedException {

        scanRet = Pair.create(null, null);

        targetLock.lock();

        try {

            while (this.target == null)
                targetCondition.await();

            if (targetTester.test(target)) {
                scanRet = Pair.create(towertype, target);
            } else
                target = null;

        } finally {
            targetLock.unlock();
        }

        if (scanRet.getSecond() != null)
            rotateTo(scanRet.getSecond());

        return scanRet;
    }

    protected void rotateTo(Target target) throws InterruptedException {
        if (target.isShootable())
            rotateTowards(
                    target.getLastCoordinates().getFirst(),
                    target.getLastCoordinates().getSecond()
            );
    }

//    @CallingThread
//    @Override
//    public void pause() {
//        super.pause();
//        pauseScan();
//    }
//
//    @CallingThread
//    @Override
//    public void cont() {
//        super.cont();
//        contScan();
//        targetLock.lock();
//        targetCondition.signalAll();
//        targetLock.unlock();
//    }

    @Override
    public void setCurrentAngle(int currentAngle) {
        super.setCurrentAngle(currentAngle);
    }

//    @CallingThread
//    public void pauseScan() {
//        scanSemaphore.stop();
//    }

//    @CallingThread
//    public void contScan() {
//        scanSemaphore.reset();
//    }

    protected volatile PositionedImage ret = new PositionedImage();
    protected PositionedImage hBar = new PositionedImage();

    protected GenericNode<Pair<PositionedImage, ImageView>> genericRet = new GenericNode<>(Pair.create(null, null));
    protected GenericNode<Pair<PositionedImage, ImageView>> genericRetHBar = new GenericNode<>(Pair.create(null, null));

    {
        genericRet.addChild(genericRetHBar);
    }

    @Daemonize(consumerArg = true)
    public GenericNode<Pair<PositionedImage, ImageView>> updateSprite() {//hack but improves performance
        ret.image = iterateSprite();
        genericRet.getValue().setFirst(ret).setSecond(view);
        updateHpSprite();
        return genericRet;
    }

    protected void updateHpSprite() {
        hBar.image = spriteHealthBarImage[(hp * 100 / hpMax - 1) / spriteHealthBarImage.length];
        hBar.positionX = ret.positionX;
        hBar.positionY = ret.positionY - 2 * hBar.image.getHeight();
        genericRetHBar.getValue().setFirst(hBar).setSecond(hpView);
    }


    private int initHpCount = 0;

    @SideQuest(SLEEP = 25, interruptible = true)
    public GenericNode<Pair<PositionedImage, ImageView>> initTower() throws InterruptedException {
        if (spriteBuffer.getCurrentAngle() != 0) {

            ret.image = spriteBuffer.getDecrementedByStep();

            genericRet.getValue().setFirst(ret).setSecond(view);

            if (initHpCount < spriteHealthBarImage.length) {
                hBar.image = spriteHealthBarImage[initHpCount++];
            }

            genericRetHBar.getValue().setFirst(hBar).setSecond(hpView);
            return genericRet;
        }

        towerLevel = new TowerLevel(1,2,1500);
        throw new InterruptedException();
    }

    @SideQuest(SLEEP = 25)
    public GenericNode<Pair<PositionedImage, ImageView>> animateTower() throws InterruptedException {
        try {
            animateSemaphore.await();
            return updateSprite();
        } catch (InterruptedException ex) {
            return null;
        }
    }

    public TowerType getTowertype() {
        return towertype;
    }

    @Override
    public String toString() {
        targetLock.lock();
        String ret =  towerLevel.toString()
                    + "\nTowerType: " + towertype
                    + "\nCurrent hp: " + hp + ", Max hp: " + hpMax
                    + "\nShootable: " + shootable
                    + "\nRange: " + range
                    + "\nTarget available: " + Boolean.toString(this.target != null)
                    + "\nTargetLock: " + targetLock.toString()
                    + "\nTargetCondition: " + targetCondition.toString()
                    + "\nTarget: shootable: " + ((target != null) ? Boolean.toString(target.isShootable()) : "NULL")
                    + ", Coord X: " + ((target != null) ? Float.toString(target.getLastCoordinates().getFirst()) : "NULL")
                    + ", Coord Y: " + ((target != null) ? Float.toString(target.getLastCoordinates().getSecond()) : "NULL")
                    + "\nAnimateSemaphore: " + animateSemaphore.toString();
        targetLock.unlock();
        return ret;
    }
}
