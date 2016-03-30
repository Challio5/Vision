package nl.vision.control;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import nl.vision.control.converter.FilterConverter;
import nl.vision.model.Context;
import nl.vision.model.Filter;
import nl.vision.model.Type;
import nl.vision.model.binaryfilter.Binary;
import nl.vision.model.grayfilter.Gray;
import nl.vision.view.Legend;

import java.util.ResourceBundle;

public class MainController {
    /* Model */
    private Context context;

    /* Control */
    @FXML private MenuController menuController;
    @FXML private DragDropController dragDropController;

    /* View */
    @FXML private BorderPane view;

    // Left
    @FXML private VBox dragDrop;

    // Right
    @FXML private BarChart<String,Number> chart;

    // Bottom
    @FXML private HBox controlBar;
    @FXML private ChoiceBox<String> imageTypeChooser;
    @FXML private ChoiceBox<Filter> filterTypeChooser;
    @FXML private TextField thresholdField;

    /* Resources */
    @FXML private ResourceBundle bundle;

    // Constructor from JVE
    @FXML
    private void initialize() {
        // Set context (Model)
        context = Context.getInstance();

        // Delegate maincontroller
        menuController.init(this);
        dragDropController.init(this);

        // Add to view
        Legend legend = new Legend();
        legend.setMap(context.getImage().getShapeList());
        dragDrop.getChildren().add(legend);

        // Bind model to view
        imageTypeChooser.setItems(context.getImage().getTypeList());
        filterTypeChooser.setItems(context.getImage().getFilterList());

        chart.setData(context.getImage().getSeries());
        ((ImageView) (dragDrop.getChildren().get(0))).imageProperty().bind(context.getImage().previousImageProperty());
        ((ImageView) (dragDrop.getChildren().get(2))).imageProperty().bind(context.getImage().processedImageProperty());

        // Set listeners for type (RGB, Gray, Binary) and filter (Smooth, Erosion etc.)
        imageTypeChooser.getSelectionModel().selectedItemProperty().addListener(this::typeChange);
        filterTypeChooser.getSelectionModel().selectedItemProperty().addListener(this::filterChange);

        // Set converter for string representation filter
        filterTypeChooser.setConverter(new FilterConverter());
    }

    // Changelistener to turn image to the right type
    private void typeChange(ObservableValue observable, String oldValue, String newValue) {
        if(newValue != null) {
            switch (newValue) {
                case "RGB":
                    // Reset image and graph
                    context.getImage().getSeries().clear();
                    context.getImage().reset();

                    // Set type to RGB
                    context.getImage().setImageType(Type.RGB);

                    // Set filter types
                    context.getImage().getFilterList().clear();
                    break;
                case "Grayscale":
                    // Turn image gray and get pixelcount
                    context.getImage().filter(new Gray());

                    // Set type to Gray
                    context.getImage().setImageType(Type.GRAY);

                    // Set filter types
                    context.getImage().getFilterList().clear();
                    context.getImage().getFilterList().addAll(context.getImage().getGrayFilterTypes());
                    break;
                case "Binary":
                    if(context.getImage().getImageType() == Type.RGB) {
                        context.getImage().filter(new Gray());
                    }

                    // Turn image to binary
                    if(thresholdField.getText().matches("\\D*")) {
                        context.getImage().filter(new Binary());
                        thresholdField.clear();
                    } else {
                        int threshold = Integer.parseInt(thresholdField.getText());
                        context.getImage().filter(new Binary(threshold));
                        thresholdField.clear();
                    }

                    // Set type to Binary
                    context.getImage().setImageType(Type.BINARY);

                    // Set filter types
                    context.getImage().getFilterList().clear();
                    context.getImage().getFilterList().addAll(context.getImage().getBinaryFilterTypes());
                    break;
            }
        }
    }

    // Changelistener to give the picture the right filter
    private void filterChange(ObservableValue observable, Filter oldValue, Filter newValue) {
        if(newValue != null) {
            context.getImage().filter(newValue);
        }
    }

    // Getters
    public BorderPane getView() {
        return view;
    }
    public HBox getControlBar() {
        return controlBar;
    }

    public DragDropController getDragDropController() {
        return dragDropController;
    }

    public MenuController getMenuController() {
        return menuController;
    }

    public ChoiceBox<String> getImageTypeChooser() {
        return imageTypeChooser;
    }
}
