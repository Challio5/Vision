package nl.vision.model.grayfilter.smooth;

import nl.vision.model.ProcessImage;

/**
 * Created by rob on 19-10-15.
 */
public class Mean extends Smooth {
    @Override
    public void apply(ProcessImage image) {
        // Mean mask
        int[][] mime = new int[][]{
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1}
        };

        // Smooth image
        this.smooth(image, mime, this.getClass().getSimpleName());
    }
}
