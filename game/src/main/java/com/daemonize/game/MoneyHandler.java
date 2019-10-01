package com.daemonize.game;

import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.daemonize.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.graphics2d.images.Image;


@Daemon(doubleDaemonize = true, daemonizeBaseMethods = true)
public class MoneyHandler extends CoordinatedImageTranslationMover  {

    private volatile int amount;
    private PositionedImage currency = new PositionedImage();

    public MoneyHandler(Image[] sprite, Image moneySign, float dXY) {
        super(sprite, 0, Pair.create(0F, 0F), dXY);
        this.currency.image = moneySign;
    }

    public void setAmount(int amount) {
        if (amount < 0  || amount > 9) //TODO fix boundries
            throw new IllegalArgumentException("Amount must be > 0 && < 10!");
        this.amount = amount;
    }

    @Override
    public void setCoordinates(float lastX, float lastY) {
        super.setCoordinates(lastX, lastY);
    }

    @Override
    public Image iterateSprite() {
        return getSprite()[amount];
    }

    private Pair<PositionedImage, PositionedImage> ret = Pair.create(null, null);

    @SideQuest(SLEEP = 15)
    public Pair<PositionedImage, PositionedImage> animateMoney() throws InterruptedException {

        PositionedImage number = super.animate();

        if(number == null)
            return null;

        currency.positionX = number.positionX + number.image.getWidth() / 2 + currency.image.getWidth() / 2;
        currency.positionY = number.positionY;

        ret.setFirst(number).setSecond(currency);
        return ret;
    }
}
