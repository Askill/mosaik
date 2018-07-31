package com.company;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import java.util.ArrayList;

public class Main extends Application {

    private ImageView   image1;
    private ImageView   image2;
    private Button      picture;
    private Button      replacements;
    private TextField   row;
    private TextField   col;
    private Button      run;
    private Button      save;
    private ProgressBar progress;
    private TextFlow    log;

    private int rows = 150;
    private int cols = 150;

    private int chunkWidth;
    private int chunkHeight;

    private File file;
    private ArrayList<String> repPaths;

    private BufferedImage combined;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("MosaikJF");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        image1 =    (javafx.scene.image.ImageView)primaryStage.getScene().lookup("#image1");
        image2 =    (javafx.scene.image.ImageView)primaryStage.getScene().lookup("#image2");
        picture =   (Button)primaryStage.getScene().lookup("#picture");
        replacements = (Button)primaryStage.getScene().lookup("#replacements");
        row =       (TextField)primaryStage.getScene().lookup("#rows");
        col =       (TextField)primaryStage.getScene().lookup("#cols");
        run =       (Button)primaryStage.getScene().lookup("#run");
        save =      (Button)primaryStage.getScene().lookup("#save");
        progress =  (ProgressBar)primaryStage.getScene().lookup("#progress");
        log =       (TextFlow)primaryStage.getScene().lookup("#log");

        hanleEvents(primaryStage);
    }

    private void hanleEvents(Stage primaryStage) throws Exception{
        picture.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image");
            file = fileChooser.showOpenDialog(primaryStage);
            FileInputStream fis;
            log.getChildren().add(new Text("loading image\n"));
            try {
                fis = new FileInputStream(file);
                try {
                    BufferedImage image = ImageIO.read(fis);
                    image1.setImage(SwingFXUtils.toFXImage(image, null));
                    log.getChildren().add(new Text("loaded image\n"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }

        });

        replacements.setOnAction(e -> {
            DirectoryChooser fileChooser = new DirectoryChooser();
            fileChooser.setTitle("Coose replacement images");
            File tempfile = fileChooser.showDialog(primaryStage);

            try {
                repPaths  = getAllImages(tempfile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        run.setOnAction(e -> {

            if(file == null || repPaths == null){

            }
            else{
                try {
                    cols = Integer.parseInt(col.getText());
                    rows = Integer.parseInt(row.getText());
                    main();
                    image2.setImage(SwingFXUtils.toFXImage(combined, null));

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        save.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            File file = fileChooser.showSaveDialog(primaryStage);

            try {
                ImageIO.write(combined, "PNG", file);

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });
    }

    public void main() throws IOException {

        FileInputStream fis = new FileInputStream(file);
        BufferedImage image = ImageIO.read(fis); //reading the image file

        chunkWidth = image.getWidth() / cols; // determines the chunk width and height
        chunkHeight = image.getHeight() / rows;
        int count = 0;
        int repCount = repPaths.size();

        chunk[] chunks = new chunk[rows * cols];
        chunk[] replacements = new chunk[repCount];

        combined = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
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
        log.getChildren().add(new Text("splitting done \n"));
        //System.out.println("splitting done");
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
            log.getChildren().add(new Text(Integer.toString((i*100)/(repCount)) + "\n"));
            //System.out.println((i*100)/(repCount));
            progress.setProgress((i*100)/(repCount));
        }
        log.getChildren().add(new Text("images loaded \n"));
        //System.out.println("images loaded");
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
        log.getChildren().add(new Text("replacement done \n"));
        //System.out.println("done");


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
