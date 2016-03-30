package nl.vision.control;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import nl.vision.model.Context;
import nl.vision.model.Type;
import nl.vision.model.grayfilter.Gray;
import nl.vision.model.map.Disparity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by rob on 20-10-15.
 */
public class DragDropController implements Initializable {
    // Model
    private Context context;

    // View
    private BorderPane view;
    private ImageView mapView;

    @FXML private CheckBox mapBox;
    @FXML private VBox imageBox;
    @FXML private Label label;

    // Delegate nodes
    private ChoiceBox<String> imageTypeChooser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        context = Context.getInstance();
        mapView = new ImageView();
        mapView.imageProperty().bind(context.getImage().mapImageProperty());
    }

    // Get delegate nodes from maincontroller
    public void init(MainController controller) {
        view = controller.getView();
        imageTypeChooser = controller.getImageTypeChooser();
    }

    // Accept file drop on target
    @FXML
    private void dragOver(DragEvent event) {
        List<String> extensions = new ArrayList<>(Arrays.asList("jpg", "jpeg", "bmp", "gif", "png", "tiff"));

        if(event.getDragboard().hasFiles()) {

            for (File file : event.getDragboard().getFiles()) {
                String fileName = file.getName();
                String extension = fileName.substring(fileName.lastIndexOf('.') + 1);

                if(extensions.contains(extension)) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
            }
        }

        event.consume();
    }

    // Visualise entering target
    @FXML private void dragEnter(DragEvent event) {
        imageBox.setStyle(
                "-fx-border-style: dashed;" +
                        "-fx-border-radius: 10px;"
        );
    }

    // Visualise exiting target
    @FXML private void dragExit(DragEvent event) {
        imageBox.setStyle("-fx-border-style: none");
    }

    // Handle dropping of file
    @FXML private void dragDropped(DragEvent event) throws FileNotFoundException {
        // Boolean to check if drop succeeded
        boolean success = false;

        // Check if dropped item is file specified with dropover
        if (event.getDragboard().hasFiles()) {
            // Set succes to true
            success = true;

            // Get file from drop
            File file = null;
            for(File temp : event.getDragboard().getFiles()) {
                file = temp;
            }

            // Check if file is not null
            if(file != null) {
                // Set model (image)
                if(mapBox.isSelected()) {
                    context.getImage().setModel(new Image(new FileInputStream(file)), Type.MAP);

                    context.getImage().filter(new Gray());
                    context.getImage().filter(new Disparity(16, 5));


                    HBox hBox = ((HBox) view.getCenter());
                    hBox.getChildren().remove(1);
                    hBox.getChildren().add(mapView);

                    view.setBottom(null);
                } else {
                    context.getImage().setModel(new Image(new FileInputStream(file)), Type.RGB);
                    mapBox.setDisable(false);
                }

                // Set image types if not already set
                if(context.getImage().getTypeList().isEmpty()) {
                    context.getImage().getTypeList().addAll(context.getImage().getImageTypes());
                }

                // Select RGB
                imageTypeChooser.getSelectionModel().selectFirst();
            }
        }

        // Remove label if not already remove (only first image)
        if(imageBox.getChildren().contains(label)) {
            imageBox.getChildren().remove(label);
        }

        // Set success and consume event
        event.setDropCompleted(success);
        event.consume();
    }

    // Getters
    public VBox getImageBox() {
        return imageBox;
    }

    public Label getLabel() {
        return label;
    }
}
