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

    /**
     * Calculates and returns average color of buffered image
     * @return Int[3] = {r,g,b,a}, if image contains a, otherwise a is always 255
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
    //TODO: include alpha in calc.
    /**
     * Calculates and returns quadratic euclidean distance between given rgb vector and rgb vector of this
     * @param a array of rgb values
     * @return a Float with the quadratic distance between given rgb vector and rgb vector of this
     */
    public float euclideanDistance(int[] a){
        return ((this.average[0] - a[0])*(this.average[0] - a[0]) + (this.average[1] - a[1])*(this.average[1] - a[1]) + (this.average[2] - a[2])*(this.average[2] - a[2]));
    }
}
