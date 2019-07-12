package com.daemonize.game;


import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.ConsumerArg;
import com.daemonize.game.imagemovers.RotatingSpriteImageMover;
import com.daemonize.game.images.Image;
import com.daemonize.game.scene.views.ImageView;

import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.LinkedList;

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Daemonize(doubleDaemonize = true)
public class Tower extends RotatingSpriteImageMover implements Target<Tower> {

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

    protected volatile Queue<Target> targetQueue;
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
        this.targetQueue = new LinkedList<>();
        this.targetLock = new ReentrantLock();
        this.targetCondition = targetLock.newCondition();
        this.animateSemaphore.stop();
        this.spriteBuffer.setCurrentAngle(180);
        this.spriteHealthBarImage = healthBarImage;
        hBar.image = spriteHealthBarImage[0];
        hBar.positionX = ret.positionX;
        hBar.positionY = ret.positionY - 2 * hBar.image.getHeight();
    }

    @CallingThread
    public int getHp() {
        return hp;
    }

    @CallingThread
    public int getMaxHp() {
        return hpMax;
    }

    @CallingThread
    public Tower setHp(int hp) {
        this.hp = hp;
        return this;
    }

    @CallingThread
    public Tower setMaxHp(int maxHp) {
        this.hpMax = maxHp;
        return this;
    }

    @CallingThread
    public ImageView getHpView() {
        return hpView;
    }

    @CallingThread
    public Tower setHpView(ImageView hpView) {
        this.hpView = hpView;
        return this;
    }

    @CallingThread
    public Tower setHealthBarImage(Image[] healthBarImage) {
        this.spriteHealthBarImage = healthBarImage;
        return this;
    }

    @Override
    public boolean isShootable() {
        return shootable;
    }

    @CallingThread
    @Override
    public Tower setShootable(boolean shootable) {
        this.shootable = shootable;
        return this;
    }

    @CallingThread
    @Override
    public Tower setParalyzed(boolean paralyzed) {
        return this;
    }

    @Override
    public boolean isParalyzed() {
        return false;
    }

    @CallingThread
    public boolean addTarget(Target target) {
        boolean ret = false;
        targetLock.lock();
        if (!targetQueue.contains(target)) {
            if (targetQueue.isEmpty()) {
                ret = targetQueue.add(target);
                targetCondition.signalAll();
            } else {
                ret = targetQueue.add(target);
            }
        }
        targetLock.unlock();
        return ret;
    }

    @CallingThread
    public float getRange() {
        return range;
    }

    @CallingThread
    public void levelUp(){
        switch (++towerLevel.currentLevel) {
            case 2:{//midle level
                towerLevel.bulletDamage += 3;
                towerLevel.reloadInterval -=500;
                break;
            }
            case 3: { //top level
                towerLevel.bulletDamage += 5;
                towerLevel.reloadInterval -= 800;
                break;
            }
            default: {
                towerLevel.currentLevel = 3; // TODO currentLevel should be equal max level
            }
        }
    }

    @CallingThread
    public TowerLevel getTowerLevel() {
        return towerLevel;
    }

    @CallingThread
    public Tower setTowerLevel(TowerLevel towerLevel) {
        this.towerLevel = towerLevel;
        return this;
    }

    private DaemonSemaphore scanSemaphore = new DaemonSemaphore().setName("Tower Scan semaphore");

    @CallingThread
    public ImageView getView() {
        return view;
    }

    @CallingThread
    public void setView(ImageView view) {
        this.view = view;
    }

    @GenerateRunnable
    public void reload(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }

    @CallingThread
    @Override
    public void setRotationSprite(Image[] rotationSprite) {
        super.setRotationSprite(rotationSprite);
    }

    @GenerateRunnable
    @Override
    public void pushSprite(Image[] sprite, float velocity) throws InterruptedException {
        super.pushSprite(sprite, velocity);
    }

    private Pair<TowerType, Target> scanRet = Pair.create(null, null);

    @DedicatedThread(name = "scan")
    public Pair<TowerType, Target> scan() throws InterruptedException {

        //pause scan semaphore
        scanSemaphore.await();

        Target target;
        scanRet = Pair.create(null, null);

        targetLock.lock();

        try {
            while (targetQueue.isEmpty())
                targetCondition.await();

            target = targetQueue.peek();
            if (targetTester.test(target))
                scanRet = Pair.create(towertype, target);
            else
                targetQueue.poll();

        } finally {
            targetLock.unlock();
        }

        rotateTo(target);
        return scanRet;
    }

    protected void rotateTo(Target target) throws InterruptedException {
        if (target.isShootable()) {
            animateSemaphore.subscribe();
            try {
                rotateTowards(
                        target.getLastCoordinates().getFirst(),
                        target.getLastCoordinates().getSecond()
                );
            } finally {
                animateSemaphore.unsubscribe();
            }
        }
    }

    @CallingThread
    @Override
    public void pause() {
        super.pause();
        pauseScan();
    }

    @CallingThread
    @Override
    public void cont() {
        super.cont();
        contScan();
        targetLock.lock();
        targetCondition.signalAll();
        targetLock.unlock();
    }

    @CallingThread
    @Override
    public void setCurrentAngle(int currentAngle) {
        super.setCurrentAngle(currentAngle);
    }

    @CallingThread
    public void pauseScan() {
        scanSemaphore.stop();
    }

    @CallingThread
    public void contScan() {
        scanSemaphore.go();
    }

    protected volatile PositionedImage ret = new PositionedImage();
    private PositionedImage hBar = new PositionedImage();

    @ConsumerArg
    public GenericNode<Pair<PositionedImage, ImageView>> updateSprite() {//hack but improves performance
        ret.image = iterateSprite();
        return updateHpSprite(new GenericNode<>(Pair.create(ret, view)));
    }

    protected GenericNode<Pair<PositionedImage, ImageView>> updateHpSprite(GenericNode<Pair<PositionedImage, ImageView>> root) {
        hBar.image = spriteHealthBarImage[(hp * 100 / hpMax - 1) / spriteHealthBarImage.length];
        hBar.positionX = ret.positionX;
        hBar.positionY = ret.positionY - 2 * hBar.image.getHeight();
        root.addChild(new GenericNode<>(Pair.create(hBar, hpView)));
        return root;
    }

    private int initHpCount = 0;

    @SideQuest(SLEEP = 25, interruptible = true)
    public GenericNode<Pair<PositionedImage, ImageView>> initTower() throws InterruptedException {
        if (spriteBuffer.getCurrentAngle() != 0) {

            ret.image = spriteBuffer.getDecrementedByStep();
            GenericNode<Pair<PositionedImage, ImageView>> root = new GenericNode<>(Pair.create(ret, view));

            if (initHpCount < spriteHealthBarImage.length) {
                hBar.image = spriteHealthBarImage[initHpCount++];
            }

            root.addChild(new GenericNode<>(Pair.create(hBar, hpView)));

            return root;
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

    @CallingThread
    public TowerType getTowertype() {
        return towertype;
    }

    @CallingThread
    @Override
    public String toString() {
        return towerLevel.toString()
                + "\nTowerType: " + towertype
                + "\nCurrent hp: " + hp + ", Max hp: " + hpMax
                + "\nShootable: " + shootable
                + "\nRange: " + range
                + "\nTargetQueue size: " + targetQueue.size()
                + "\nTargetLock: " + targetLock.toString()
                + "\nTargetCondition: " + targetCondition.toString()
                + "\nScanSemaphore: " + scanSemaphore.toString()
                + "\nAnimateSemaphore: " + animateSemaphore.toString();
    }
}
