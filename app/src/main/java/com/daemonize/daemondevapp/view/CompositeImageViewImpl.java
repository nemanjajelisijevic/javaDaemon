package com.daemonize.daemondevapp.view;

import android.util.Log;

import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;

import java.security.interfaces.RSAMultiPrimePrivateCrtKey;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CompositeImageViewImpl extends ImageViewImpl {

    protected List<CompositeImageViewImpl> childrenViews;

    public CompositeImageViewImpl() {}

    public CompositeImageViewImpl(int x, int y, int z, Image image) {
        //CompositeImageViewImpl rootView = new CompositeImageViewImpl().setAbsoluteX(x).setAbsoluteY(y).setZindex(z).setImage(image); //.show();
        super();
        childrenViews = new LinkedList<>();
        this.setAbsoluteX(x);
        this.setAbsoluteY(y);
        this.setZindex(z);
        this.setImage(image);


    }

    public List<CompositeImageViewImpl> getChildrenViews() {
        return childrenViews;
    }

    @Override
    public boolean checkCoordinates(float x, float y) {
        for (CompositeImageViewImpl child : getChildrenViews()){
            return child.checkCoordinates(x, y);
        }
        return false;
    }

    @Override
    public void addChild(CompositeImageViewImpl child) {
        child.setAbsoluteX((int) (this.getAbsoluteX() - this.getxOffset() + child.getAbsoluteX() + child.getxOffset()));
        child.setAbsoluteY((int) (this.getAbsoluteY() - this.getyOffset() + child.getAbsoluteY() + child.getyOffset()));
        child.setZindex(this.getZindex() + 1);
        addCh(this,child);
    }

    @Override
    public void addChild(Image image, Pair<Integer, Integer> coordinates) {
        if (childrenViews == null) {
            childrenViews = new LinkedList<>();
        }

        CompositeImageViewImpl child = new CompositeImageViewImpl(
                                    (int) (this.getAbsoluteX() - this.getxOffset() + coordinates.getFirst() + image.getWidth()/2),
                                    (int) (this.getAbsoluteY() - this.getyOffset() + coordinates.getSecond() + image.getHeight() / 2),
                                    this.getZindex() + 1,
                                    image);

        addCh(this,child);

    }

    private boolean isViewBInsideViewA (ImageView viewA, ImageView viewB){
        int x1 = (int) (viewA.getAbsoluteX() - viewA.getxOffset());
        int x2 = (int) (viewA.getAbsoluteX() + viewA.getxOffset());
        int y1 = (int) (viewA.getAbsoluteY() - viewA.getyOffset());
        int y2 = (int) (viewA.getAbsoluteY() + viewA.getyOffset());

        int xB1 = (int) (viewB.getAbsoluteX() - viewB.getxOffset());
        int yB1 = (int) (viewB.getAbsoluteY() - viewB.getyOffset());

        if (xB1 >= x1 && xB1 <= x2 && yB1 >= y1 && yB1 <= y2){
            return true;
        }
        return false;
    }

    private void addCh(CompositeImageViewImpl compositeImageView, CompositeImageViewImpl newChild) {
        for (CompositeImageViewImpl child : compositeImageView.getChildrenViews()){
            if (/*isViewBInsideViewA(child,newChild)*/child.checkCoordinates(newChild.getAbsoluteX() - newChild.getxOffset(), newChild.getAbsoluteY() - newChild.getyOffset())){
                //ponovi sve za dete
                newChild.setZindex(child.getZindex() + 1); // povecamo z index mozda treba i kordinate prevezati
                addCh(child,newChild);
                return;
            }
        }
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
            if (child.getChildrenViews()!= null && child.getChildrenViews().size()!=0){
                lst.add((ImageView) getAllViews(child));
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
        Log.w("hide","desio se hajd compositimagea");
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
