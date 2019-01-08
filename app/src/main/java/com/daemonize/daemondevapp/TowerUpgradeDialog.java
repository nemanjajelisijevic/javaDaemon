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

    public TowerUpgradeDialog(int absX, int absY, Image dialogueImageTowerUpgrade,
                              Button upgradeButton, Button closeButton, Button saleButton,
                              float width,float height) {

        //this.dialogueImageTowerUpgrade = dialogueImageTowerUpgradeLevel;
        towerUpgrade = new CompositeImageViewImpl("Root", absX,absY,6, width, height);
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
