package sprint3.product.GUI;

import javafx.animation.*;
import javafx.geometry.Point3D;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import sprint3.product.Cell;
import sprint3.product.Game.Game;
import sprint3.product.Game.GameState;
import sprint3.product.GamePiece;
import sprint3.product.Player.Player;

import java.util.Arrays;
import java.util.Random;

public class GameSpace extends Pane {
    private final Board board;
    private final Game game;
    private final int row, col;
    private final Circle gamePiece = new Circle();
    private final Circle point = new Circle();
    private char color;

    public GameSpace(int row, int col, boolean valid, Board board) {
        this.row = row;
        this.col = col;
        this.game = board.getGame();
        this.board = board;

        this.setPrefSize(2000, 2000);
        if (valid){
            drawPoint();
            this.setOnMouseClicked(_ -> handleMouseClick());
            getChildren().add(new Label(row+", "+col));
        } else {
            drawLine();
        }
    }

    public Circle getGamePiece() {
        return gamePiece;
    }

    private void drawPoint() {
        this.point.centerXProperty().bind(this.widthProperty().divide(2));
        this.point.centerYProperty().bind(this.heightProperty().divide(2));
        this.point.radiusProperty().bind(this.widthProperty().divide(8));
        this.point.setFill(Color.BLACK);
        this.point.setStrokeWidth(2);

        if (validLine(this.col, this.row, -1))
            drawPointLine(0.5, 0.5, 1.0, 0.5);  // Right Line
        if (validLine(this.row, this.col, -1))
            drawPointLine(0.5, 0.5, 0.5, 1.0);  // Down Line
        if (validLine(this.col, this.row, 1))
            drawPointLine(0.0, 0.5, 0.5, 0.5);  // Left Line
        if (validLine(this.row, this.col, 1))
            drawPointLine(0.5, 0.0, 0.5, 0.5);  // Up Line
        getChildren().add(this.point);
        drawGamePiece();
    }
    private boolean validLine(int x, int y, int d ){
        int start, end, center;
        int max = board.getGameSize()-1;
        int middle = max/2;

        if (d==1){
            start = middle;
            end = 0;
            center = max;
        } else {
            start = 0;
            end = max;
            center = middle;
        }

        return ((x>=start&&x<=center)
                &&!(x==middle+d&&y==middle)
                ||(y==middle&&!(x==end||x==middle+d)));
    }

    private void drawPointLine(double startX, double startY, double endX, double endY) {
        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(5.0);
        line.startXProperty().bind(this.widthProperty().multiply(startX));
        line.startYProperty().bind(this.heightProperty().multiply(startY));
        line.endXProperty().bind(this.widthProperty().multiply(endX));
        line.endYProperty().bind(this.heightProperty().multiply(endY));
        getChildren().add(line);
    }

    private void drawLine() {
        int max = board.getGameSize()-1;
        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(5.0);
        if (this.row == 0 || this.row == max || (this.row == 1 && (this.col>0 && this.col<max))
                || (this.row == max-1 && (this.col > 0 && this.col< max))) {
            line.startYProperty().bind(this.heightProperty().divide(2));
            line.endXProperty().bind(this.widthProperty());
            line.endYProperty().bind(this.heightProperty().divide(2));
        }
        else if (this.row==this.col){
            line.setStroke(Color.TRANSPARENT);
        }
        else {
            line.startXProperty().bind(this.widthProperty().divide(2));
            line.endXProperty().bind(this.widthProperty().divide(2));
            line.endYProperty().bind(this.heightProperty());
        }
        getChildren().add(line);
    }

    private void drawGamePiece() {
        this.gamePiece.centerXProperty().bind(this.widthProperty().divide(2));
        this.gamePiece.centerYProperty().bind(this.heightProperty().divide(2));
        this.gamePiece.radiusProperty().bind(this.widthProperty().divide(4));
        this.gamePiece.setStrokeWidth(2);
        this.gamePiece.setFill(Color.TRANSPARENT);

        getChildren().add(this.gamePiece);
    }
    private Circle drawAnimateGamePiece(GameSpace gp, Color color) {
        Circle animateGP = new Circle();

        animateGP.centerXProperty().bind(this.widthProperty().divide(2));
        animateGP.centerYProperty().bind(this.heightProperty().divide(2));
        animateGP.radiusProperty().bind(this.widthProperty().divide(4));
        animateGP.setStrokeWidth(2);

        gp.getChildren().add(animateGP);
        animateGP.setFill(color);
        gp.toFront();
        animateGP.toFront();

        animateGP.setVisible(false);

        return animateGP;
    }

    private void highlightCells(){
        GameSpace movingGP = this.board.getMovingGamePiece();
        for (int row = 0; row < board.getGameSize(); row++)
            for (int col = 0; col < board.getGameSize(); col++)
                if (game.getCell(row, col) == game.movingOrFlying() && movingGP != null)
                    this.point.setStroke(Color.GREEN);
                else
                    this.point.setStroke(Color.TRANSPARENT);
    }

    private void handleMouseClick() {
        Player turnPlayer = game.getTurnPlayer();
        GameState gameState = turnPlayer.getPlayersGamestate();
        if(!turnPlayer.isCPU()) {
            System.out.println(Arrays.toString(new int[]{this.row, this.col}));
            System.out.println(game.getCell(this.row,this.col));
            switch (gameState) {
                case PLACING:
                    if(game.canPlacePiece(this.row, this.col))
                        turnPlayer.placePiece(this.row, this.col);
                    break;
                case MOVING,FLYING:
                    handleMovingFlying(turnPlayer);
                    break;
                case MILLING:
                    turnPlayer.removePiece(this.row, this.col);
                    break;
            }
        }
    }
    private void handleMovingFlying(Player turnPlayer) {
        GameSpace movingGP = board.getMovingGamePiece();
        GamePiece piece = turnPlayer.getGamePieceByLocation(this.row, this.col);
        if (this.color == turnPlayer.getColor()) {

            if (movingGP!=null) {
                movingGP.gamePiece.setStroke(Color.TRANSPARENT);
                game.clearHighlightCells();
            }
            board.setMovingGamePiece(this);

            this.gamePiece.setStroke(Color.GREEN);
            if (turnPlayer.getPlayersGamestate() == GameState.MOVING )
                piece.updateValidMovesLocations();
            piece.printValidMoves();
            highlightCells();
        } else if (game.getCell(this.row, this.col) == game.movingOrFlying() && movingGP != null) {
            turnPlayer.movePiece(this.row, this.col, movingGP.row, movingGP.col);
            board.setMovingGamePiece(null);
        }
        highlightCells();
    }

    public void animatePlacePiece(Runnable onFinished) {
        Color color = (game.getTurnPlayer().getColor()=='R') ? board.getRed() : board.getBlue();
        Circle animateGP = drawAnimateGamePiece(this,color);
        // Get Random Axis to rotate on for flip
        Point3D[] axis = new Point3D[]{Rotate.X_AXIS,Rotate.Y_AXIS};

        this.gamePiece.setVisible(false);

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(0));
        pauseTransition.setOnFinished(_ -> animateGP.setVisible(true));

        // Create a moving transition to simulate a gamePiece falling down
        TranslateTransition moveTransition = new TranslateTransition(Duration.millis(500), animateGP);
        moveTransition.setFromY(-200);
        moveTransition.setToY(0);
        moveTransition.setInterpolator(javafx.animation.Interpolator.EASE_IN);

        // Create a fall (scale) transition a gamePiece shrinking as it falls
        ScaleTransition fallTransition = new ScaleTransition(Duration.millis(500), animateGP);
        fallTransition.setFromY(2.0);
        fallTransition.setFromX(2.0);
        fallTransition.setToY(1.0);
        fallTransition.setToX(1.0);
        fallTransition.setInterpolator(Interpolator.EASE_IN);

        // Create a flipping transition
        RotateTransition flipTransition = new RotateTransition(Duration.millis(500), animateGP);
        // Rotate around the X-axis or Y-axis
        flipTransition.setAxis(axis[getRandom(axis.length, 0,false)]);
        flipTransition.setFromAngle(0);
        flipTransition.setToAngle(360);  // Full rotation for a coin flip effect
        flipTransition.setInterpolator(Interpolator.EASE_BOTH);

        // Combine both the movement, fall and flip sequence into a parallel transition
        ParallelTransition parallelTransition = new ParallelTransition(moveTransition, fallTransition, flipTransition);

        // Add pause and parallel transitions into a sequential transition to start with a pause
        SequentialTransition finalTransition = new SequentialTransition(pauseTransition, parallelTransition);

        finalTransition.play();

        finalTransition.setOnFinished(_ -> {
            if (onFinished != null) {
                onFinished.run();
            }
            this.gamePiece.setVisible(true);
            board.updateGameStatus();
            getChildren().remove(animateGP);
        });
    }

    public void animateMovePiece(Runnable onFinished, GameSpace movingGP) {
        Color color = (game.getTurnPlayer().getColor()=='R') ? board.getRed() : board.getBlue();
        Circle animateGP = drawAnimateGamePiece(movingGP,color);
        int rowDiff = this.row-movingGP.row;
        int colDiff = this.col-movingGP.col;

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(0));
        pauseTransition.setOnFinished(_ ->{
            animateGP.setVisible(true);
            updateCell();
        });

        TranslateTransition transition = new TranslateTransition();
        transition.setNode(animateGP);
        transition.setToX(animateGP.getCenterX()*colDiff*2);
        transition.setToY(animateGP.getCenterY()*rowDiff*2);
        transition.setInterpolator(Interpolator.LINEAR);

        SequentialTransition sequentialTransition = new SequentialTransition(pauseTransition, transition);
        sequentialTransition.play();


        animateGP.setTranslateX(0);
        animateGP.setTranslateY(0);

        transition.setOnFinished(_ -> {
            if (onFinished != null) {
                onFinished.run();
            }
            board.updateGameStatus();
            movingGP.getChildren().remove(animateGP);
        });

    }

    public void animateRemovePiece(Runnable onFinished) {
        Color color = (game.getOpponentPlayer().getColor()=='R') ? board.getRed() : board.getBlue();
        Circle animateGP = drawAnimateGamePiece(this,color);
        int randomNumber;

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(0));
        pauseTransition.setOnFinished(_ ->{
            animateGP.setVisible(true);
            updateCell();
        });

        TranslateTransition flingTransition = new TranslateTransition(Duration.millis(500), animateGP);

        randomNumber = getRandom(1000, 200, true);
        flingTransition.setToX(randomNumber);
        randomNumber = getRandom(1000, -200, true);
        flingTransition.setToY(randomNumber);

        flingTransition.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        SequentialTransition sequentialTransition = new SequentialTransition(pauseTransition, flingTransition);
        sequentialTransition.play();

        flingTransition.setOnFinished(_ -> {
            if (onFinished != null) {
                onFinished.run();
            }
            //updateMill();
            board.updateGameStatus();
            getChildren().remove(animateGP);
        });

    }
    public void updateCell(){
        Cell cell = game.getCell(row, col);
        char color;

        if (cell == Cell.RED) {
            gamePiece.setFill(board.getRed());
            color = 'R';
        }
        else if (cell == Cell.BLUE)  {
            gamePiece.setFill(board.getBlue());
            color = 'B';
        } else {
            gamePiece.setFill(Color.TRANSPARENT);
            gamePiece.setStroke(Color.TRANSPARENT);
            color = ' ';
        }
        this.color = color;
    }
    private int getRandom(int bound, int min, boolean allowNegative){
        Random r = new Random();
        boolean isNegative = r.nextBoolean();
        int randomNumber = r.nextInt(bound) + min;

        return (allowNegative && isNegative) ? -randomNumber : randomNumber;
    }
}
