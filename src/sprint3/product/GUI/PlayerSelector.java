package sprint3.product.GUI;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;

public class PlayerSelector extends StackPane {
    private final BooleanProperty switchedOn = new SimpleBooleanProperty(false);
    private final Button humanRadio = new Button("Human");
    private final Button cpuRadio = new Button("CPU");
    private final Slider difficulty = new Slider(1, 3, 3);
    private int difficultyValue;


    public PlayerSelector(char number) {
        Label playerLabel = new Label("Player " + number);
        this.difficultyValue = 3;
        playerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black; -fx-padding: 5px;");

        setOn(humanRadio);
        setOff(cpuRadio);

        humanRadio.setPrefSize(100, 35);
        cpuRadio.setPrefSize(100, 35);

        humanRadio.setOnAction(_ -> switchValues(false));
        cpuRadio.setOnAction(_ -> switchValues(true));

        difficulty.setShowTickMarks(true);
        difficulty.setShowTickLabels(true);
        difficulty.setMajorTickUnit(1);
        difficulty.setMinorTickCount(0);
        difficulty.setBlockIncrement(1);
        difficulty.setMaxWidth(125);

        difficulty.setVisible(false);


        humanRadio.setText("Human");
        cpuRadio.setText("CPU");

        getChildren().addAll(playerLabel, humanRadio, cpuRadio, difficulty);

        StackPane.setAlignment(playerLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(humanRadio, Pos.CENTER_LEFT);
        StackPane.setAlignment(cpuRadio, Pos.CENTER_RIGHT);
        StackPane.setAlignment(difficulty, Pos.BOTTOM_CENTER);

        StackPane.setMargin(playerLabel, new javafx.geometry.Insets(0, 70, 75, 70));

        difficulty.valueProperty().addListener((_, _, newValue) -> {
            double value = (double) newValue;
            int roundedValue = (int) Math.round(value);
            difficulty.setValue(roundedValue);
            this.difficultyValue = roundedValue;
        });

        difficulty.setLabelFormatter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Double value) {
                if (value == 1) return "EASY";
                if (value == 2) return "NORMAL";
                if (value == 3) return "HARD";
                return null;
            }

            @Override
            public Double fromString(String string) {
                return switch (string) {
                    case "EASY" -> 1.0;
                    case "NORMAL" -> 2.0;
                    default -> 3.0;
                };
            }
        });
    }

    private void switchValues(Boolean value) {
        if (value) {
            setOn(cpuRadio);
            setOff(humanRadio);
            difficulty.setVisible(true);
        } else {
            setOn(humanRadio);
            setOff(cpuRadio);
            difficulty.setVisible(false);
        }

        setSwitchedOn(value);
    }

    private void setOn(Button button) {
        button.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: black; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-border-color: white; -fx-border-width: 2px;");

    }

    private void setOff(Button button) {
        button.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: white; -fx-text-fill: black; -fx-padding: 10px 20px; -fx-border-color: black; -fx-border-width: 2px;");
    }

    public boolean isSwitchedOn() {
        return switchedOn.get();
    }

    public void setSwitchedOn(boolean value) {
        switchedOn.set(value);
    }

    public int getDifficultyValue() {
        return difficultyValue;
    }
}