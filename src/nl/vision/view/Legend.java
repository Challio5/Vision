package nl.vision.view;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import nl.vision.model.Shape;

/**
 * Created by rob on 24-10-15.
 */
public class Legend extends HBox {
    private ObservableMap<Color, Shape> map;

    public Legend() {
        this.map = FXCollections.observableHashMap();
    }

    public void clearMap() {
        map.clear();
    }

    public void setMap(ObservableMap<Color, Shape> map) {
        this.map = map;
        this.map.addListener((MapChangeListener<Color, Shape>) change -> {
            if(change.wasAdded()) {
                Color color = change.getKey();
                Shape shape = change.getValueAdded();

                LegendItem item = new LegendItem(color, shape);
                this.getChildren().add(item);
            }
        });
    }
}
