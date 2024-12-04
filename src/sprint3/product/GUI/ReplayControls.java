package sprint3.product.GUI;

import com.google.gson.JsonArray;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sprint3.product.Game.Game;
import sprint3.product.Player.ScriptedPlayer;

public class ReplayControls extends VBox {
        private Slider seekSlider;
        private int turn;

        public ReplayControls(JsonArray log, Game game) {
            ScriptedPlayer red = (ScriptedPlayer) game.getRedPlayer();
            ScriptedPlayer blue = (ScriptedPlayer) game.getBluePlayer();
            seekSlider = new Slider();
            seekSlider.setMin(0);
            seekSlider.setMax(log.size());
            seekSlider.setBlockIncrement(1);
            seekSlider.setMajorTickUnit(10);
            seekSlider.setMinorTickCount(5);
            seekSlider.setShowTickMarks(true);
            seekSlider.setShowTickLabels(true);


            seekSlider.valueProperty().addListener((_, _, newValue) -> {
                double value = (double) newValue;
                int roundedValue = (int) Math.round(value);
//                seekSlider.setValue(roundedValue);
//
//                red.setPlayTo(roundedValue);
//                blue.setPlayTo(roundedValue);
//                game.letCPUMove();
            });

            Button playButton = new Button("Play");
            Button prevButton = new Button("Prev Step");
            Button nextButton = new Button("Next Step");

            playButton.setOnAction(e -> {
                red.togglePlaying();
                blue.togglePlaying();
                game.letCPUMove();
            });

            prevButton.setOnAction(e -> {
                seekSlider.setValue(seekSlider.getValue()-1);
//                red.setPlayTo(turn);
//                blue.setPlayTo(turn);
            });

            nextButton.setOnAction(e -> {
                seekSlider.setValue(seekSlider.getValue()+1);
//                red.setPlayTo(turn);
//                blue.setPlayTo(turn);
            });

            HBox buttonBox = new HBox(10, prevButton, playButton, nextButton);
            getChildren().addAll(seekSlider, buttonBox);
        }

    public void setSeek(int turnNumber) {
//            seekSlider.setValue(turnNumber);
    }
}

