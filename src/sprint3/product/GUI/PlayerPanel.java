package sprint3.product.GUI;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import sprint3.product.Player.Player;

import java.util.Random;

public class PlayerPanel extends Pane {
    private final double WIDTH;
    private final double HEIGHT;
    private final double ICON_SIZE;
    private final double GAMEPIECE_SIZE;
    private GridPane pieceQueue = new GridPane();
    private Pane captureSpace = new Pane();
    private Color playerColor;
    private Color oppColor;
    private Player player;
    private PlayerType playerType;
    private DropShadow turnGlow;
    private Pane glowPane = new Pane();
    private String playerLabel;

    public PlayerPanel(double playerPaneSize, Color playerColor, Color oppColor, Player player, PlayerType playerType) {
        this.WIDTH = playerPaneSize;
        this.HEIGHT = playerPaneSize*3;
        this.playerColor = playerColor;
        this.oppColor = oppColor;
        this.player = player;
        this.ICON_SIZE = this.WIDTH / 3;
        this.GAMEPIECE_SIZE = this.WIDTH /10;
        this.playerType = playerType;

        setMouseTransparent(true);

        addIcon();
        playerStatus();
        drawPieceQueue();
        drawCaptureSpace();
        addGlow();

        this.setStyle("-fx-background-color: lightgrey;");

        this.setPrefSize(this.WIDTH, this.HEIGHT);
    }

    private void addGlow(){
        glowPane.setStyle("-fx-background-color: lightgrey;");
        glowPane.setPrefSize(this.WIDTH, this.HEIGHT);

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
        if (isVisible) {
            turnGlow.setColor(playerColor.brighter());
        }
        else {
            turnGlow.setColor(Color.TRANSPARENT);
        }
    }

    private void addIcon() {
        SVGPath icon = new SVGPath();
        double sizeMult = 1;
        switch (playerType){
            case HUMAN:
                icon.setContent("M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6");
                this.playerLabel = "Human";
                break;
            case CPU:
                icon.setContent("M1.5 0A1.5 1.5 0 0 0 0 1.5v7A1.5 1.5 0 0 0 1.5 10H6v1H1a1 1 0 0 0-1 1v3a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1v-3a1 1 0 0 0-1-1h-5v-1h4.5A1.5 1.5 0 0 0 16 8.5v-7A1.5 1.5 0 0 0 14.5 0zm0 1h13a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-.5.5h-13a.5.5 0 0 1-.5-.5v-7a.5.5 0 0 1 .5-.5M12 12.5a.5.5 0 1 1 1 0 .5.5 0 0 1-1 0m2 0a.5.5 0 1 1 1 0 .5.5 0 0 1-1 0M1.5 12h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1 0-1M1 14.25a.25.25 0 0 1 .25-.25h5.5a.25.25 0 1 1 0 .5h-5.5a.25.25 0 0 1-.25-.25");
                this.playerLabel = "CPU";
                break;
            case SCRIPTED:
                icon.setContent("M4 0h5.293A1 1 0 0 1 10 .293L13.707 4a1 1 0 0 1 .293.707V14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2m5.5 1.5v2a1 1 0 0 0 1 1h2z");
                this.playerLabel = "Replay";
                sizeMult=.75;
                break;
        }

        double iconStartingSize = icon.getBoundsInLocal().getWidth();
        icon.setScaleX((this.ICON_SIZE / iconStartingSize)*sizeMult);
        icon.setScaleY((this.ICON_SIZE / iconStartingSize)*sizeMult);

        icon.setFill(playerColor);

        icon.setLayoutX((this.WIDTH - iconStartingSize) / 2);
        icon.setLayoutY(this.ICON_SIZE / 2);

        icon.setFill(playerColor);
        getChildren().add(icon);
    }
    private String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
    private void playerStatus(){
        Label gameStatus = new Label("");
        gameStatus.setText(playerLabel+"\nPlayer");
        gameStatus.setLayoutY(this.ICON_SIZE + 10);
        gameStatus.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-alignment: center;" +
                "-fx-alignment: center;" +
                "-fx-text-fill: " +
                toRGBCode(playerColor.darker()) + ";");

        gameStatus.setMinWidth(this.WIDTH);
        getChildren().add(gameStatus);
    }
    private Circle drawGamePiece(Color color) {
        Circle gamePiece = new Circle(GAMEPIECE_SIZE, color);
        gamePiece.setStroke(color.darker());
        gamePiece.setStrokeWidth(2.0);
        return gamePiece;
    }
    private void drawPieceQueue(){
        int numberOfGamePieces = player.numberOfGamePieces();
        int col = 3;
        int row = (int) Math.ceil((double) numberOfGamePieces / col);
        int pieceShift = 10;

        getChildren().add(this.pieceQueue);

        int pieceLeft = 0;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (pieceLeft < numberOfGamePieces) {
                    Circle gp = drawGamePiece(this.playerColor);
                    gp.setTranslateX(-(i * pieceShift));
                    gp.setTranslateY(-(j * (pieceShift / 1.5)));
                    this.pieceQueue.add(gp, i, j);
                    pieceLeft++;
                }
            }
        }
        this.pieceQueue.requestLayout();

        Platform.runLater(() -> {
            double pieceQueueWidth = this.pieceQueue.getWidth();

            this.pieceQueue.setLayoutX(((this.WIDTH - pieceQueueWidth) / 2)+pieceShift);
            this.pieceQueue.setLayoutY(this.ICON_SIZE *2);
        });
    }

    private void drawCaptureSpace(){
        double height = this.HEIGHT / 3;
        this.captureSpace.setPrefSize(this.WIDTH, height);

        this.captureSpace.setLayoutX(0);

        this.captureSpace.setLayoutY(this.HEIGHT - height);

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
        int min = (int) (GAMEPIECE_SIZE *3);
        int max = (int) (this.WIDTH -min);

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
        pane.setPrefSize(this.WIDTH, this.HEIGHT);
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