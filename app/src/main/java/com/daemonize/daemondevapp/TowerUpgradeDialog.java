package com.daemonize.daemondevapp;

import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.view.Button;
import com.daemonize.daemondevapp.view.CompositeImageViewImpl;

public class TowerUpgradeDialog {

    private CompositeImageViewImpl towerUpgrade;
    private Tower tower;
    //private Image dialogueImageTowerUpgrade;

    public Tower getTower() {
        return tower;
    }

    public TowerUpgradeDialog setTower(Tower tower) {
        this.tower = tower;
        return this;
    }

    public TowerUpgradeDialog(int absX, int absY, Image dialogueImageTowerUpgrade, Button upgradeButton, Button
            closeButton, float width,float height) {

        //this.dialogueImageTowerUpgrade = dialogueImageTowerUpgradeLevel;
        towerUpgrade = new CompositeImageViewImpl("Root", absX,absY,6, width, height);
        towerUpgrade.addChild(new CompositeImageViewImpl("TowerView", width / 2, dialogueImageTowerUpgrade.getHeight() / 2, dialogueImageTowerUpgrade));
        towerUpgrade.addChild(upgradeButton.setRelativeX(dialogueImageTowerUpgrade.getWidth() - upgradeButton.getWidth() / 2).setRelativeY(dialogueImageTowerUpgrade.getHeight() + upgradeButton.getHeight() / 2));
        towerUpgrade.addChild(closeButton.setRelativeX(closeButton.getWidth() / 2).setRelativeY(dialogueImageTowerUpgrade.getHeight() + closeButton.getHeight() / 2));
    }

    public CompositeImageViewImpl getTowerUpgrade() {
        return towerUpgrade;
    }
}
