package nl.vision.model.grayfilter;

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
 * @since 22-09-15.
 */
public class Gray implements Filter {
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
                // Read color of pixel
                Color gray = image.getOriginalImage().getPixelReader().getColor(x, y).grayscale();

                // Write color to pixel
                image.getProcessedImage().getPixelWriter().setColor(x, y, gray);

                // Add value to datalist
                temp[(int) (gray.getBlue() * 256.0)]++;
            })
        );

        // Add data to class
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
        for(int i = 0; i < temp.length; ++i) {
            data.add(new XYChart.Data<>(String.valueOf(i), temp[i]));
        }

        XYChart.Series<String, Number> serie = new XYChart.Series<>(data);
        serie.setName("Grayified");

        boolean exists = image.getSeries().stream().anyMatch((item) -> item.getName().equals("Grayified"));
        if(!exists) image.getSeries().add(serie);
    }
}
