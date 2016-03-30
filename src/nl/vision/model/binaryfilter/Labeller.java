package nl.vision.model.binaryfilter;

import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import nl.vision.model.Coordinate;
import nl.vision.model.Filter;
import nl.vision.model.ProcessImage;
import nl.vision.model.Shape;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by rob on 23-10-15.
 */
public class Labeller implements Filter {
    private int labelNumber = 1;

    @Override
    public void apply(ProcessImage image) {
        // Width and length of image
        int width = (int) image.getOriginalImage().getWidth();
        int height = (int) image.getOriginalImage().getHeight();

        // Array with labelled pixels (0 = background (initialized))
        int[][] labels = new int[width][height];

        // Counter for labelling (0 = background)
        //int labelNumber = 1;

        // Set previous image
        image.setPreviousImage(new WritableImage(image.getProcessedImage().getPixelReader(), width, height));

        // Pixellist
        List<Coordinate> pixelList = new ArrayList<>();

        // Pixelqueue
        Queue<Coordinate> pixelQueue = new LinkedList<>();

        // Print
        String category = String.format("%-18s%-18s%-18s%-18s\n",
                "Shape", "Area", "Perimeter", "Centroid"
        );
        System.out.print(category);

        // Loop over image
        IntStream.range(1, width - 1).parallel().forEach((x) ->
            IntStream.range(1, height - 1).parallel().forEach((y) -> {
                // Get pixelReader
                PixelReader reader = image.getPreviousImage().getPixelReader();

                // Get pixel value
                int pixel = reader.getArgb(x , y) & 0xFF;

                // Check if foreground pixel
                if(pixel != 0) {
                    // Check if not already labeled
                    if(labels[x][y] == 0) {
                        labels[x][y] = labelNumber;
                        pixelQueue.add(new Coordinate(x, y));

                        // Make queue with pixel and unlabeled neighbours (white pixels)
                        while(!pixelQueue.isEmpty()) {
                            // Get pixel coordinates
                            Coordinate coordinate = pixelQueue.poll();
                            pixelList.add(coordinate);

                            // Loop over neighbour pixels
                            for (int i = -1; i < 2; ++i) {
                                for (int j = -1; j < 2; ++j) {
                                    // Set coordinates to neighbour
                                    int x1 = coordinate.getX() + i;
                                    int y1 = coordinate.getY() + j;

                                    // Get neighbour pixel
                                    int neighbour = reader.getArgb(x1, y1) & 0xFF;

                                    // Check if not already labeled
                                    if(neighbour != 0 && labels[x1][y1] == 0) {
                                        // Label neighbour pixel
                                        labels[x1][y1] = labelNumber;

                                        // Add neighbour pixel to queue
                                        pixelQueue.add(new Coordinate(x1, y1));
                                    }
                                }
                            }
                        }
                    }

                    // Increase labelnumber
                    labelNumber++;

                    // Shape detection
                    if(!pixelList.isEmpty()) {
                        // Create color
                        Random random = new Random();
                        Color color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));

                        // Shape detection
                        this.shapeDetecter(image, color, pixelList);

                        // Color pixels
                        pixelList.stream().forEach(coordinate ->
                                image.getProcessedImage().getPixelWriter().setColor(
                                        coordinate.getX(), coordinate.getY(), color));

                        // Clear list
                        pixelList.clear();
                    }
                }
            })
        );

        /*
        for (int x = 1; x < width - 1; ++x) {
            for (int y = 1; y < height - 1; ++y) {
                // Get pixelReader
                PixelReader reader = image.getPreviousImage().getPixelReader();

                // Get pixel value
                int pixel = reader.getArgb(x , y) & 0xFF;

                // Check if foreground pixel
                if(pixel != 0) {
                    // Check if not already labeled
                    if(labels[x][y] == 0) {
                        labels[x][y] = labelNumber;
                        pixelQueue.add(new Coordinate(x, y));

                        // Make queue with pixel and unlabeled neighbours (white pixels)
                        while(!pixelQueue.isEmpty()) {
                            // Get pixel coordinates
                            Coordinate coordinate = pixelQueue.poll();
                            pixelList.add(coordinate);

                            // Loop over neighbour pixels
                            for (int i = -1; i < 2; ++i) {
                                for (int j = -1; j < 2; ++j) {
                                    // Set coordinates to neighbour
                                    int x1 = coordinate.getX() + i;
                                    int y1 = coordinate.getY() + j;

                                    // Get neighbour pixel
                                    int neighbour = reader.getArgb(x1, y1) & 0xFF;

                                    // Check if not already labeled
                                    if(neighbour != 0 && labels[x1][y1] == 0) {
                                        // Label neighbour pixel
                                        labels[x1][y1] = labelNumber;

                                        // Add neighbour pixel to queue
                                        pixelQueue.add(new Coordinate(x1, y1));
                                    }
                                }
                            }
                        }
                    }

                    // Increase labelnumber
                    labelNumber++;

                    // Shape detection
                    if(!pixelList.isEmpty()) {
                        // Create color
                        Random random = new Random();
                        Color color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));

                        // Shape detection
                        this.shapeDetecter(image, color, pixelList);

                        // Color pixels
                        pixelList.stream().forEach(coordinate ->
                                image.getProcessedImage().getPixelWriter().setColor(
                                        coordinate.getX(), coordinate.getY(), color));

                        // Clear list
                        pixelList.clear();
                    }
                }
            }
        }
        */
    }

    private void shapeDetecter(ProcessImage image, Color color, List<Coordinate> pixelList) {
        // Bounding box
        int xmin = pixelList.stream().mapToInt(Coordinate::getX).min().getAsInt();
        int xmax = pixelList.stream().mapToInt(Coordinate::getX).max().getAsInt();
        int ymin = pixelList.stream().mapToInt(Coordinate::getY).min().getAsInt();
        int ymax = pixelList.stream().mapToInt(Coordinate::getY).max().getAsInt();

        // Edge detection
        Edge edge = new Edge(new Coordinate(xmin, ymin), new Coordinate(xmax, ymax));
        edge.apply(image);

        // Area and perimeter
        int area = pixelList.size();
        int perimeter = edge.getPerimeter();
        Coordinate centroid = new Coordinate(xmin + (xmax + xmin / 2), ymin + (ymax + ymin / 2));

        // Circle
        double circleperm = 2.0 * Math.PI * (((xmax + 1) - xmin) / 2.0);
        double circle = (4.0 * Math.PI * area) / (Math.pow(circleperm, 2.0));

        // Area bouding box
        int box = ((xmax + 1) - xmin) * ((ymax + 1) - ymin);

        // Shape detection
        if (box == area) {
            image.getShapeList().put(color, Shape.SQUARE);
            String string = String.format("%-18s", "Square");
            System.out.print(string);
        }
        else if (box / 2 % area <= 10 ||  box / 2 % area >= area - 10) {
            image.getShapeList().put(color, Shape.TRIANGLE);
            String string = String.format("%-18s", "Triangle");
            System.out.print(string);
        }
        else if(circle % 1 < 0.01 || circle % 1 > 0.99) {
            image.getShapeList().put(color, Shape.CIRCLE);
            String string = String.format("%-18s", "Circle");
            System.out.print(string);
        }
        else {
            String string = String.format("%-18s", "-");
            System.out.print(string);
        }

        // Print info
        String string = String.format("%-18d%-18s%-18s",
                area,
                perimeter,
                centroid
        );

        System.out.println(string);
    }
}
