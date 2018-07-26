package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
public class chunk {


    private BufferedImage img;

    chunk(BufferedImage Img){
    this.img = Img;
    }

    public BufferedImage getImg() {
        return img;
    }
    public void setImg(BufferedImage img) {
        this.img = img;
    }
    public int[] average(){
        int r = 0, g = 0, b = 0, a = 0, x= 0, y = 0;

        for(; y < this.img.getHeight(); y++){
            for(; x < this.img.getWidth(); x++){

                //get pixel value
                int p = this.img.getRGB(x,y);

                //get alpha
                 a = (p>>24) & 0xff;

                //get red
                 r = (p>>16) & 0xff;

                //get green
                 g = (p>>8) & 0xff;

                //get blue
                 a = p & 0xff;

            }
        }

        r /= x*y;
        g /= x*y;
        b /= x*y;
        a /= x*y;

        System.out.println(r + " , " + g + " , " + b  + " , " + a);
        return new int[]{r,g,b};
    }
}
