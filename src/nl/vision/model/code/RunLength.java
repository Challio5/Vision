package nl.vision.model.code;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.Arrays;

/**
 * Created by rob on 22-10-15.
 */
public class RunLength implements Encode, Decode {
    @Override
    public void encode(WritableImage image, File file) {
        StringBuilder builder = new StringBuilder();

        int oldValue = image.getPixelReader().getArgb(0, 0) & 0xFF;

        int counter = 0;

        for(int y = 0; y < image.getHeight(); ++y) {
            for(int x = 0; x < image.getWidth(); ++x) {
                int pixel = image.getPixelReader().getArgb(x, y) & 0xFF;

                if(pixel == oldValue) {
                    counter++;
                }
                else {
                    // Black
                    if(oldValue == 0) {
                        builder.append(counter);
                        builder.append('B');
                        oldValue = pixel;
                        counter = 1;
                    }

                    // White
                    else if(oldValue == 255) {
                        builder.append(counter);
                        builder.append('W');
                        oldValue = pixel;
                        counter = 1;
                    }

                    // Unknown pixel
                    else {
                        System.err.println("No binary");
                    }
                }
            }

            // End of row
            // Black
            if(oldValue == 0) {
                builder.append(counter);
                builder.append('B');
                builder.append('\n');
                oldValue = image.getPixelReader().getArgb(0, y) & 0xFF;
                counter = 0;
            }

            // White
            else if(oldValue == 255) {
                builder.append(counter);
                builder.append('W');
                builder.append('\n');
                oldValue = image.getPixelReader().getArgb(0, y) & 0xFF;
                counter = 0;
            }

            // Unknown pixel
            else {
                System.err.println("No binary");
            }
        }

        try {
            PrintWriter writer = new PrintWriter(file);
            writer.write(builder.toString());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Image decode(File file) {
        // Image
        WritableImage image = null;

        try {
            // Reader of encoded binary
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // Calculate width and height
            int width = Arrays.stream(reader.readLine().split("[BW]"))
                    .mapToInt(Integer::parseInt).sum();
            int height = reader.lines().mapToInt(e -> 1).sum() + 1;

            // Create image dimensions
            image = new WritableImage(width, height);

            // Reopen buffer
            reader = new BufferedReader(new FileReader(file));

            // Write image pixels
            String line;
            for(int y = 0; (line = reader.readLine()) != null; ++y) {
                String[] numbers = line.split("[BW]");
                String[] binary = line.split("\\d+");
                binary = Arrays.copyOfRange(binary, 1, binary.length);

                if(numbers.length == binary.length) {
                    // Sum of length
                    int sum = 0;

                    // Loop over numbers
                    for(int i = 0; i < numbers.length; ++i) {
                        switch (binary[i]) {
                            case "W":
                                // Write white pixels times number
                                int wCount = Integer.parseInt(numbers[i]);
                                //System.out.print(" White: " + wCount);
                                for(int x = 0; x < wCount; ++x) {
                                    image.getPixelWriter().setColor(x + sum, y, Color.rgb(255, 255, 255));
                                }

                                // Add count to sum
                                sum += wCount;
                                break;
                            case "B":
                                // Write black pixels times number
                                int bCount = Integer.parseInt(numbers[i]);
                                //System.out.print(" Black: " + bCount);
                                for(int x = 0; x < bCount; ++x) {
                                    image.getPixelWriter().setColor(x + sum, y, Color.rgb(0, 0, 0));
                                }

                                // Add count to sum
                                sum += bCount;
                                break;
                        }
                    }
                }
                else {
                    System.err.println("Error parsing encoding file");
                }

                //System.out.println();
            }

            // Close reader
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
