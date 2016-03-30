package nl.vision.control;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import nl.vision.model.Context;
import nl.vision.model.Type;
import nl.vision.model.binaryfilter.Binary;
import nl.vision.model.code.Decode;
import nl.vision.model.code.Encode;
import nl.vision.model.code.RunLength;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by rob on 20-10-15.
 */
public class MenuController implements Initializable {
    // Context (Model)
    private Context context;

    // Delegate nodes
    private ChoiceBox<String> imageTypeChooser;
    private VBox imageBox;
    private Label label;

    @FXML private MenuBar bar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        context = Context.getInstance();
        //bar.setUseSystemMenuBar(true);
    }

    // Get delegate nodes from maincontroller
    public void init(MainController controller) {
        imageTypeChooser = controller.getImageTypeChooser();
        imageBox = controller.getDragDropController().getImageBox();
        label = controller.getDragDropController().getLabel();
    }

    /* File Menu */
    @FXML
    private void load(ActionEvent event) {
        // Create filechooser for browsing image path
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a picture");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files",
                "*.jpg", "*.jpeg", "*.bmp", "*.gif", "*.png", "*.tiff"));

        File file = chooser.showOpenDialog(new Stage());

        if (file != null) {
            try {
                // Set model (image)
                context.getImage().setModel(new Image(new FileInputStream(file)), Type.RGB);

                // Set image types if not already set
                if(context.getImage().getTypeList().isEmpty()) {
                    context.getImage().getTypeList().addAll(context.getImage().getImageTypes());
                }

                // Select RGB
                imageTypeChooser.getSelectionModel().selectFirst();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Remove label if not already remove (only first image)
        if (imageBox.getChildren().contains(label)) {
            imageBox.getChildren().remove(label);
        }
    }

    // Clear the graph
    @FXML private void clear(ActionEvent event) {
        context.getImage().getSeries().clear();
    }

    // Reset the image
    @FXML private void reset(ActionEvent event) {
        imageTypeChooser.getSelectionModel().selectFirst();
    }

    // Close the program
    @FXML private void close(ActionEvent event) {
        System.exit(0);
    }


    /* Image menu */
    @FXML private void encode(ActionEvent event) {
        // Check if not binary, then convert
        if(context.getImage().getImageType() != Type.BINARY) {
            imageTypeChooser.getSelectionModel().selectLast();
        }

        // Create filechooser
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save as");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Encoding file", "*.txt"));

        // Ask for savepath
        File file = chooser.showSaveDialog(new Stage());
        if(file != null) {
            // Encode image to file
            Encode encode = new RunLength();
            encode.encode(context.getImage().getProcessedImage(), file);
        }
    }

    @FXML private void decode(ActionEvent event) {
        // Image
        Image image =  null;

        // Config file chooser
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open file");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Encoding file", "*.txt"));

        // Open and read file
        File file = chooser.showOpenDialog(new Stage());
        if(file != null) {
            Decode decode = new RunLength();
            image = decode.decode(file);
        }

        // Set model
        if(image != null) {
            context.getImage().setModel(image, Type.BINARY);
        }

        // Set view only for binary
        imageTypeChooser.getItems().remove(0, imageTypeChooser.getItems().size() - 1);
    }

    @FXML
    public void save(ActionEvent e) {
        File file = new File("output.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(context.getImage().getProcessedImage(), null), "png", file);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}