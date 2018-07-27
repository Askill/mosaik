package com.company;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {

        File file = new File("C:\\Users\\Elliot\\Desktop\\bear.jpg");
        FileInputStream fis = new FileInputStream(file);
        BufferedImage image = ImageIO.read(fis); //reading the image file

        ArrayList<String> repPaths  = getAllImages(new File("C:\\Users\\Elliot\\Desktop\\test\\"));

        int rows = 3;
        int cols = 3;

        int chunkWidth = image.getWidth() / cols; // determines the chunk width and height
        int chunkHeight = image.getHeight() / rows;
        int count = 0;

        chunk[] chunks = new chunk[rows * cols];
        chunk[] replacements = new chunk[rows * cols];

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //Initialize the image array with image chunks
                BufferedImage tempimg = new BufferedImage(chunkWidth, chunkHeight, image.getType());
                chunks[count] = new chunk(tempimg);

                // draws the image chunk
                Graphics2D gr = chunks[count++].getImg().createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.dispose();

                chunks[count-1].average();
            }
        }

        for (int i = 0; i < cols; i++) {
            // read file from path
            File tempfile = new File(repPaths.get(i));
            FileInputStream tempfis = new FileInputStream(tempfile);

            // fill array of chunks with read images
            BufferedImage tempimg = ImageIO.read(tempfis);
            replacements[i] = new chunk(tempimg);
        }

        ImageIO.write(chunks[8].getImg(), "jpeg", new File("collage.jpeg"));

    }

    /**
     * Returns all jpg images from a directory in an array.
     * Source: http://www.java2s.com/Code/Java/2D-Graphics-GUI/Returnsalljpgimagesfromadirectoryinanarray.htm
     * @param directory                 the directory to start with
     * @return an ArrayList<String> containing all the file paths or nul if none are found..
     * @throws IOException
     */
    public static ArrayList<String> getAllImages(File directory) throws IOException {
        ArrayList<String> resultList = new ArrayList<String>(256);
        File[] f = directory.listFiles();
        for (File file : f) {
            if (file != null && file.getName().toLowerCase().endsWith(".jpg")) {
                resultList.add(file.getCanonicalPath());
            }
        }
        if (resultList.size() > 0)
            return resultList;
        else
            return null;
    }
}
