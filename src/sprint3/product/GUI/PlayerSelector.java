package sprint3.product.GUI;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;

public class PlayerSelector extends StackPane {
    private final BooleanProperty SWITCHED_ON = new SimpleBooleanProperty(false);
    private final Button HUMAN_RADIO = new Button("Human");
    private final Button CPU_RADIO = new Button("CPU");
    private final Slider DIFFICULTY = new Slider(1, 3, 3);
    private int difficultyValue;


    public PlayerSelector(char number) {
        Label playerLabel = new Label("Player " + number);
        this.difficultyValue = 3;
        playerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black; -fx-padding: 5px;");

        setOn(HUMAN_RADIO);
        setOff(CPU_RADIO);

        HUMAN_RADIO.setPrefSize(100, 35);
        CPU_RADIO.setPrefSize(100, 35);

        HUMAN_RADIO.setOnAction(_ -> switchValues(false));
        CPU_RADIO.setOnAction(_ -> switchValues(true));

        DIFFICULTY.setShowTickMarks(true);
        DIFFICULTY.setShowTickLabels(true);
        DIFFICULTY.setMajorTickUnit(1);
        DIFFICULTY.setMinorTickCount(0);
        DIFFICULTY.setBlockIncrement(1);
        DIFFICULTY.setMaxWidth(125);

        DIFFICULTY.setVisible(false);


        HUMAN_RADIO.setText("Human");
        CPU_RADIO.setText("CPU");

        getChildren().addAll(playerLabel, HUMAN_RADIO, CPU_RADIO, DIFFICULTY);

        StackPane.setAlignment(playerLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(HUMAN_RADIO, Pos.CENTER_LEFT);
        StackPane.setAlignment(CPU_RADIO, Pos.CENTER_RIGHT);
        StackPane.setAlignment(DIFFICULTY, Pos.BOTTOM_CENTER);

        StackPane.setMargin(playerLabel, new javafx.geometry.Insets(0, 70, 75, 70));

        DIFFICULTY.valueProperty().addListener((_, _, newValue) -> {
            double value = (double) newValue;
            int roundedValue = (int) Math.round(value);
            DIFFICULTY.setValue(roundedValue);
            this.difficultyValue = roundedValue;
        });

        DIFFICULTY.setLabelFormatter(new javafx.util.StringConverter<>() {
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
            setOn(CPU_RADIO);
            setOff(HUMAN_RADIO);
            DIFFICULTY.setVisible(true);
        } else {
            setOn(HUMAN_RADIO);
            setOff(CPU_RADIO);
            DIFFICULTY.setVisible(false);
        }

        setSWITCHED_ON(value);
    }

    private void setOn(Button button) {
        button.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: black; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-border-color: white; -fx-border-width: 2px;");
    }

    private void setOff(Button button) {
        button.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: white; -fx-text-fill: black; -fx-padding: 10px 20px; -fx-border-color: black; -fx-border-width: 2px;");
    }

    public boolean getSWITCHED_ON() {
        return SWITCHED_ON.get();
    }

    public void setSWITCHED_ON(boolean value) {
        SWITCHED_ON.set(value);
    }

    public int getDifficultyValue() {
        return difficultyValue;
    }
}