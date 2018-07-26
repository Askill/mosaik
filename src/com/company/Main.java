package com.company;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) throws IOException {

        File file = new File("C:\\Users\\Elliot\\Desktop\\bear.jpeg");
        FileInputStream fis = new FileInputStream(file);
        BufferedImage image = ImageIO.read(fis); //reading the image file

        int rows = 20;
        int cols = 30;
        int chunks = rows * cols;

        int chunkWidth = image.getWidth() / cols; // determines the chunk width and height
        int chunkHeight = image.getHeight() / rows;
        int count = 0;
        chunk[] imgs = new chunk[chunks];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //Initialize the image array with image chunks
                BufferedImage tempimg = new BufferedImage(chunkWidth, chunkHeight, image.getType());
                imgs[count] = new chunk(tempimg);
                imgs[count].average();


                // draws the image chunk
                Graphics2D gr = imgs[count++].getImg().createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.dispose();
            }
        }
        ImageIO.write(imgs[2].getImg(), "jpg", new File("collage.jpg"));

    }
}
