package sprint3.product.GUI;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
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


    public PlayerPanel(double playerPaneSize, Color playerColor, Color oppColor, Player player) {
        this.width = playerPaneSize;
        this.height = playerPaneSize*3;
        this.playerColor = playerColor;
        this.oppColor = oppColor;
        this.player = player;
        this.iconSize = this.width / 3;
        this.gamePieceSize = this.width/10;

        addIcon(player.isCPU());
        drawPieceQueue();
        drawCaptureSpace();

        this.setStyle("-fx-background-color: lightgrey;");

        this.setPrefSize(this.width, this.height);
    }

    private void addIcon(boolean isCPU) {
        SVGPath icon = new SVGPath();
        if (isCPU)
            icon.setContent("M1.5 0A1.5 1.5 0 0 0 0 1.5v7A1.5 1.5 0 0 0 1.5 10H6v1H1a1 1 0 0 0-1 1v3a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1v-3a1 1 0 0 0-1-1h-5v-1h4.5A1.5 1.5 0 0 0 16 8.5v-7A1.5 1.5 0 0 0 14.5 0zm0 1h13a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-.5.5h-13a.5.5 0 0 1-.5-.5v-7a.5.5 0 0 1 .5-.5M12 12.5a.5.5 0 1 1 1 0 .5.5 0 0 1-1 0m2 0a.5.5 0 1 1 1 0 .5.5 0 0 1-1 0M1.5 12h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1 0-1M1 14.25a.25.25 0 0 1 .25-.25h5.5a.25.25 0 1 1 0 .5h-5.5a.25.25 0 0 1-.25-.25");
        else
            icon.setContent("M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6");

        double iconStartingSize = icon.getBoundsInLocal().getHeight();
        icon.setScaleX(this.iconSize / iconStartingSize); // Assuming the original icon is 24x24
        icon.setScaleY(this.iconSize / iconStartingSize); // Assuming the original icon is 24x24

        // Set the icon's fill color
        icon.setFill(playerColor);
        System.out.println();

        // Position the icon at the top center
        icon.setLayoutX((this.width - iconStartingSize) / 2); // Center horizontally
        icon.setLayoutY(this.iconSize/2); // Position at the top (with a small offset if needed)

        // Set the icon's fill color (optional)
        icon.setFill(playerColor);
        getChildren().add(icon);
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

        javafx.application.Platform.runLater(() -> {
            double pieceQueueWidth = this.pieceQueue.getWidth();

            this.pieceQueue.setLayoutX(((this.width - pieceQueueWidth) / 2)+pieceShift);
            this.pieceQueue.setLayoutY(this.iconSize*1.5);
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
}
