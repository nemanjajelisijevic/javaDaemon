package com.daemonize.daemondevapp;

import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.scene.view.Button;
import com.daemonize.daemondevapp.scene.view.CompositeImageViewImpl;

public class TowerSelectDialogue {

    private CompositeImageViewImpl selectTowerDialogue;

    public TowerSelectDialogue(int absX, int absY,float width,float height,Image selection,
                               Button towerType1, Button towerType2, Button towerType3) {

        selectTowerDialogue = new CompositeImageViewImpl("SelecDialogBackground", absX, absY,6, width, height);
        selectTowerDialogue.addChild(new CompositeImageViewImpl("Tower1",selection.getWidth()/2,selection.getHeight()/2,selection));
        selectTowerDialogue.addChild(new CompositeImageViewImpl("Tower2",selection.getWidth()/2,height / 3 + selection.getHeight()/2,selection));
        selectTowerDialogue.addChild(new CompositeImageViewImpl("Tower3",selection.getWidth()/2,height * 2 / 3 +selection.getHeight()/2,selection));

        selectTowerDialogue.addChild(towerType1.setRelativeX(selection.getWidth()/2).setRelativeY(selection.getHeight()/2));
        selectTowerDialogue.addChild(towerType2.setRelativeX(selection.getWidth()/2).setRelativeY(height / 3 + selection.getHeight()/2));
        selectTowerDialogue.addChild(towerType3.setRelativeX(selection.getWidth()/2).setRelativeY(height * 2 / 3 +selection.getHeight()/2));

    }

    public CompositeImageViewImpl getSelectTowerDialogue() {
        return selectTowerDialogue;
    }
}
