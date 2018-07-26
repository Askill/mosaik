package com.company;

import java.awt.image.BufferedImage;

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
        int r = 0, g = 0, b = 0, x= 0, y = 0, clr;

        for(; x < this.img.getWidth(); x++){
            for(; x < this.img .getHeight(); x++){
                clr   = this.img.getRGB(x, y);
                r += (clr & 0x00ff0000) >> 16;
                g += (clr & 0x0000ff00) >> 8;
                b +=  clr & 0x000000ff;
            }
        }

        r /= x*y;
        g /= x*y;
        b /= x*y;

        return new int[]{r,g,b};
    }
}
