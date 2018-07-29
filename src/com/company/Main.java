package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {

        File file = new File("C:\\Users\\Elliot\\Desktop\\test.jpg");
        FileInputStream fis = new FileInputStream(file);
        BufferedImage image = ImageIO.read(fis); //reading the image file

        ArrayList<String> repPaths  = getAllImages(new File("C:\\Users\\Elliot\\Desktop\\test1\\"));

        int rows = 150;
        int cols = 150;

        int chunkWidth = image.getWidth() / cols; // determines the chunk width and height
        int chunkHeight = image.getHeight() / rows;
        int count = 0;
        int repCount = repPaths.size();

        chunk[] chunks = new chunk[rows * cols];
        chunk[] replacements = new chunk[repCount];

        BufferedImage combined = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = combined.getGraphics();

        //split image into chunks
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
        System.out.println("splitting done");
        //load replacement images into array
        for (int i = 0; i < repCount; i++) {
            // read file from path
            // % repPaths.size to fill array even if not enough images in rep. folder
            File tempfile = new File(repPaths.get(i));
            FileInputStream tempfis = new FileInputStream(tempfile);

            // scale loaded image to fit chunk
            BufferedImage img = ImageIO.read(tempfis);
            Image tmp = img.getScaledInstance(chunkWidth, chunkHeight, Image.SCALE_SMOOTH);
            BufferedImage dimg = new BufferedImage(chunkWidth, chunkHeight, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = dimg.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();

            // fill array of chunks with read images
            replacements[i] = new chunk(dimg);
            System.out.println((i*100)/(repCount));
        }
        System.out.println("images loaded");
        //for each chunk, calculate the euclidean distance to every possible replacement
        for (int i = 0; i < cols*rows; i++) {

            float[] distances = new float[repCount];
            int minEuclid;

            for (int j = 0; j < repCount; j++) {
                distances[j] = replacements[j].euclideanDistance(chunks[i].getAverage());
                //int[] a = replacements[j].getAverage();
                //int[] b = chunks[i].getAverage();
                //System.out.println( a[0]+ " " + a[1]+ " " +a[2]+ " " + b[0]+ " " + b[1]+ " " +b[2] + " " + replacements[j].euclideanDistance(chunks[i].getAverage()));

            }
            minEuclid = getMinValue(distances);

            g.drawImage(replacements[minEuclid].getImg(), ((i%cols)*chunkWidth), ((i/cols)*chunkHeight), null);

        }
        System.out.println("done");
        ImageIO.write(combined, "PNG", new File("C:\\Users\\Elliot\\Desktop\\","combined.png"));

    }

    /**
     * Returns all jpg images from a directory in an array.
     * Source: http://www.java2s.com/Code/Java/2D-Graphics-GUI/Returnsalljpgimagesfromadirectoryinanarray.htm
     * @param directory                 the directory to start with
     * @return an ArrayList<String> containing all the file paths or nul if none are found..
     * @throws IOException
     */
    private static ArrayList<String> getAllImages(File directory) throws IOException {
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
    /**
     * Returns index of smalest value
     * @param numbers array with float values
     * @return int index of smallest value
     */
    private static int getMinValue(float[] numbers){
        float minValue = numbers[0];
        int index = 0;
        for(int i=1;i<numbers.length;i++){
            if(numbers[i] < minValue){
                minValue = numbers[i];
                index = i;
            }
        }
        //System.out.println(minValue);
        return index;
    }
}
