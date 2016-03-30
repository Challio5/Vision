package nl.vision.model.grayfilter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import nl.vision.model.Filter;
import nl.vision.model.ProcessImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by rob on 20-10-15.
 */
public class Median implements Filter {
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
                // List of surrounding pixels
                List<Integer> pixelList = new ArrayList<>();

                IntStream.range(0, 9).parallel().forEach((i) ->
                    pixelList.add(image.getPreviousImage().getPixelReader().getArgb((x - 1) + (i % 3), (y -1) + (i / 3)) & 0xFF));

                // Get median of 9 pixels (index == 4)
                Collections.sort(pixelList);
                int median = pixelList.get(4);

                // Write color to pixel
                image.getProcessedImage().getPixelWriter().setColor(x, y, Color.rgb(median, median, median));

                // Add to array
                temp[median]++;
            })
        );

        /* Loop over image
        for(int x = 1; x < image.getPreviousImage().getWidth() - 1; ++x) {
            for (int y = 1; y < image.getPreviousImage().getHeight() - 1; ++y) {
                // List of surrounding pixels
                List<Integer> pixelList = new ArrayList<>();

                for(int i = -1; i < 2; ++i) {
                    for(int j = -1; j < 2; ++j) {
                        pixelList.add(image.getPreviousImage().getPixelReader().getArgb(x - i, y - j) & 0xFF);
                    }
                }

                // Get median of 9 pixels (index == 4)
                Collections.sort(pixelList);
                int median = pixelList.get(4);

                // Write color to pixel
                image.getProcessedImage().getPixelWriter().setColor(x, y, Color.rgb(median, median, median));

                // Add to array
                temp[median]++;
            }
        }
        */

        // Add data to class
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
        for(int i = 0; i < temp.length; ++i) {
            data.add(new XYChart.Data<>(String.valueOf(i), temp[i]));
        }

        XYChart.Series<String, Number> serie = new XYChart.Series<>(data);
        serie.setName("Median");

        boolean exists = image.getSeries().stream().anyMatch((item) -> item.getName() == "Median");
        if(!exists) image.getSeries().add(serie);
    }
}
