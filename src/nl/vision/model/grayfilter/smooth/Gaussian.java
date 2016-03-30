package nl.vision.model.grayfilter.smooth;

import nl.vision.model.ProcessImage;

/**
 * Created by rob on 19-10-15.
 */
public class Gaussian extends Smooth {
    @Override
    public void apply(ProcessImage image) {
        // Gaussian mask
        int[][] mime = new int[][]{
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
        };

        // Smooth image
        this.smooth(image, mime, this.getClass().getSimpleName());
    }
}
