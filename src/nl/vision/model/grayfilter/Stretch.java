package nl.vision.model.grayfilter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import nl.vision.model.Filter;
import nl.vision.model.ProcessImage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by
 *
 * @author Rob
 * @since 22-09-15.
 */
public class Stretch implements Filter {
    private int threshold;

    public Stretch() {
        threshold = 4;
    }

    public Stretch(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public void apply(ProcessImage image) {
        // Set previous image
        image.setPreviousImage(new WritableImage(image.getProcessedImage().getPixelReader(),
                (int) image.getOriginalImage().getWidth(), (int) image.getOriginalImage().getHeight()));

        // Remove data under threshold
        List<XYChart.Data<String,Number>> list =
                image.getSeries().get(0).getData().stream().filter(item ->
                        item.getYValue().intValue() > threshold).collect(Collectors.toList());

        // Get min/max value
        int min = Integer.parseInt(list.get(0).getXValue());
        int max = Integer.parseInt(list.get(list.size() - 1).getXValue());

        // Temp data
        long[] temp = new long[257];

        // Width and height of picture
        int width = (int) image.getOriginalImage().getWidth();
        int height = (int) image.getOriginalImage().getHeight();

        // Loop over image
        IntStream.range(0, width).parallel().forEach((x) ->
            IntStream.range(0, height).parallel().forEach((y) -> {
                // Read color of pixel
                int color = image.getPreviousImage().getPixelReader().getArgb(x, y) & 0xFF;

                // Calculate stretched value
                int sValue = (int) ((color - min) * (255.0 / (max - min)));

                if(sValue < 0) {
                    sValue = 0;
                }

                if(sValue > 255) {
                    sValue = 255;
                }

                // Write color to pixel
                image.getProcessedImage().getPixelWriter().setColor(x, y, Color.rgb(sValue, sValue, sValue));

                // Add to array
                temp[sValue]++;
            })
        );

        /*
        for(int x = 0; x < image.getPreviousImage().getWidth(); ++x) {
            for(int y = 0; y < image.getPreviousImage().getHeight(); ++y) {
                // Read color of pixel
                int color = image.getPreviousImage().getPixelReader().getArgb(x, y) & 0xFF;

                // Calculate stretched value
                int sValue = (int) ((color - min) * (255.0 / (max - min)));

                if(sValue < 0) {
                    sValue = 0;
                }

                if(sValue > 255) {
                    sValue = 255;
                }

                // Write color to pixel
                image.getProcessedImage().getPixelWriter().setColor(x, y, Color.rgb(sValue, sValue, sValue));

                // Add to array
                temp[sValue]++;
            }
        }
        */

        // Add data to class
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
        for(int i = 0; i < temp.length; ++i) {
            data.add(new XYChart.Data<>(String.valueOf(i), temp[i]));
        }

        XYChart.Series<String, Number> serie = new XYChart.Series<>(data);
        serie.setName("Stretched");

        boolean exists = image.getSeries().stream().anyMatch((item) -> item.getName() == "Stretched");
        if(!exists) image.getSeries().add(serie);
    }
}
