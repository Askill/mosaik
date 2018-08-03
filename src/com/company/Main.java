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
import javafx.scene.control.CheckBox;

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
    private javafx.scene.control.CheckBox scaleing;

    private int rows = 150;
    private int cols = 150;
    private boolean scale = false;

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
        scaleing =  (javafx.scene.control.CheckBox)primaryStage.getScene().lookup("#scale");

        handleEvents(primaryStage);
    }

    private void handleEvents(Stage primaryStage) throws Exception{
        picture.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG", "*.jpeg"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GIF", "*.gif"));

            file = fileChooser.showOpenDialog(primaryStage);
            FileInputStream fis;

            log.getChildren().add(0, new Text("loading image\n"));
            try {
                fis = new FileInputStream(file);
                try {
                    BufferedImage image = ImageIO.read(fis);
                    image1.setImage(SwingFXUtils.toFXImage(image, null));
                    log.getChildren().add(new Text("loaded image\n"));
                } catch (Exception e1) {
                    e1.printStackTrace();
                    log.getChildren().add(0, new Text("Error while loading Image\n"));
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                log.getChildren().add(0, new Text("Error while loading Image\n"));
            }

        });

        replacements.setOnAction(e -> {
            DirectoryChooser fileChooser = new DirectoryChooser();
            fileChooser.setTitle("Choose replacement images");
            File tempfile = fileChooser.showDialog(primaryStage);

            try {
                repPaths  = getAllImages(tempfile);
            } catch (Exception e1) {
                e1.printStackTrace();
                log.getChildren().add(0, new Text("Error while loading  replacement images\n"));
            }
        });

        run.setOnAction(e -> {
            if(file != null && repPaths != null){
                try {
                    if(!col.getText().equals("")){
                        cols = Integer.parseInt(col.getText());
                    }
                    if(!row.getText().equals("")){
                        rows = Integer.parseInt(row.getText());
                    }
                    scale = scaleing.isSelected();
                    main();
                    image2.setImage(SwingFXUtils.toFXImage(combined, null));

                } catch (Exception e1) {
                    e1.printStackTrace();
                    log.getChildren().add(0, new Text("Error while calculating new image \n"));
                }
            }
            else{
                log.getChildren().add(0, new Text("Image or replacement images not selected \n"));
            }

        });
        save.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG", "*.jpeg"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GIF", "*.gif"));

            File file = fileChooser.showSaveDialog(primaryStage);

            try {
                ImageIO.write(combined, "PNG", file);

            } catch (Exception e1) {
                e1.printStackTrace();
                log.getChildren().add(0, new Text("Error while saving \n"));
            }

        });
    }

    public void main() throws Exception {

        FileInputStream fis = new FileInputStream(file);
        BufferedImage image = ImageIO.read(fis); //reading the image file

        int chunkWidth = image.getWidth() / cols;
        int chunkHeight = image.getHeight() / rows;
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
        log.getChildren().add(0, new Text("splitting done \n"));

        //load replacement images into array
        for (int i = 0; i < repCount; i++) {
            // read file from path
            // % repPaths.size to fill array even if not enough images in rep. folder
            File tempfile = new File(repPaths.get(i));
            FileInputStream tempfis = new FileInputStream(tempfile);

            // scale loaded image to fit chunk
            BufferedImage img = ImageIO.read(tempfis);

            if(scale){
                Image tmp = img.getSubimage(0,0,chunkWidth,chunkHeight);
                BufferedImage dimg = new BufferedImage(chunkWidth, chunkHeight, BufferedImage.TYPE_INT_ARGB);

                Graphics2D g2d = dimg.createGraphics();
                g2d.drawImage(tmp, 0, 0, null);
                g2d.dispose();

                // fill array of chunks with read images
                replacements[i] = new chunk(dimg);
            }
            else{
                // fill array of chunks with read images
                replacements[i] = new chunk(img);
            }


            log.getChildren().add(0, new Text(Integer.toString((i*100)/(repCount)) + "\n"));

            progress.setProgress((i*100)/(repCount));
        }
        log.getChildren().add(0, new Text("images loaded \n"));

        //for each chunk, calculate the euclidean distance to every possible replacement
        for (int i = 0; i < cols*rows; i++) {

            float[] distances = new float[repCount];
            int minEuclid;

            for (int j = 0; j < repCount; j++) {
                distances[j] = chunks[i].euclideanDistance(replacements[j].getAverage());
            }
            minEuclid = getIndexofLowest(distances);

            g.drawImage(replacements[minEuclid].getImg(), ((i%cols)* chunkWidth), ((i/cols)* chunkHeight), null);

        }
        log.getChildren().add(0, new Text("replacement done \n"));
    }

    /**
     * Returns all jpg images from a directory in an array.
     * Source: http://www.java2s.com/Code/Java/2D-Graphics-GUI/Returnsalljpgimagesfromadirectoryinanarray.htm
     * @param directory                 the directory to start with
     * @return an ArrayList<String> containing all the file paths or nul if none are found..
     * @throws Exception
     */
    private static ArrayList<String> getAllImages(File directory) throws Exception {
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
    private static int getIndexofLowest(float[] numbers){
        float minValue = numbers[0];
        int index = 0;
        for(int i=1;i<numbers.length;i++){
            if(numbers[i] < minValue){
                minValue = numbers[i];
                index = i;
            }
        }

        return index;
    }
}
