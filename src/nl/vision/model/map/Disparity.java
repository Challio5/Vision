package nl.vision.model.map;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import nl.vision.model.Filter;
import nl.vision.model.ProcessImage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by rob on 09-11-15
 */
public class Disparity implements Filter {

    private int disparity;
    private int wHalfSize;
    private int counter = 0;

    private List<Integer> disparityList;

    public Disparity(int disparity, int wSize){
        this.disparity = disparity;
        wHalfSize = (wSize - 1) / 2;
        disparityList = new ArrayList<>();
    }

    @Override
    public void apply(ProcessImage image) {
        int width = (int) image.getPreviousImage().getWidth();
        int height = (int) image.getPreviousImage().getHeight();

        PixelReader leftImageReader = image.getPreviousImage().getPixelReader();
        PixelReader rightImageReader = image.getProcessedImage().getPixelReader();

        WritableImage mapImage = new WritableImage(width, height);
        PixelWriter mapWriter = mapImage.getPixelWriter();

        // Loop over image
        IntStream.range(0, width).forEach(x ->
            IntStream.range(0, height).forEach(y -> {

                // Out of range
                if( x <= disparity + wHalfSize ||
                    x >= width - disparity - wHalfSize ||
                    y <= wHalfSize ||
                    y >= height - wHalfSize)
                {
                        mapWriter.setColor(x, y, Color.BLACK);
                        return;
                }

                // Left window
                int[][] leftWindow = new int[5][5];
                IntStream.range(0, 25).parallel().forEach(i ->
                        leftWindow[i % 5][i / 5] = leftImageReader.getArgb(
                                x + (i % 5) - wHalfSize, y + (i / 5) - wHalfSize) & 0xFF);

                // Disparity Check
                IntStream.range(-disparity, disparity + 1).forEach(d -> {
                    int sum = IntStream.range(0, 25).boxed().mapToInt(
                            (i) -> Math.abs(leftWindow[i % 5][i / 5] - (rightImageReader.getArgb(
                                    d + x + ((i % 5) - wHalfSize), y + ((i / 5) - wHalfSize)) & 0xFF))).sum();

                    // Add sum
                    disparityList.add(sum);
                });

                // Check lowest cost
                int index = IntStream.range(0, disparityList.size()).parallel().reduce(
                        (i, j) -> (disparityList.get(i) < disparityList.get(j)) ? i : j).getAsInt();

                // Remap
                int disparity = Math.abs(index - 16);

                // Clear list
                disparityList.clear();

                // Color
                Color color = colorMap(disparity);

                // Map
                mapImage.getPixelWriter().setColor(x, y, color);
            })
        );

        // Set image
        image.setMapImage(mapImage);
    }

    public Color colorMap(int disparity) {
        switch(disparity){
            case 0:
                return Color.web("0x0000BF");
            case 1:
                return Color.web("0x0000FF");
            case 2:
                return Color.web("0x003FFF");
            case 3:
                return Color.web("0x007FFF");
            case 4:
                return Color.web("0x00BFFF");
            case 5:
                return Color.web("0x00FFFF");
            case 6:
                return Color.web("0x3FFFBF");
            case 7:
                return Color.web("0x7FFF7F");
            case 8:
                return Color.web("0xBFFF3F");
            case 9:
                return Color.web("0xFFFF00");
            case 10:
                return Color.web("0xFFBF00");
            case 11:
                return Color.web("0xFF7400");
            case 12:
                return Color.web("0xFF3F00");
            case 13:
                return Color.web("0xFF0000");
            case 14:
                return Color.web("0xBF0000");
            case 15:
                return Color.web("0x7F0000");
            case 16:
                return Color.web("0x5F0000");
            default:
                return Color.web("0xFFFFFF");
        }
    }
}
