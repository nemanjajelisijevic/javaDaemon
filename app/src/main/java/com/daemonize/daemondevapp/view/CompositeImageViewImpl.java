package com.daemonize.daemondevapp.view;

import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CompositeImageViewImpl extends ImageViewImpl {

    protected List<CompositeImageViewImpl> childrenViews;

    private float relativeX;
    private float relativeY;

    public float getRelativeX() {
        return relativeX;
    }

    public float getRelativeY() {
        return relativeY;
    }

    public CompositeImageViewImpl(float relX, float relY, Image image) {
        super();
        childrenViews = new LinkedList<>();
        this.relativeX = relX;
        this.relativeY = relY;
        this.setImage(image);
    }

    //for root only!
    public CompositeImageViewImpl(float absX, float absY, int z, Image image) {
        super();
        childrenViews = new LinkedList<>();
        this.setAbsoluteX(absX);
        this.setAbsoluteY(absY);
        this.setZindex(z);
        this.setImage(image);
    }

    @Override
    public CompositeImageViewImpl setAbsoluteX(float absoluteX) {
        this.absoluteX = absoluteX;
        this.startingX = absoluteX - this.xOffset;
        for(CompositeImageViewImpl child : childrenViews) {
            child.setAbsoluteX(this.getStartingX() + child.getRelativeX());
        }
        return this;
    }

    @Override
    public ImageViewImpl setAbsoluteY(float absoluteY) {
        this.absoluteY = absoluteY;
        this.startingY = absoluteY - this.yOffset;
        for(CompositeImageViewImpl child : childrenViews) {
            child.setAbsoluteY(this.getStartingY() + child.getRelativeY());
        }
        return this;
    }

    public List<CompositeImageViewImpl> getChildrenViews() {
        return childrenViews;
    }

    @Override
    public boolean checkCoordinates(float x, float y) {
//        if (super.checkCoordinates(x,y)) {
//            for (CompositeImageViewImpl child : getChildrenViews()) {
//                return child.checkCoordinates(x, y);
//            }
//        }

        for (CompositeImageViewImpl child : getChildrenViews()){
            if (child.checkCoordinates(x, y)) {
                return true;
            }
        }
        return false;
    }

    //@Override
    public void addChild(CompositeImageViewImpl child) {
        addCh(this, child);
    }//TODO check for multi nested call

    //@Override
    public void addChild(Image image, Pair<Integer, Integer> coordinates) {
        if (childrenViews == null) {
            childrenViews = new LinkedList<>();
        }

        CompositeImageViewImpl child = new CompositeImageViewImpl(
                                    (int) (getStartingX() + coordinates.getFirst() + image.getWidth()/2),
                                    (int) (getStartingY() + coordinates.getSecond() + image.getHeight() / 2),
                                    this.getZindex() + 1,
                                    image);

        addCh(this,child);

    }

//    private boolean isViewBInsideViewA (ImageView viewA, ImageView viewB){
//        int x1 = (int) (viewA.getAbsoluteX() - viewA.getxOffset());
//        int x2 = (int) (viewA.getAbsoluteX() + viewA.getxOffset());
//        int y1 = (int) (viewA.getAbsoluteY() - viewA.getyOffset());
//        int y2 = (int) (viewA.getAbsoluteY() + viewA.getyOffset());
//
//        int xB1 = (int) (viewB.getAbsoluteX() - viewB.getxOffset());
//        int yB1 = (int) (viewB.getAbsoluteY() - viewB.getyOffset());
//
//        if (xB1 >= x1 && xB1 <= x2 && yB1 >= y1 && yB1 <= y2){
//            return true;
//        }
//        return false;
//    }

    private void addCh(CompositeImageViewImpl compositeImageView, CompositeImageViewImpl newChild) {
        for (CompositeImageViewImpl child : compositeImageView.getChildrenViews()){
            if (child.checkCoordinates(newChild.getStartingX(), newChild.getStartingY())){
                //ponovi sve za dete
                newChild.setZindex(child.getZindex() + 1); // povecamo z index mozda treba i kordinate prevezati
                addCh(child,newChild);
                return;
            }
        }

        newChild.setAbsoluteX((this.startingX + newChild.getRelativeX() /*+ child.getxOffset()*/));//TODO check this
        newChild.setAbsoluteY((this.startingY + newChild.getRelativeY() /*+ child.getyOffset()*/));//TODO check this
        newChild.setZindex(this.getZindex() + 1);

        compositeImageView.getChildrenViews().add(newChild);

    }

    @Override
    public List<ImageView> getAllViews () {
        return  getAllViews(this);
    }

    private List<ImageView> getAllViews (CompositeImageViewImpl compositeImageViewImpl) {
        List<ImageView> lst = new ArrayList<>();
        for (CompositeImageViewImpl child : compositeImageViewImpl.getChildrenViews()){
            lst.add(child);
            if (child.getChildrenViews()!= null && !child.getChildrenViews().isEmpty()){
                lst.addAll(getAllViews(child));
            }
        }
        lst.add(compositeImageViewImpl);
        return lst;
    }

    @Override
    public ImageViewImpl show() {
        super.show();
        showAllViews(this);
        return this;
    }

    @Override
    public ImageViewImpl hide() {
        super.hide();
        hideAllViews(this);
        return this;
    }

    private CompositeImageViewImpl showAllViews(CompositeImageViewImpl compositeImageView) {
        for (CompositeImageViewImpl child : compositeImageView.getChildrenViews()) {
            child.show();
        }
        return compositeImageView;
    }

    private CompositeImageViewImpl hideAllViews(CompositeImageViewImpl compositeImageView) {
        for (CompositeImageViewImpl child : compositeImageView.getChildrenViews()) {
            child.hide();
        }
        return compositeImageView;
    }
}
