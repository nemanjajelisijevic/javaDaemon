package com.daemonize.game;


import com.daemonize.game.images.Image;
import com.daemonize.game.view.ImageView;

public class InfoTable {
    private int x,y;

   // private ImageView backGroundImage;
    //private ImageView title;
    private ImageView [] numbersView;
    private Image [] numberImages;

    public InfoTable(int x, int y, ImageView backGroundImage, ImageView title, ImageView[] numbers, Image[] images) {
        this.x = x;
        this.y = y;
        //this.backGroundImage = backGroundImage;
       // this.title = title;
        this.numbersView = numbers;
        this.numberImages = images;

        backGroundImage.setAbsoluteY(y).setAbsoluteX(x).show();
        title.setAbsoluteX(x).setAbsoluteY(y/2).show();

        for (int i=0;i<numbersView.length;i++){
            numbersView[i].setAbsoluteY(y).setAbsoluteX(x+((-2+i)*images[0].getWidth())).setImage(images[i]).show();
        }
    }

    public InfoTable setNumbers(int number){
        numbersView[4].setImage(numberImages[number%10]);
        numbersView[3].setImage(numberImages[((int)(number/10))%10]);
        numbersView[2].setImage(numberImages[((int)(number/100))%10]);
        numbersView[1].setImage(numberImages[((int)(number/1000))%10]);
        numbersView[0].setImage(numberImages[((int)(number/10000))%10]);
        return this;
    }
}
