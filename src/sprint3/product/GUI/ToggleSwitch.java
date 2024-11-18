package sprint3.product.GUI;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.input.MouseEvent;

public class ToggleSwitch extends StackPane {
    private final BooleanProperty switchedOn = new SimpleBooleanProperty(false);
    private final SVGPath icon = new SVGPath();
    private final Color color;

    public ToggleSwitch(Color color) {
        this.color = color;
        // Set default icon to "off"
        setIconOff();

        // Set default size
        setPrefSize(100, 50); // Increase this for larger toggle
        setStyle("-fx-background-color: transparent;");

        // Scale the icon itself (make it larger)
//        icon.setStyle("-fx-fill: black;");
        icon.setFill(color);
        icon.setScaleX(3);  // Scale horizontally (larger size)
        icon.setScaleY(3);  // Scale vertically (larger size)

        // Change icon based on the switchedOn state
        switchedOn.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setIconOn();
            } else {
                setIconOff();
            }
        });

        // Toggle behavior when clicked
        setOnMouseClicked(event -> switchedOn.set(!switchedOn.get()));

        // Add hover effect
        setOnMouseEntered(event -> setStyle("-fx-cursor: hand; -fx-background-color: lightgray;"));
        setOnMouseExited(event -> setStyle("-fx-cursor: hand; -fx-background-color: transparent;"));

        // Add pressed effect (optional visual feedback on click)
        setOnMousePressed(event -> icon.setFill(color.darker()));
        setOnMouseReleased(event -> icon.setFill(color));

        // Add SVG icon to StackPane
        getChildren().add(icon);
    }

    private void setIconOn() {
        icon.setContent("M1.5 0A1.5 1.5 0 0 0 0 1.5v7A1.5 1.5 0 0 0 1.5 10H6v1H1a1 1 0 0 0-1 1v3a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1v-3a1 1 0 0 0-1-1h-5v-1h4.5A1.5 1.5 0 0 0 16 8.5v-7A1.5 1.5 0 0 0 14.5 0zm0 1h13a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-.5.5h-13a.5.5 0 0 1-.5-.5v-7a.5.5 0 0 1 .5-.5M12 12.5a.5.5 0 1 1 1 0 .5.5 0 0 1-1 0m2 0a.5.5 0 1 1 1 0 .5.5 0 0 1-1 0M1.5 12h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1 0-1M1 14.25a.25.25 0 0 1 .25-.25h5.5a.25.25 0 1 1 0 .5h-5.5a.25.25 0 0 1-.25-.25");
    }

    private void setIconOff() {
        icon.setContent("M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6");
    }

    public BooleanProperty switchedOnProperty() {
        return switchedOn;
    }

    public boolean isSwitchedOn() {
        return switchedOn.get();
    }

    public void setSwitchedOn(boolean value) {
        switchedOn.set(value);
    }
}