package nl.vision.model;

/**
 * Created by rob on 20-10-15.
 */
public class Context {

    /* Model */
    private ProcessImage image = new ProcessImage();

    public ProcessImage getImage() {
        return image;
    }

    public void setImage(ProcessImage image) {
        this.image = image;
    }

    /* Singleton */
    private final static Context context = new Context();

    public static Context getInstance() {
        return context;
    }
}
