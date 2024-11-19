package sprint3.product.GUI;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import sprint3.product.Game.GameState;
import sprint3.product.Player.Player;

import java.util.Random;

public class PlayerPanel extends Pane {
    private final double width;
    private final double height;
    private final double iconSize;
    private final double gamePieceSize;
    private GridPane pieceQueue = new GridPane();
    private Pane captureSpace = new Pane();
    private Color playerColor;
    private Color oppColor;
    private Player player;
    private boolean isCPU;
    private String playerType;
    private DropShadow turnGlow;
    private Pane glowPane = new Pane();

    public PlayerPanel(double playerPaneSize, Color playerColor, Color oppColor, Player player) {
        this.width = playerPaneSize;
        this.height = playerPaneSize*3;
        this.playerColor = playerColor;
        this.oppColor = oppColor;
        this.player = player;
        this.iconSize = this.width / 3;
        this.gamePieceSize = this.width/10;
        this.isCPU = player.isCPU();

        setMouseTransparent(true);

        addIcon();
        playerStatus();
        drawPieceQueue();
        drawCaptureSpace();
        addGlow();

        this.setStyle("-fx-background-color: lightgrey;");

        this.setPrefSize(this.width, this.height);
    }

    private void addGlow(){
        glowPane.setStyle("-fx-background-color: lightgrey;");
        glowPane.setPrefSize(this.width, this.height);

        glowPane.setLayoutX(0);

        glowPane.setLayoutY(0);

        getChildren().add(glowPane);
        glowPane.toBack();

        turnGlow = new DropShadow();
        turnGlow.setColor(playerColor.brighter());
        turnGlow.setRadius(60);
        turnGlow.setSpread(0.5);
        turnGlow.setOffsetX(0);
        turnGlow.setOffsetY(0);

        // Apply the glow turnGlow pane
        glowPane.setEffect(turnGlow);
    }
    public void setGlowVisible(boolean isVisible){
        if (isVisible)
            turnGlow.setColor(playerColor.brighter());
        else
            turnGlow.setColor(Color.TRANSPARENT);
    }

    private void addIcon() {
        SVGPath icon = new SVGPath();
        if (this.isCPU)
            icon.setContent("M1.5 0A1.5 1.5 0 0 0 0 1.5v7A1.5 1.5 0 0 0 1.5 10H6v1H1a1 1 0 0 0-1 1v3a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1v-3a1 1 0 0 0-1-1h-5v-1h4.5A1.5 1.5 0 0 0 16 8.5v-7A1.5 1.5 0 0 0 14.5 0zm0 1h13a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-.5.5h-13a.5.5 0 0 1-.5-.5v-7a.5.5 0 0 1 .5-.5M12 12.5a.5.5 0 1 1 1 0 .5.5 0 0 1-1 0m2 0a.5.5 0 1 1 1 0 .5.5 0 0 1-1 0M1.5 12h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1 0-1M1 14.25a.25.25 0 0 1 .25-.25h5.5a.25.25 0 1 1 0 .5h-5.5a.25.25 0 0 1-.25-.25");
        else
            icon.setContent("M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6");

        double iconStartingSize = icon.getBoundsInLocal().getWidth();
        icon.setScaleX(this.iconSize / iconStartingSize);
        icon.setScaleY(this.iconSize / iconStartingSize);

        // Set the icon's fill color
        icon.setFill(playerColor);

        // Position the icon at the top center
        double centerX = (this.width - this.iconSize) / 2;
        icon.setLayoutX((this.width - iconStartingSize) / 2);
        icon.setLayoutY(this.iconSize / 2);

        // Set the icon's fill color (optional)
        icon.setFill(playerColor);
        getChildren().add(icon);
    }
    private String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
//    void updatePlayerStatus(){
//        GameState state = this.player.getPlayersGamestate();
//        String stateText = String.valueOf(state);
//        if (state==GameState.GAMEOVER)
//            stateText = "";
//        gameStatus.setText(playerType+"\n"+stateText);
//        gameStatus.setLayoutX((this.width - gameStatus.getWidth()) / 2);
//    }
    private void playerStatus(){
        Label gameStatus = new Label("");
        // Add the Label below the icon
        playerType = this.isCPU ? "CPU" : "Human";
        gameStatus.setText(playerType+"\nPlayer");
        gameStatus.setLayoutY(this.iconSize + 10);
        gameStatus.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-alignment: center;" +
                "-fx-alignment: center;" +
                "-fx-text-fill: " +
                toRGBCode(playerColor.darker()) + ";");
//        updatePlayerStatus();

        gameStatus.setMinWidth(this.width);
        getChildren().add(gameStatus);
    }
    private Circle drawGamePiece(Color color) {
        Circle gamePiece = new Circle(gamePieceSize, color);
        gamePiece.setStroke(color.darker());
        gamePiece.setStrokeWidth(2.0);
        return gamePiece;
    }
    private void drawPieceQueue(){
        int pieceShift = 10;
//        double height = this.height / 3;
//        this.pieceQueue.setPrefSize(this.width, height);

        getChildren().add(this.pieceQueue);
//        this.pieceQueue.setStyle("-fx-background-color: black;");

        for (int i = 0; i < player.numberOfGamePieces() / 3; i++) {
            for (int j = 0; j < 3; j++) {
                Circle gp = drawGamePiece(this.playerColor);
                gp.setTranslateX(-(i * pieceShift));
                gp.setTranslateY(-(j * (pieceShift/1.5)));
                this.pieceQueue.add(gp, i, j);
            }
        }
        this.pieceQueue.requestLayout();
//        this.pieceQueue.setStyle("-fx-background-color: black;");

        Platform.runLater(() -> {
            double pieceQueueWidth = this.pieceQueue.getWidth();

            this.pieceQueue.setLayoutX(((this.width - pieceQueueWidth) / 2)+pieceShift);
            this.pieceQueue.setLayoutY(this.iconSize*2);
        });
    }

    private void drawCaptureSpace(){
        double height = this.height / 3;
        this.captureSpace.setPrefSize(this.width, height);

        this.captureSpace.setLayoutX(0);

        this.captureSpace.setLayoutY(this.height - height);

        getChildren().add(this.captureSpace);
    }

    public Circle getGamePieceFromQueue(){
        Circle c = (Circle) this.pieceQueue.getChildren().getLast();
        c.setStroke(playerColor);
        return c;
    }

    public void removeFromQueue(){
        this.pieceQueue.getChildren().removeLast();
    }

    public Circle addToCaptureSpace(){
        Circle gp = drawGamePiece(this.oppColor);
        int min = (int) (gamePieceSize*3);
        int max = (int) (this.width-min);

        gp.setLayoutX(randomDouble(min, max));
        gp.setLayoutY(randomDouble(min, max));
        this.captureSpace.getChildren().add(gp);
        return gp;
    }

    private double randomDouble(double min, double max){
        Random random = new Random();
        return min + (max - min) * random.nextDouble();
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public void winnerGlow(Color color) {
        Pane pane = new Pane();
        pane.setPrefSize(this.width, this.height);
        pane.setStyle("-fx-background-color: lightgrey;");
        getChildren().add(pane);
        pane.toBack();
        glowPane.toBack();
        setGlowVisible(true);
        turnGlow.setColor(color.brighter());
        turnGlow.setRadius(90);
        turnGlow.setSpread(0.3);
    }
    public Pane getGlow(){
        return this.glowPane;
    }
}
