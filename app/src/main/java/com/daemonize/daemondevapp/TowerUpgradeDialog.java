package com.daemonize.daemondevapp;

import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.view.Button;
import com.daemonize.daemondevapp.view.CompositeImageViewImpl;

public class TowerUpgradeDialog {

    private CompositeImageViewImpl towerUpgrade;
    private Tower tower;
    private Image [] dialogueImageTowerUpgradeLevel;
//    private Image upgradeButton;
//    private Image closeButton;
//    private Image greenDialogueImage;

    public TowerUpgradeDialog(int absX, int absY, Image[] dialogueImageTowerUpgradeLevel, Image upgradeButton, Image
            closeButton, Image greenDialogueImage) {
//        this.dialogueImageTowerUpgrade = dialogueImageTowerUpgrade;
//        this.upgradeButton = upgradeButton;
//        this.closeButton = closeButton;
//        this.greenDialogueImage = greenDialogueImage;
        this.dialogueImageTowerUpgradeLevel = dialogueImageTowerUpgradeLevel;
        towerUpgrade = new CompositeImageViewImpl(absX,absY,6, greenDialogueImage);

        Image dialogueImageTowerUpgrade = dialogueImageTowerUpgradeLevel[1];//tower.getLevel()];

        towerUpgrade.addChild(new CompositeImageViewImpl(greenDialogueImage.getWidth() / 2, dialogueImageTowerUpgrade.getHeight() / 2, dialogueImageTowerUpgrade));

        towerUpgrade.addChild(new Button(closeButton.getWidth() / 2, dialogueImageTowerUpgrade.getHeight() + closeButton.getHeight() / 2, closeButton).onClick(() -> {
            //                        dijalogAnimator.stop();
            //                        dijalogActive = false;
            //drawConsumer.consume(() -> towerUpgrade.hide());
            //                        contAll();
        }));

        towerUpgrade.addChild(new Button(
                dialogueImageTowerUpgrade.getWidth() - upgradeButton.getWidth() / 2,
                dialogueImageTowerUpgrade.getHeight() + upgradeButton.getHeight() / 2,
                upgradeButton).onClick(() -> {
//            tower.setScanInterval();
//            if(tower.towerShootInterval > 200)
//                towerShootInterval -= 100;
//
//            tow.setScanInterval(towerShootInterval);
            int level = tower.getLevel();
            tower.setLevel(1);

//            dialogueImageTowerUpgradeLevel[tower.getLevel()]
            //                        dijalogAnimator.stop();
            //                        dijalogActive = false;
            //                        drawConsumer.consume(()->dijalog.hide());
            //                        contAll();
        }));
    }

    public CompositeImageViewImpl getTowerUpgrade() {
        return towerUpgrade;
    }
}
