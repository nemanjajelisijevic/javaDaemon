package com.daemonize.game;

import com.daemonize.game.images.Image;
import com.daemonize.game.view.Button;
import com.daemonize.game.view.CompositeImageViewImpl;

public class TowerUpgradeDialog {

    private CompositeImageViewImpl towerUpgrade;
    private TowerDaemon tower;
    //private Image dialogueImageTowerUpgrade;

    public TowerDaemon getTower() {
        return tower;
    }

    public TowerUpgradeDialog setTower(TowerDaemon tower) {
        this.tower = tower;
        return this;
    }

    public TowerUpgradeDialog(int absX, int absY, Image dialogueImageTowerUpgrade,
                              Button upgradeButton, Button closeButton, Button saleButton,
                              float width,float height) {

        //this.dialogueImageTowerUpgrade = dialogueImageTowerUpgradeLevel;
        towerUpgrade = new CompositeImageViewImpl("Root", absX,absY,10, width, height);
        towerUpgrade.addChild(new CompositeImageViewImpl("TowerView", width / 2, height/ 2, dialogueImageTowerUpgrade));
        float h = height / 2 + dialogueImageTowerUpgrade.getHeight()/2;
        towerUpgrade.addChild(upgradeButton.setRelativeX(width - upgradeButton.getWidth() / 2)
                                            .setRelativeY(h + upgradeButton.getHeight() / 2));
        towerUpgrade.addChild(saleButton.setRelativeX(saleButton.getWidth() / 2).setRelativeY(h + saleButton.getHeight() / 2));
        towerUpgrade.addChild(closeButton.setRelativeX(width - closeButton.getWidth() / 2).setRelativeY(closeButton.getHeight() / 2));
    }

    public CompositeImageViewImpl getTowerUpgrade() {
        return towerUpgrade;
    }
}
