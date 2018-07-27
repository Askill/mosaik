package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class chunk {


    private BufferedImage img;
    private int[] average;

    chunk(BufferedImage Img){
        this.img = Img;
        this.average = this.average();
    }

/*
* GETTER AND SETTER
*/
    public BufferedImage getImg() {
        return img;
    }
    public void setImg(BufferedImage img) {
        this.img = img;
    }

    public int[] getAverage() {
        return average;
    }
    public void setAverage(int[] average) {
        this.average = average;
    }

    /*
* OTHER METHODS
*/
    public int[] average(){
        int r = 0, g = 0, b = 0, a = 0, x= 0, y = 0, pc = 0;

        for(; y < this.img.getHeight(); y++){
            for(; x < this.img.getWidth(); x++){

                //get pixel value
                int p = this.img.getRGB(x,y);

                a += (p>>24) & 0xff;
                r += (p>>16) & 0xff;
                g += (p>>8) & 0xff;
                b += p & 0xff;
                pc++;
            }
        }

        r /= pc;
        g /= pc;
        b /= pc;
        a /= pc;

        //System.out.println(r + " , " + g + " , " + b  + " , " + a);
        return new int[]{r,g,b,a};
    }

}
