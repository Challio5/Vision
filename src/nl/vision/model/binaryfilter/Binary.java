package nl.vision.model.binaryfilter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import nl.vision.model.Filter;
import nl.vision.model.ProcessImage;

import java.util.stream.IntStream;

/**
 * Created by
 *
 * @author Rob
 * @since 23-09-15.
 */
public class Binary implements Filter {

    private int threshold;

    public Binary(int threshold) {
        this.threshold = threshold;
    }

    public Binary() {
        this.threshold = 127;
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
        IntStream.range(0, width).parallel().forEach((x) ->
            IntStream.range(0, height).parallel().forEach((y) -> {
                // Read gray
                int color = image.getPreviousImage().getPixelReader().getArgb(x, y) & 0xFF;

                // Check threshold, greater then white and less then black
                if(color < threshold) {
                    color = 255;
                    image.getProcessedImage().getPixelWriter().setColor(x, y, Color.rgb(color, color, color));
                }
                else {
                    color = 0;
                    image.getProcessedImage().getPixelWriter().setColor(x, y, Color.rgb(color, color, color));
                }

                // Add value to datalist
                temp[color]++;
            })
        );

        // Add data to class
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
        for(int i = 0; i < temp.length; ++i) {
            data.add(new XYChart.Data<>(String.valueOf(i), temp[i]));
        }

        XYChart.Series<String, Number> serie = new XYChart.Series<>(data);
        serie.setName("Binary");

        boolean exists = image.getSeries().stream().anyMatch((item) -> item.getName().equals("Binary"));
        if(!exists) image.getSeries().add(serie);
    }
}
