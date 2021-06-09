package sample;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Controller {

    @FXML
    GridPane rootPane;

    private Image img;

    private Vector<Double> listSaturation = new Vector<Double>();

    private double imgMinSaturation = 1.0;
    private double imgMaxSaturation = 0.0;
    private double imgMeanSaturation;
    private double imgMedianSaturation;

    @FXML
    public void initialize() {
        img = new Image("https://cdn0.tnwcdn.com/wp-content/blogs.dir/1/files/2015/04/coloursquare.jpg?p=wp-content/blogs.dir/1/files/2015/04/coloursquare.jpg");

        saturationStatistics();
        generateSatLevelChart();
    }

    private void saturationStatistics() {
        PixelReader pxReader = img.getPixelReader();
        System.out.println("Image Width: " + img.getWidth());
        System.out.println("Image Height: " + img.getHeight());

        for (int readY = 0; readY < img.getHeight(); readY++) {
            for (int readX = 0; readX < img.getWidth(); readX++) {
                Color color = pxReader.getColor(readX, readY);

                listSaturation.add(color.getSaturation());

                if (color.getSaturation() < imgMinSaturation)
                    imgMinSaturation = color.getSaturation();
                else if (color.getSaturation() > imgMaxSaturation)
                    imgMaxSaturation = color.getSaturation();

            }
        }

        for (double e : listSaturation)
            imgMeanSaturation += e;
        imgMeanSaturation /= listSaturation.size();

        listSaturation.sort(null);
        imgMedianSaturation = listSaturation.get(listSaturation.size()/2);

        System.out.println("Minimal Saturation: " + imgMinSaturation);
        System.out.println("Maximal Saturation: " + imgMaxSaturation);
        System.out.println("Mean Saturation: " + imgMeanSaturation);
        System.out.println("Median Saturation: " + imgMedianSaturation);
    }

    private void generateSatLevelChart() {
        int depth = 2;                                                                                                                                                                  // Set to 2 since more precision rarely needed

        Hashtable<Double, Integer> uniqueValues = new Hashtable<>();

        double f = listSaturation.get(0);
        f = round(f, depth);
        int counter = 0;
        for (double e : listSaturation) {
            e = round(e, depth);
            if (e == f)
                counter++;
            else {
                uniqueValues.put(f, counter);
                counter = 1;
                f = e;
            }
        }

        TreeMap<Double, Integer> sortedUniqueValues = new TreeMap<>(uniqueValues);

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> saturationLevelChart = new BarChart<String, Number>(xAxis, yAxis);
        saturationLevelChart.setTitle("Saturation Levels");
        saturationLevelChart.setMinSize(1820, 980);
        xAxis.setLabel("Level");
        yAxis.setLabel("Occurence");

        XYChart.Series series1 = new XYChart.Series();

        for (Map.Entry<Double, Integer> entry: sortedUniqueValues.entrySet() ){
            Double key = entry.getKey();
            Integer val = entry.getValue();

            series1.getData().add(new XYChart.Data( key.toString(), val));
        }

        saturationLevelChart.getData().addAll(series1);
        rootPane.getChildren().add(saturationLevelChart);
    }

    public static double round(double value, int places) {
        // Method taken from https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
