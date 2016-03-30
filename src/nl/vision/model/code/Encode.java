package nl.vision.model.code;

import javafx.scene.image.WritableImage;

import java.io.File;

/**
 * Created by rob on 20-10-15.
 */
public interface Encode {
    void encode(WritableImage image, File file);
}
