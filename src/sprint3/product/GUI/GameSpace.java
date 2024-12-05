package sprint3.product.GUI;

import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
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

import java.util.Random;

public class GameSpace extends Pane {
    private final Board BOARD;
    private final Game GAME;
    private final int ROW, COL;
    private final Circle POINT = new Circle();
    private Circle gamePiece = new Circle();
    private char color;

    // a gameSpace used for each tile for the game board
    public GameSpace(int row, int col, boolean valid, Board board) {
        this.ROW = row;
        this.COL = col;
        this.GAME = board.getGame();
        this.BOARD = board;

        this.setPrefSize(2000, 2000);
        if (valid){
            drawPoint();
            this.setOnMouseClicked(_ -> handleMouseClick());
            getChildren().add(new Label(row+", "+col));
        } else {
            drawLine();
        }
    }

    // draw a point on the gameSpace to show the play a piece can be placed here
    private void drawPoint() {
        this.POINT.centerXProperty().bind(this.widthProperty().divide(2));
        this.POINT.centerYProperty().bind(this.heightProperty().divide(2));
        this.POINT.radiusProperty().bind(this.widthProperty().divide(8));
        this.POINT.setFill(Color.WHITE);
        this.POINT.setStroke(Color.BLACK);
        this.POINT.setStrokeWidth(2);

        if (validLine(this.COL, this.ROW, -1)) {
            drawPointLine(0.5, 0.5, 1.0, 0.5);  // Right Line
        }
        if (validLine(this.ROW, this.COL, -1)) {
            drawPointLine(0.5, 0.5, 0.5, 1.0);  // Down Line
        }
        if (validLine(this.COL, this.ROW, 1)) {
            drawPointLine(0.0, 0.5, 0.5, 0.5);  // Left Line
        }
        if (validLine(this.ROW, this.COL, 1)) {
            drawPointLine(0.5, 0.0, 0.5, 0.5);  // Up Line
        }
        getChildren().add(this.POINT);

        this.POINT.setEffect(addGlow());

        drawSpacesGamePiece();
    }

    private DropShadow addGlow() {
        DropShadow glow = new DropShadow();

        glow.setColor(Color.TRANSPARENT);

        glow.setRadius(30);
        glow.setSpread(0.5);
        glow.setOffsetX(0);
        glow.setOffsetY(0);

        return glow;
    }

    // find the directions a line need to be drawn to connect valid gameSpace together
    private boolean validLine(int x, int y, int d ){
        int start, end, center;
        int max = BOARD.getGameSize()-1;
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

    // draw a line on a point connecting valid gameSpace together
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

    // draws line on non point spaces connecting valid gameSpace together
    private void drawLine() {
        int max = BOARD.getGameSize()-1;
        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(5.0);
        if (this.ROW == 0 || this.ROW == max || (this.ROW == 1 && (this.COL >0 && this.COL <max))
                || (this.ROW == max-1 && (this.COL > 0 && this.COL < max))) {
            line.startYProperty().bind(this.heightProperty().divide(2));
            line.endXProperty().bind(this.widthProperty());
            line.endYProperty().bind(this.heightProperty().divide(2));
        }
        else if (this.ROW ==this.COL){
            line.setStroke(Color.TRANSPARENT);
        }
        else {
            line.startXProperty().bind(this.widthProperty().divide(2));
            line.endXProperty().bind(this.widthProperty().divide(2));
            line.endYProperty().bind(this.heightProperty());
        }
        getChildren().add(line);
    }

    // draws a game piece for the board space or to be used in an animation
    private Circle drawGamePiece() {
        Circle gp = new Circle();
        gp.centerXProperty().bind(this.widthProperty().divide(2));
        gp.centerYProperty().bind(this.heightProperty().divide(2));
        gp.radiusProperty().bind(this.widthProperty().divide(4));

        return gp;
    }
    // draws a gamePiece for each valid game space to use
    private void drawSpacesGamePiece() {
        this.gamePiece = drawGamePiece();
        this.gamePiece.setFill(Color.TRANSPARENT);

        getChildren().add(this.gamePiece);

        this.gamePiece.setEffect(addGlow());
    }

    // sets glow for point
    public void setPointGlow(Color c) {
        DropShadow glow = (DropShadow) POINT.getEffect();
        if (glow!=null) {
            glow.setColor(c);
        }
    }

    // return game piece that is on this game space
    public Circle getGamePiece() {
        return gamePiece;
    }

    // sets glow for game piece
    public void setGamePieceGlow(Color c) {
        DropShadow glow = (DropShadow) gamePiece.getEffect();
        if (glow!=null) {
            glow.setColor(c);
        }
    }

    // return game space's row
    public int getRow() {
        return ROW;
    }

    // return game space's col
    public int getCol() {
        return COL;
    }

    // draw a gamePiece to be used for animations
    private Circle drawAnimationGamePiece(GameSpace gp, Color color) {
        Circle animateGP = drawGamePiece();

        gp.getChildren().add(animateGP);
        animateGP.setFill(color);
        gp.toFront();
        animateGP.toFront();

        animateGP.setVisible(false);

        return animateGP;
    }

    // handle the users mouse inputs base on their game state
    private void handleMouseClick() {
        Player turnPlayer = GAME.getTurnPlayer();
        GameState gameState = turnPlayer.getPlayersGamestate();
        if(!turnPlayer.isCPU() && !BOARD.isRunningAnimation()) {
            switch (gameState) {
                case PLACING:
                    if(GAME.canPlacePiece(this.ROW, this.COL))
                        turnPlayer.placePiece(this.ROW, this.COL);
                    break;
                case MOVING,FLYING:
                    handleMovingFlying(turnPlayer);
                    break;
                case MILLING:
                    turnPlayer.removePiece(this.ROW, this.COL);
                    break;
            }
        }
    }

    // handle the users mouse inputs for the moving and flying game state
    private void handleMovingFlying(Player turnPlayer) {
        GameSpace movingGP = BOARD.getMovingGamePiece();
        GamePiece piece = turnPlayer.getGamePieceByLocation(this.ROW, this.COL);
        Color pieceColor = (Color) this.gamePiece.getFill();

        if (this.color == turnPlayer.getColor()) {

            if (movingGP!=null) {
                movingGP.setGamePieceGlow(Color.TRANSPARENT);
            }
            BOARD.setMovingGamePiece(this);

            this.setGamePieceGlow(pieceColor.brighter());
            if (turnPlayer.getPlayersGamestate() == GameState.MOVING ) {
                piece.updateValidMovesLocations();
            }

            BOARD.highlightCells();
        } else if (GAME.movingOrFlying(this.ROW, this.COL) && movingGP != null) {
            turnPlayer.movePiece(this.ROW, this.COL, movingGP.ROW, movingGP.COL);
            BOARD.setMovingGamePiece(null);
        }
    }

    private SequentialTransition animateQueuePlace(Circle animateGP) {
        double animationSpeed = BOARD.getAnimationSpeed();
        PlayerPanel turnPanel = BOARD.getTurnPlayerPanel();
        Circle panelGamePiece = turnPanel.getGamePieceFromQueue();

        animateGP.setLayoutY(-200);
        Bounds animateStartBounds = this.localToScene(animateGP.getBoundsInLocal());
        Bounds queuePieceBounds = panelGamePiece.localToScene(panelGamePiece.getBoundsInLocal());

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(0));
        pauseTransition.setOnFinished(_ -> {
            BOARD.setRunningAnimation(true);
        });

        // Translate `panelGamePiece` to match the animateGP start position
        TranslateTransition queueMoveTransition = new TranslateTransition(Duration.millis(500*animationSpeed), panelGamePiece);
        queueMoveTransition.setFromX(0);
        queueMoveTransition.setFromY(0);
        queueMoveTransition.setToX(animateStartBounds.getMinX() - queuePieceBounds.getMinX());
        queueMoveTransition.setToY(animateStartBounds.getMinY() - queuePieceBounds.getMinY()-200);
        queueMoveTransition.setInterpolator(Interpolator.EASE_BOTH);

        // grows the panelGamePiece to match animateGP
        ScaleTransition queueGrowTransition = new ScaleTransition(Duration.millis(500*animationSpeed), panelGamePiece);
        queueGrowTransition.setFromY(1.0);
        queueGrowTransition.setFromX(1.0);
        queueGrowTransition.setToY(2.0);
        queueGrowTransition.setToX(2.0);
        queueGrowTransition.setInterpolator(Interpolator.EASE_OUT);

        // Combine both the movement, fall and flip sequence into a parallel transition
        ParallelTransition queueParallelTransition = new ParallelTransition(queueMoveTransition, queueGrowTransition);
        SequentialTransition queueTransition = new SequentialTransition(pauseTransition,queueParallelTransition);

        queueTransition.setOnFinished(_ -> {
            panelGamePiece.setVisible(false);
            turnPanel.removeFromQueue();
            BOARD.setRunningAnimation(false);
        });
        return queueTransition;
    }

    // run animation for placing a piece
    public void animatePlacePiece(Runnable onFinished) {
        double animationSpeed = BOARD.getAnimationSpeed();
        Color color = (GAME.getTurnPlayer().getColor()=='R') ? BOARD.getRed() : BOARD.getBlue();
        Circle animateGP = drawAnimationGamePiece(this,color);
        // Get Random Axis to rotate on for flip
        Point3D[] axis = new Point3D[]{Rotate.X_AXIS,Rotate.Y_AXIS};

        this.gamePiece.setVisible(false);

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(0));
        pauseTransition.setOnFinished(_ -> {
            animateGP.setVisible(true);
            BOARD.setRunningAnimation(true);
        });

        SequentialTransition queueParallelTransition = animateQueuePlace(animateGP);

        // Create a moving transition to simulate a gamePiece falling down
        TranslateTransition moveTransition = new TranslateTransition(Duration.millis(500*animationSpeed), animateGP);
        moveTransition.setFromY(0);
        moveTransition.setToY(200);
        moveTransition.setInterpolator(javafx.animation.Interpolator.EASE_IN);

        // Create a fall (scale) transition a gamePiece shrinking as it falls
        ScaleTransition fallTransition = new ScaleTransition(Duration.millis(500*animationSpeed), animateGP);
        fallTransition.setFromY(2.0);
        fallTransition.setFromX(2.0);
        fallTransition.setToY(1.0);
        fallTransition.setToX(1.0);
        fallTransition.setInterpolator(Interpolator.EASE_IN);

        // Create a flipping transition
        RotateTransition flipTransition = new RotateTransition(Duration.millis(500*animationSpeed), animateGP);
        // Rotate around the X-axis or Y-axis
        flipTransition.setAxis(axis[getRandom(axis.length, 0,false)]);
        flipTransition.setFromAngle(0);
        flipTransition.setToAngle(360);  // Full rotation for a coin flip effect
        flipTransition.setInterpolator(Interpolator.EASE_BOTH);

        // Combine both the movement, fall and flip sequence into a parallel transition
        ParallelTransition parallelTransition = new ParallelTransition(moveTransition, fallTransition, flipTransition);

        // Add pause and parallel transitions into a sequential transition to start with a pause
        SequentialTransition finalTransition = new SequentialTransition(queueParallelTransition, pauseTransition, parallelTransition);

        finalTransition.play();

        finalTransition.setOnFinished(_ -> {
            if (onFinished != null) {
                onFinished.run();
            }
            this.gamePiece.setVisible(true);
            BOARD.updateGameStatus();
            getChildren().remove(animateGP);
            BOARD.setRunningAnimation(false);
        });
    }

    // run animation for moving a piece
    public void animateMovePiece(Runnable onFinished, GameSpace movingGP) {
        double animationSpeed = BOARD.getAnimationSpeed();
        Color color = (GAME.getTurnPlayer().getColor()=='R') ? BOARD.getRed() : BOARD.getBlue();
        Circle animateGP = drawAnimationGamePiece(movingGP,color);
        int rowDiff = this.ROW -movingGP.ROW;
        int colDiff = this.COL -movingGP.COL;

        BOARD.clearHighlights();
        movingGP.gamePiece.setVisible(false);

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(0));
        pauseTransition.setOnFinished(_ ->{
            animateGP.setVisible(true);
            updateCell();
            BOARD.setRunningAnimation(true);
        });

        TranslateTransition transition = new TranslateTransition(Duration.millis(250*animationSpeed));
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
            BOARD.updateGameStatus();
            movingGP.gamePiece.setVisible(true);
            this.gamePiece.setVisible(true);
            movingGP.getChildren().remove(animateGP);
            BOARD.setRunningAnimation(false);
        });

    }

    // run animation for removing a piece
    public void animateRemovePiece(Runnable onFinished) {
        double animationSpeed = BOARD.getAnimationSpeed();
        PlayerPanel turnPanel = BOARD.getTurnPlayerPanel();
        Circle captureGP = turnPanel.addToCaptureSpace();
        Color borderColor = (Color) captureGP.getStroke();

        Bounds gpStartBounds = this.localToScene(this.gamePiece.getBoundsInLocal());
        Bounds capturePieceBounds = captureGP.localToScene(captureGP.getBoundsInLocal());

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(0));
        pauseTransition.setOnFinished(_ ->{
            this.gamePiece.setVisible(false);
            captureGP.setStroke(Color.TRANSPARENT);
            BOARD.setRunningAnimation(true);
        });

        // Translate `panelGamePiece` to match the animateGP start position
        TranslateTransition captureMoveTransition = new TranslateTransition(Duration.millis(500*animationSpeed), captureGP);
        captureMoveTransition.setFromX(gpStartBounds.getMinX() - capturePieceBounds.getMinX());
        captureMoveTransition.setFromY(gpStartBounds.getMinY() - capturePieceBounds.getMinY());
        captureMoveTransition.setToX(0);
        captureMoveTransition.setToY(0);
        captureMoveTransition.setInterpolator(Interpolator.EASE_OUT);

        SequentialTransition sequentialTransition = new SequentialTransition(pauseTransition, captureMoveTransition);
        sequentialTransition.play();

        sequentialTransition.setOnFinished(_ -> {
            if (onFinished != null) {
                onFinished.run();
            }
            captureGP.setStroke(borderColor);
            BOARD.updateGameStatus();
            BOARD.setRunningAnimation(false);
        });
    }

    // update the game piece on this game space
    public void updateCell(){
        Cell cell = GAME.getCell(ROW, COL);

        char color;

        if (cell == Cell.RED) {
            gamePiece.setFill(BOARD.getRed());
            color = 'R';
        }
        else if (cell == Cell.BLUE)  {
            gamePiece.setFill(BOARD.getBlue());
            color = 'B';
        } else {
            gamePiece.setFill(Color.TRANSPARENT);
            this.setGamePieceGlow(Color.TRANSPARENT);
            color = ' ';
        }
        this.color = color;
    }

    // get random number for the animations
    private int getRandom(int bound, int min, boolean allowNegative){
        Random r = new Random();
        boolean isNegative = r.nextBoolean();
        int randomNumber = r.nextInt(bound) + min;

        return (allowNegative && isNegative) ? -randomNumber : randomNumber;
    }
}