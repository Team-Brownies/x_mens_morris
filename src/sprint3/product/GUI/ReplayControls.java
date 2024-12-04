package sprint3.product.GUI;

import com.google.gson.JsonArray;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import sprint3.product.Game.Game;
import sprint3.product.Player.ScriptedPlayer;

public class ReplayControls extends VBox {
        private Slider seekSlider;

        public ReplayControls(JsonArray log, double size, Board board) {
            Game game = board.getGame();
            ScriptedPlayer red = (ScriptedPlayer) game.getRedPlayer();
            ScriptedPlayer blue = (ScriptedPlayer) game.getBluePlayer();
            red.setPlayTo(log.size());
            blue.setPlayTo(log.size());

            seekSlider = new Slider();
            seekSlider.setMin(0);
            seekSlider.setMax(log.size());
            seekSlider.setDisable(true);
            seekSlider.setStyle("-fx-opacity: 1.0;");

            Button playButton = new Button("Pause");
            Button nextButton = new Button("Next Step");

            playButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Arial';");
            nextButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Arial';");

            playButton.setOnAction(e -> {
                boolean isPlaying = red.isPlaying();

                playButton.setText(isPlaying ? "Play" : "Pause");
                red.setPlaying(!isPlaying);
                blue.setPlaying(!isPlaying);
                game.letCPUMove();
            });

            nextButton.setOnAction(e -> {
                boolean isAnimateRunning = board.isRunningAnimation();
                if (!red.isPlaying()&&!isAnimateRunning) {
                    red.setPlaying(true);
                    blue.setPlaying(true);
                    game.letCPUMove();
                }
                red.setPlaying(false);
                blue.setPlaying(false);
            });

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox buttonBox = new HBox(20, playButton, spacer, nextButton);
            getChildren().addAll(seekSlider, buttonBox);
            this.setMinWidth(size);
            this.setSpacing(20);
        }

    public void setSeek(int turnNumber) {
            seekSlider.setValue(turnNumber);
    }
}