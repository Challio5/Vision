package nl.vision.model.binaryfilter;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import nl.vision.model.Coordinate;
import nl.vision.model.Filter;
import nl.vision.model.ProcessImage;

import java.util.stream.IntStream;

/**
 * Created by rob on 26-10-15.
 */
public class Edge implements Filter {

    private int perimeter;

    private Coordinate upperCoordinate;
    private Coordinate lowerCoordinate;

    public Edge(Coordinate upperCoordinate, Coordinate lowerCoordinate) {
        this.upperCoordinate = upperCoordinate;
        this.lowerCoordinate = lowerCoordinate;
    }

    @Override
    public void apply(ProcessImage image) {
        IntStream.range(upperCoordinate.getX(), lowerCoordinate.getX() + 1).parallel().forEach((x) ->
            IntStream.range(upperCoordinate.getY(), upperCoordinate.getY() + 1).parallel().forEach((y) -> {
                // 3x3 scan of pixel
                int sum = IntStream.range(0, 9).boxed().parallel().mapToInt((i) -> {
                    // Get factor from mime
                    int factor = -1;

                    // Mid pixel
                    if ((i % 3) == 1 && (i / 3) == 1) factor = 8;

                    // Multiply mime factor with pixel
                    int pixel = image.getPreviousImage().getPixelReader().getArgb(
                            (x - 1) - (i % 3), (y - 1) - (i / 3)) & 0xFF;

                    return pixel * factor;
                }).sum();

                int avg = Math.min(255, Math.max(0, sum));

                if (avg != 0) perimeter++;
            })
        );

        // Loop over image
        /*
        for(int x = upperCoordinate.getX(); x <= lowerCoordinate.getX(); ++x) {
            for (int y = upperCoordinate.getY(); y <= lowerCoordinate.getY(); ++y) {
                // 3x3 scan of pixel
                int sum = 0;

                for (int i = -1; i < 2; ++i) {
                    for (int j = -1; j < 2; ++j) {
                        // Get factor from mime
                        int factor = -1;

                        // Mid pixel
                        if (i == 0 && j == 0) factor = 8;

                        // Multiply mime factor with pixel
                        int pixel = image.getPreviousImage().getPixelReader().getArgb(x - i, y - j) & 0xFF;
                        sum += pixel * factor;
                    }
                }
                int avg = Math.min(255, Math.max(0, sum));

                if (avg != 0) perimeter++;
            }
        }
        */
    }

    public int getPerimeter() {
        return perimeter;
    }
}
