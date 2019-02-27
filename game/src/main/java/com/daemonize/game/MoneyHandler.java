package com.daemonize.game;

import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.daemonize.game.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.game.imagemovers.spriteiterators.BasicSpriteIterator;
import com.daemonize.game.imagemovers.spriteiterators.SpriteIterator;
import com.daemonize.game.images.Image;


@Daemonize(doubleDaemonize = true)
public class MoneyHandler extends CoordinatedImageTranslationMover  {

    private volatile int amount;
    private Image moneySign;

    public MoneyHandler(Image[] sprite, Image moneySign, float dXY) {
        super(sprite, 0, Pair.create(0F, 0F), dXY);
        this.moneySign = moneySign;
    }

    public void setAmount(int amount) {
        if (amount < 0  || amount > 9)
            throw new IllegalArgumentException("Amount must be > 0 && < 10!");
        this.amount = amount;
    }

    @Override
    public void setCoordinates(float lastX, float lastY) {
        super.setCoordinates(lastX, lastY);
    }

    @Override
    public boolean goTo(float x, float y, float velocityInt) throws InterruptedException {
        return super.goTo(x, y, velocityInt);
    }

    @Override
    public Image iterateSprite() {
        return getSprite()[amount];
    }

    @SideQuest(SLEEP = 15)
    public Pair<PositionedImage, PositionedImage> animateMoney() throws InterruptedException {

        PositionedImage number = super.animate();

        if(number == null)
            return null;

        PositionedImage currency = new PositionedImage();

        currency.image = moneySign;
        currency.positionX = number.positionX + number.image.getWidth() / 2 + moneySign.getWidth() / 2;
        currency.positionY = number.positionY;

        return Pair.create(number, currency);
    }

    @Override
    public void setOutOfBordersConsumer(Consumer consumer) {
        super.setOutOfBordersConsumer(consumer);
    }

    @Override
    public void setOutOfBordersClosure(Runnable outOfBordersClosure) {
        super.setOutOfBordersClosure(outOfBordersClosure);
    }
}
