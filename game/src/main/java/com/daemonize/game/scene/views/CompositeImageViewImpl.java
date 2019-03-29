package com.daemonize.game.scene.views;

import com.daemonize.game.images.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CompositeImageViewImpl extends ImageViewImpl {

    protected List<CompositeImageViewImpl> childrenViews;
    private boolean isRoot = false;
    private Map<String, CompositeImageViewImpl> childMap;

    private CompositeImageViewImpl root;
    private CompositeImageViewImpl parent;

    private float relativeX;
    private float relativeY;

    private CompositeImageViewImpl(String name, float absX, float absY, int z) {
        super(name);
        childrenViews = new LinkedList<>();
        childMap = new HashMap<>();
        childMap.put(name, this);
        this.isRoot = true;
        this.setAbsoluteX(absX);
        this.setAbsoluteY(absY);
        this.setZindex(z);
    }

    //for root only!
    public CompositeImageViewImpl(String name, float absX, float absY, int z, Image image) {
        this(name, absX, absY, z);
        this.setImage(image);
    }

    //for root without image only!
    public CompositeImageViewImpl(String name, float absX, float absY, int z, float width,float height) {
        this(name, absX, absY, z);
        this.xOffset = width / 2;
        this.yOffset = height / 2;
        this.startingX = this.absoluteX - this.xOffset;
        this.startingY = this.absoluteY - this.yOffset;
    }

    //for child views
    public CompositeImageViewImpl(String name, float relX, float relY, Image image) {
        super(name);
        childrenViews = new LinkedList<>();
        this.relativeX = relX;
        this.relativeY = relY;
        this.setImage(image);
    }

    public CompositeImageViewImpl getParent() {
        if (isRoot)
            throw new IllegalStateException("Can not get parent of root view!");
        return parent;
    }

    public CompositeImageViewImpl getRoot() {
        if (isRoot)
            return this;

        return root;
    }

    public float getRelativeX() {
        return relativeX;
    }

    public float getRelativeY() {
        return relativeY;
    }

    public CompositeImageViewImpl setRelativeX(float relativeX) {
        this.relativeX = relativeX;
        return this;
    }

    public CompositeImageViewImpl setRelativeY(float relativeY) {
        this.relativeY = relativeY;
        return this;
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

    public CompositeImageViewImpl getViewByName(String name) {
        if (isRoot)
            return childMap.get(name);
        else
            return root.childMap.get(name);
    }

    @Override
    public boolean checkCoordinates(float x, float y) {
        if (super.checkCoordinates(x,y)) {
            for (CompositeImageViewImpl child : getChildrenViews()) {
                if (child.checkCoordinates(x, y)) {
                    return true;
                }
                //return child.checkCoordinates(x, y);
            }
        }
        return false;
    }

    //@Override
    public void addChild(CompositeImageViewImpl child) {
        if(child.isRoot)
            throw new IllegalStateException("Can not add a child view that is root. Please use non root constructor for this child view(" + child.viewName + ")");
        child.setAbsoluteX((this.startingX + child.getRelativeX() ));//TODO check this -- need this because of root child
        child.setAbsoluteY((this.startingY + child.getRelativeY() ));//TODO check this
        child.setZindex(this.getZindex() + 1);
        this.addCh(child);
    }

    private void addCh(CompositeImageViewImpl newChild) {
        for (CompositeImageViewImpl child : this.childrenViews){
            if (child.checkRootCoordinates(newChild.getStartingX(), newChild.getStartingY())){
                newChild.setRelativeX(newChild.getRelativeX() - (child.startingX - this.startingX));
                newChild.setRelativeY(newChild.getRelativeY() - (child.startingY - this.startingY));
                newChild.setAbsoluteX((child.startingX + newChild.getRelativeX()));//TODO check this reson to stay duble check
                newChild.setAbsoluteY((child.startingY + newChild.getRelativeY()));//TODO check this
                newChild.setZindex(child.getZindex() + 1);
                child.addCh(newChild);
                return;
            }
        }

        newChild.parent = this;

        if(isRoot)
            newChild.root = this;
        else {
            CompositeImageViewImpl temp = this;

            while(!temp.isRoot){
                temp = temp.parent;
            }

            newChild.root = temp;
        }

        newChild.root.childMap.put(newChild.viewName, newChild);
        this.childrenViews.add(newChild);
    }

    private boolean checkRootCoordinates(float x, float y) {
        if (x > getStartingX() && x < getEndX()) {
            if (y > getStartingY() && y < getEndY())
                return true;
        }
        return false;
    }

    @Override
    public List<ImageView> getAllViews () {
        return getAllViews(this);
    }

    private List<ImageView> getAllViews (CompositeImageViewImpl compositeImageViewImpl) {
        List<ImageView> lst = new ArrayList<>();
        for (CompositeImageViewImpl child : compositeImageViewImpl.getChildrenViews()){
            lst.add(child);
            if (child.getChildrenViews()!= null && !child.getChildrenViews().isEmpty()){
                lst.addAll(getAllViews(child));
            }
        }

        if (compositeImageViewImpl.getImage() != null)// TODO fix!!!
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
