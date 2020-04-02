package com.daemonize.graphics2d.scene.views;

import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.SceneDrawer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CompositeImageViewImpl implements ImageView<CompositeImageViewImpl> {

    protected ImageViewImpl view;

    protected List<CompositeImageViewImpl> childrenViews;
    private boolean isRoot = false;
    private Map<String, CompositeImageViewImpl> childMap;

    private CompositeImageViewImpl root;
    private CompositeImageViewImpl parent;

    private float relativeX;
    private float relativeY;

    private CompositeImageViewImpl(String name, float absX, float absY, int z) {
        view = new ImageViewImpl(name);
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
    public CompositeImageViewImpl(String name, float absX, float absY, int z, float width, float height) {
        this(name, absX, absY, z);
        view.xOffset = width / 2;
        view.yOffset = height / 2;
        view.startingX = view.absoluteX - view.xOffset;
        view.startingY = view.absoluteY - view.yOffset;
    }

    //for child views
    public CompositeImageViewImpl(String name, Image image) {
        view = new ImageViewImpl(name);
        childrenViews = new LinkedList<>();
        this.setImage(image);
    }

    //for child views
    public CompositeImageViewImpl(String name, float relX, float relY, Image image) {
        view = new ImageViewImpl(name);
        childrenViews = new LinkedList<>();
        this.relativeX = relX;
        this.relativeY = relY;
        this.setImage(image);
    }

    @Override
    public String getName() {
        return view.viewName;
    }

    @Override
    public float getAbsoluteX() {
        return view.absoluteX;
    }

    @Override
    public float getAbsoluteY() {
        return view.absoluteY;
    }

    @Override
    public float getStartingX() {
        return view.startingX;
    }

    @Override
    public float getStartingY() {
        return view.startingY;
    }

    @Override
    public float getEndX() {
        return view.getEndX();
    }

    @Override
    public float getEndY() {
        return view.getEndY();
    }

    @Override
    public float getxOffset() {
        return view.xOffset;
    }

    @Override
    public float getyOffset() {
        return view.yOffset;
    }

    @Override
    public float getWidth() {
        return view.getWidth();
    }

    @Override
    public float getHeight() {
        return view.getHeight();
    }

    @Override
    public CompositeImageViewImpl setImageWithoutOffset(Image image) {
        view.setImageWithoutOffset(image);
        return this;
    }

    @Override
    public CompositeImageViewImpl setImage(Image image) {
        view.setImage(image);
        return this;
    }

    @Override
    public Image getImage() {
        return view.getImage();
    }

    @Override
    public CompositeImageViewImpl setZindex(int zindex) {
        view.zIndex = zindex;
        return this;
    }

    @Override
    public int getZindex() {
        return view.zIndex;
    }

    @Override
    public boolean isShowing() {
        return view.isShowing();
    }

    @Override
    public int compareTo(ImageView o) {
        return Integer.compare(view.zIndex, o.getZindex());
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
        view.absoluteX = absoluteX;
        view.startingX = absoluteX - view.xOffset;
        for(CompositeImageViewImpl child : childrenViews) {
            child.setAbsoluteX(this.getStartingX() + child.getRelativeX());
        }
        return this;
    }

    @Override
    public CompositeImageViewImpl setAbsoluteY(float absoluteY) {
        view.absoluteY = absoluteY;
        view.startingY = absoluteY - view.yOffset;
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
        if (view.checkCoordinates(x,y)) {
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
            throw new IllegalArgumentException("Can not add a child view that is root. Please use non root constructor for this child view(" + child.getName() + ")");
        child.setAbsoluteX((view.getStartingX() + child.getRelativeX()));//TODO check this -- need this because of root child
        child.setAbsoluteY((view.getStartingX() + child.getRelativeY()));//TODO check this
        child.setZindex(this.getZindex() + 1);
        this.addCh(child);
    }

    private void addCh(CompositeImageViewImpl newChild) {
        for (CompositeImageViewImpl child : this.childrenViews){
            if (child.checkRootCoordinates(newChild.getStartingX(), newChild.getStartingY())){
                newChild.setRelativeX(newChild.getRelativeX() - (child.getStartingX() - this.view.getStartingX()));
                newChild.setRelativeY(newChild.getRelativeY() - (child.getStartingY() - this.view.getStartingY()));
                newChild.setAbsoluteX((child.getStartingX() + newChild.getRelativeX()));//TODO check this reson to stay duble check
                newChild.setAbsoluteY((child.getStartingY() + newChild.getRelativeY()));//TODO check this
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

            while(!temp.isRoot)
                temp = temp.parent;

            newChild.root = temp;
        }

        newChild.root.childMap.put(newChild.getName(), newChild);
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
    public CompositeImageViewImpl show() {
        view.show();
        showAllViews(this);
        return this;
    }

    @Override
    public CompositeImageViewImpl hide() {
        view.hide();
        hideAllViews(this);
        return this;
    }

    private CompositeImageViewImpl showAllViews(CompositeImageViewImpl compositeImageView) {
        for (CompositeImageViewImpl child : compositeImageView.getChildrenViews())
            child.show();
        return compositeImageView;
    }

    private CompositeImageViewImpl hideAllViews(CompositeImageViewImpl compositeImageView) {
        for (CompositeImageViewImpl child : compositeImageView.getChildrenViews()) {
            child.hide();
        }
        return compositeImageView;
    }

    @Override
    public void draw(SceneDrawer sceneDrawer) {
        if (isShowing()) {
            sceneDrawer.drawView(this);
            for(CompositeImageViewImpl child : childrenViews)
                child.draw(sceneDrawer);
        }
    }
}

