package nl.vision.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.vision.model.Shape;

/**
 * Created by rob on 23-10-15.
 */
public class LegendItem extends HBox {
    private Rectangle color;
    private Label shape;

    public LegendItem(Color color, Shape shape) {
        this.setPadding(new Insets(10));

        this.color = new Rectangle(20, 20, color);

        switch (shape) {
            case CIRCLE:
                this.shape = new Label("Circle");
                break;
            case SQUARE:
                this.shape = new Label("Square");
                break;
            case TRIANGLE:
                this.shape = new Label("Triangle");
                break;
        }

        this.getChildren().add(this.color);
        this.getChildren().add(this.shape);
    }
}
