package nl.vision.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import nl.vision.model.binaryfilter.Dilation;
import nl.vision.model.binaryfilter.Erosion;
import nl.vision.model.binaryfilter.Labeller;
import nl.vision.model.grayfilter.Median;
import nl.vision.model.grayfilter.Stretch;
import nl.vision.model.grayfilter.smooth.Gaussian;
import nl.vision.model.grayfilter.smooth.LaPlacian;
import nl.vision.model.grayfilter.smooth.Mean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by
 *
 * @author Rob
 * @since 10-09-15.
 */
public class ProcessImage {
    // Static type of image
    private Type imageType;

    // Static original image
    private Image originalImage;

    // Static supported image and filter types
    private List<String> imageTypes;
    private List<Filter> grayFilterTypes;
    private List<Filter> binaryFilterTypes;

    // Dynamic images for applying filters
    private ObjectProperty<WritableImage> previousImage;
    private ObjectProperty<WritableImage> processedImage;

    // Map image
    private ObjectProperty<WritableImage>  mapImage;

    // Dynamic lists for displaying chart data, types and filters
    private ObservableList<XYChart.Series<String,Number>> series;
    private ObservableList<String> typeList;
    private ObservableList<Filter> filterList;
    private ObservableMap<Color, Shape> shapeList;

    // Constructor
    public ProcessImage() {
        // Image type
        this.imageType = Type.NONE;

        // Empty image
        originalImage = null;
        mapImage = new SimpleObjectProperty<>(new WritableImage(300, 200));

        previousImage = new SimpleObjectProperty<>(new WritableImage(300, 200));
        processedImage = new SimpleObjectProperty<>(new WritableImage(300, 200));

        // Supported imagetypes and filter
        imageTypes = new ArrayList<>(Arrays.asList("RGB", "Grayscale", "Binary"));
        grayFilterTypes = new ArrayList<>(Arrays.asList(new Stretch(), new Mean(), new Gaussian(), new LaPlacian(), new Median()));
        binaryFilterTypes = new ArrayList<>(Arrays.asList(new Erosion(), new Dilation(), new Labeller()));

        // Lists for displaying chart data, image types and filters
        series = FXCollections.observableArrayList();
        typeList = FXCollections.observableArrayList();
        filterList = FXCollections.observableArrayList();
        shapeList = FXCollections.observableHashMap();
    }

    // Method to apply an filter to the picture
    public void filter(Filter filter) {
        filter.apply(this);
    }

    // Methode to reset the images and data
    public void reset() {
        previousImage.set(new WritableImage(
                originalImage.getPixelReader(), (int) originalImage.getWidth(), (int) originalImage.getHeight()));

        processedImage.set(new WritableImage(
                originalImage.getPixelReader(), (int) originalImage.getWidth(), (int) originalImage.getHeight()));
    }

    // Getters
    public Type getImageType() {
        return imageType;
    }

    public Image getOriginalImage() {
        return originalImage;
    }

    public WritableImage getPreviousImage() {
        return previousImage.get();
    }

    public WritableImage getProcessedImage() {
        return processedImage.get();
    }

    public List<String> getImageTypes() {
        return imageTypes;
    }

    public List<Filter> getGrayFilterTypes() {
        return grayFilterTypes;
    }

    public List<Filter> getBinaryFilterTypes() {
        return binaryFilterTypes;
    }

    public ObservableList<String> getTypeList() {
        return typeList;
    }

    public ObservableList<Filter> getFilterList() {
        return filterList;
    }

    public ObservableMap<Color, Shape> getShapeList() {
        return shapeList;
    }

    // Setters
    public void setModel(Image image, Type imageType) {
        if(imageType == Type.MAP) {
            this.processedImage.set(new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight()));
        } else {
            this.previousImage.set(new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight()));
            this.processedImage.set(new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight()));

            this.originalImage = image;
        }

        this.imageType = imageType;
    }

    // Map image
    public WritableImage getMapImage() {
        return mapImage.get();
    }

    public void setMapImage(WritableImage mapImage) {
        this.mapImage.set(mapImage);
    }

    public void setImageType(Type imageType) {
        this.imageType = imageType;
    }

    public void setPreviousImage(WritableImage previousImage) {
        this.previousImage.set(previousImage);
    }

    // Properties
    public ObjectProperty<WritableImage> mapImageProperty() {
        return mapImage;
    }

    public ObjectProperty<WritableImage> previousImageProperty() {
        return previousImage;
    }

    public ObjectProperty<WritableImage> processedImageProperty() {
        return processedImage;
    }

    public ObservableList<XYChart.Series<String, Number>> getSeries() {
        return series;
    }


}
