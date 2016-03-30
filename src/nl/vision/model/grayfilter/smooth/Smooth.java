package nl.vision.model.grayfilter.smooth;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import nl.vision.model.Filter;
import nl.vision.model.ProcessImage;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Created by
 *
 * @author Rob
 * @since 22-09-15.
 */
public abstract class Smooth implements Filter {
    public void smooth(ProcessImage image, int[][] mime, String name) {
        // Set previous image
        image.setPreviousImage(new WritableImage(image.getProcessedImage().getPixelReader(),
                (int) image.getOriginalImage().getWidth(), (int) image.getOriginalImage().getHeight()));

        // Temp data
        long[] temp = new long[257];

        // Width and height of picture
        int width = (int) image.getOriginalImage().getWidth();
        int height = (int) image.getOriginalImage().getHeight();

        // Check mime
        int size = mime.length;
        int total = IntStream.range(0, (int) (Math.pow(size, 2))).boxed().parallel().mapToInt((i) ->
            mime[i % 3][i / 3]).sum();

        // Loop over image
        IntStream.range(1, width - 1).parallel().forEach((x) ->
            IntStream.range(1, height - 1).parallel().forEach((y) -> {
                // 3x3 scan of pixel
                int sum = IntStream.range(0, 9).boxed().parallel().mapToInt((i) ->
                        mime[i % 3][i / 3] *
                                (image.getPreviousImage().getPixelReader().getArgb(
                                        (x - 1) + (i % 3), (y - 1) + (i % 3)) & 0xFF))
                .sum();

                int avg;
                if(total == 0) {
                    // Min/Max values
                    int min = Arrays.stream(mime).flatMapToInt(Arrays::stream).filter(i -> i < 0).sum() * 255;
                    int max = Arrays.stream(mime).flatMapToInt(Arrays::stream).filter(i -> i >= 0).sum() * 255;

                    // Rescale
                    avg = (int) ((sum - min) * (255.0 / (max - min)));

                } else {
                    avg = sum / total;
                }

                // Write color to pixel
                image.getProcessedImage().getPixelWriter().setColor(x, y, Color.rgb(avg, avg, avg));

                // Add to array
                temp[avg]++;
            })
        );

        // Add data to class
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
        for(int i = 0; i < temp.length; ++i) {
            data.add(new XYChart.Data<>(String.valueOf(i), temp[i]));
        }

        XYChart.Series<String, Number> serie = new XYChart.Series<>(data);
        serie.setName(name);

        boolean exists = image.getSeries().stream().anyMatch((item) -> item.getName().equals(name));
        if(!exists) image.getSeries().add(serie);
    }
}
