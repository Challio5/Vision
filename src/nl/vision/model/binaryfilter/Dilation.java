package nl.vision.model.binaryfilter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import nl.vision.model.Filter;
import nl.vision.model.ProcessImage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by
 *
 * @author Rob
 * @since 23-09-15.
 */
public class Dilation implements Filter {
    private int scan;

    public Dilation() {
        scan = 4;
    }

    public Dilation(int scan) {
        this.scan = scan;
    }

    @Override
    public void apply(ProcessImage image) {
        // Set previous image
        image.setPreviousImage(new WritableImage(image.getProcessedImage().getPixelReader(),
                (int) image.getOriginalImage().getWidth(), (int) image.getOriginalImage().getHeight()));

        // Temp data
        long[] temp = new long[257];

        // Width and height of picture
        int width = (int) image.getOriginalImage().getWidth();
        int height = (int) image.getOriginalImage().getHeight();

        // Loop over image
        IntStream.range(1, width - 1).parallel().forEach((x) ->
            IntStream.range(1, height - 1).parallel().forEach((y) -> {
                // List of pixels
                List<Integer> pixelList = new ArrayList<>();

                // Scan surrounding pixels
                if(scan == 4) {
                    pixelList.add(image.getPreviousImage().getPixelReader().getArgb(x , y - 1));
                    pixelList.add(image.getPreviousImage().getPixelReader().getArgb(x + 1, y));
                    pixelList.add(image.getPreviousImage().getPixelReader().getArgb(x , y + 1));
                    pixelList.add(image.getPreviousImage().getPixelReader().getArgb(x - 1 , y));

                    boolean white = pixelList.stream().anyMatch((pixel) -> (pixel & 0xFF) == 255);

                    if(white) {
                        image.getProcessedImage().getPixelWriter().setColor(x, y, Color.rgb(255, 255, 255));
                        temp[0]++;
                    }
                    else {
                        image.getProcessedImage().getPixelWriter().setColor(x, y, Color.rgb(0, 0, 0));
                        temp[255]++;
                    }
                }
                else {
                    System.out.println("Error");
                    return;
                }
            })
        );

        /*
        for(int x = 1; x < image.getPreviousImage().getWidth() - 1; ++x) {
            for (int y = 1; y < image.getPreviousImage().getHeight() - 1; ++y) {
                // List of pixels
                List<Integer> pixelList = new ArrayList<>();

                // Scan surrounding pixels
                if(scan == 4) {
                    pixelList.add(image.getPreviousImage().getPixelReader().getArgb(x , y - 1));
                    pixelList.add(image.getPreviousImage().getPixelReader().getArgb(x + 1, y));
                    pixelList.add(image.getPreviousImage().getPixelReader().getArgb(x , y + 1));
                    pixelList.add(image.getPreviousImage().getPixelReader().getArgb(x - 1 , y));

                    boolean white = pixelList.stream().anyMatch((pixel) -> (pixel & 0xFF) == 255);

                    if(white) {
                        image.getProcessedImage().getPixelWriter().setColor(x, y, Color.rgb(255, 255, 255));
                        temp[0]++;
                    }
                    else {
                        image.getProcessedImage().getPixelWriter().setColor(x, y, Color.rgb(0, 0, 0));
                        temp[255]++;
                    }
                }
                else if(scan == 8) {
                    for(int i = -1; i < 2; ++i) {
                        for(int j = -1; j < 2; ++j) {

                        }
                    }
                }
                else {
                    System.out.println("Error");
                    return;
                }
            }
        }
        */

        // Add data to class
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
        for(int i = 0; i < temp.length; ++i) {
            data.add(new XYChart.Data<>(String.valueOf(i), temp[i]));
        }

        XYChart.Series<String, Number> serie = new XYChart.Series<>(data);
        serie.setName("Dilation");

        boolean exists = image.getSeries().stream().anyMatch((item) -> item.getName() == "Dilation");
        if(!exists) image.getSeries().add(serie);
    }
}
