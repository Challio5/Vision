package nl.vision.model.grayfilter.smooth;

import nl.vision.model.ProcessImage;

/**
 * Created by
 *
 * @author Rob
 * @since 11-10-15.
 */
public class LaPlacian extends Smooth {
    @Override
    public void apply(ProcessImage image) {
        // Laplacian mask
        int[][] mime = new int[][]{
                {0, -1, 0},
                {-1, 4, -1},
                {0, -1, 0}
        };

        // Detect edge using smooth
        this.smooth(image, mime, this.getClass().getSimpleName());
    }
}
