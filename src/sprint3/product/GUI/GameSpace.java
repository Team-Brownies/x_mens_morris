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
import sprint3.product.Game.GameHistory;
import sprint3.product.GamePiece;
import sprint3.product.Player.Player;

import java.util.Random;

public class GameSpace extends Pane {
    private final Board board;
    private final Game game;
    private final int row, col;
    private Circle gamePiece = new Circle();
    private final Circle point = new Circle();
    private char color;
    private boolean animateRunning;
    GameHistory gameHistory = new GameHistory(this);



    // a gameSpace used for each tile for the game board
    public GameSpace(int row, int col, boolean valid, Board board) {
        this.row = row;
        this.col = col;
        this.game = board.getGame();
        this.board = board;

        this.setPrefSize(2000, 2000);
        if (valid){
            drawPoint();
            this.setOnMouseClicked(_ -> handleMouseClick());
//            getChildren().add(new Label(row+", "+col));
        } else {
            drawLine();
        }
    }

    // draw a point on the gameSpace to show the play a piece can be placed here
    private void drawPoint() {
        this.point.centerXProperty().bind(this.widthProperty().divide(2));
        this.point.centerYProperty().bind(this.heightProperty().divide(2));
        this.point.radiusProperty().bind(this.widthProperty().divide(8));
        this.point.setFill(Color.WHITE);
        this.point.setStroke(Color.BLACK);
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

        this.point.setEffect(addGlow());

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
        System.out.println("glow");
        System.out.println(
                this.gamePiece.getEffect());
    }

    // return point that is indicator for a space a game piece can be placed
    public Circle getPoint() {
        return point;
    }

    // sets glow for point
    public void setPointGlow(Color c) {
        DropShadow glow = (DropShadow) point.getEffect();
        if (glow!=null)
            glow.setColor(c);
    }

    // return game piece that is on this game space
    public Circle getGamePiece() {
        return gamePiece;
    }

    // sets glow for game piece
    public void setGamePieceGlow(Color c) {
        DropShadow glow = (DropShadow) gamePiece.getEffect();
        if (glow!=null)
            glow.setColor(c);
    }

    // return game space's row
    public int getRow() {
        return row;
    }

    // return game space's col
    public int getCol() {
        return col;
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
        Player turnPlayer = game.getTurnPlayer();
        GameState gameState = turnPlayer.getPlayersGamestate();
        if(!turnPlayer.isCPU() && !board.isRunningAnimation()) {
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

//            gameHistory.logMove(this);
        }
    }

    // handle the users mouse inputs for the moving and flying game state
    private void handleMovingFlying(Player turnPlayer) {
        GameSpace movingGP = board.getMovingGamePiece();
        GamePiece piece = turnPlayer.getGamePieceByLocation(this.row, this.col);
        Color pieceColor = (Color) this.gamePiece.getFill();

        if (this.color == turnPlayer.getColor()) {

            if (movingGP!=null) {
                movingGP.setGamePieceGlow(Color.TRANSPARENT);
            }
            board.setMovingGamePiece(this);

            this.setGamePieceGlow(pieceColor.brighter());
            if (turnPlayer.getPlayersGamestate() == GameState.MOVING )
                piece.updateValidMovesLocations();
            board.highlightCells();
        } else if (game.getCell(this.row, this.col) == game.movingOrFlying() && movingGP != null) {
            turnPlayer.movePiece(this.row, this.col, movingGP.row, movingGP.col);
            board.setMovingGamePiece(null);
        }
    }

    private ParallelTransition animateQueuePlace(Circle animateGP) {
        PlayerPanel turnPanel = board.getTurnPlayerPanel();
        Circle panelGamePiece = turnPanel.getGamePieceFromQueue();

        animateGP.setLayoutY(-200);
        Bounds animateStartBounds = this.localToScene(animateGP.getBoundsInLocal());
        Bounds queuePieceBounds = panelGamePiece.localToScene(panelGamePiece.getBoundsInLocal());

        System.out.println("a: "+animateStartBounds.getWidth());
        System.out.println("q: "+animateStartBounds.getHeight());

        // Translate `panelGamePiece` to match the animateGP start position
        TranslateTransition queueMoveTransition = new TranslateTransition(Duration.millis(500), panelGamePiece);
        queueMoveTransition.setFromX(0);
        queueMoveTransition.setFromY(0);
        queueMoveTransition.setToX(animateStartBounds.getMinX() - queuePieceBounds.getMinX());
        queueMoveTransition.setToY(animateStartBounds.getMinY() - queuePieceBounds.getMinY()-200);
        queueMoveTransition.setInterpolator(Interpolator.EASE_BOTH);

        // grows the panelGamePiece to match animateGP
        ScaleTransition queueGrowTransition = new ScaleTransition(Duration.millis(500), panelGamePiece);
        queueGrowTransition.setFromY(1.0);
        queueGrowTransition.setFromX(1.0);
        queueGrowTransition.setToY(2.0);
        queueGrowTransition.setToX(2.0);
        queueGrowTransition.setInterpolator(Interpolator.EASE_OUT);

        // Combine both the movement, fall and flip sequence into a parallel transition
        ParallelTransition queueParallelTransition = new ParallelTransition(queueMoveTransition, queueGrowTransition);


        queueParallelTransition.setOnFinished(_ -> {
            panelGamePiece.setVisible(false);
            turnPanel.removeFromQueue();
        });
        return queueParallelTransition;
    }

    // run animation for placing a piece
    public void animatePlacePiece(Runnable onFinished) {
        Color color = (game.getTurnPlayer().getColor()=='R') ? board.getRed() : board.getBlue();
        Circle animateGP = drawAnimationGamePiece(this,color);
        // Get Random Axis to rotate on for flip
        Point3D[] axis = new Point3D[]{Rotate.X_AXIS,Rotate.Y_AXIS};

        this.gamePiece.setVisible(false);

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(0));
        pauseTransition.setOnFinished(_ -> {
            animateGP.setVisible(true);
            board.setRunningAnimation(true);
        });

        ParallelTransition queueParallelTransition = animateQueuePlace(animateGP);

        // Create a moving transition to simulate a gamePiece falling down
        TranslateTransition moveTransition = new TranslateTransition(Duration.millis(500), animateGP);
        moveTransition.setFromY(0);
        moveTransition.setToY(200);
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
        SequentialTransition finalTransition = new SequentialTransition(queueParallelTransition, pauseTransition, parallelTransition);

        finalTransition.play();

        finalTransition.setOnFinished(_ -> {
            if (onFinished != null) {
                onFinished.run();
            }
            this.gamePiece.setVisible(true);
            board.updateGameStatus();
            getChildren().remove(animateGP);
            board.setRunningAnimation(false);
        });
    }

    // run animation for moving a piece
    public void animateMovePiece(Runnable onFinished, GameSpace movingGP) {
        Color color = (game.getTurnPlayer().getColor()=='R') ? board.getRed() : board.getBlue();
        Circle animateGP = drawAnimationGamePiece(movingGP,color);
        int rowDiff = this.row-movingGP.row;
        int colDiff = this.col-movingGP.col;

        board.clearHighlights();
        movingGP.gamePiece.setVisible(false);

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(0));
        pauseTransition.setOnFinished(_ ->{
            animateGP.setVisible(true);
            updateCell();
            board.setRunningAnimation(true);
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
            movingGP.gamePiece.setVisible(true);
            this.gamePiece.setVisible(true);
            movingGP.getChildren().remove(animateGP);
            board.setRunningAnimation(false);
        });

    }

    // run animation for removing a piece
    public void animateRemovePiece(Runnable onFinished) {
        PlayerPanel turnPanel = board.getTurnPlayerPanel();
        Circle captureGP = turnPanel.addToCaptureSpace();
        Color borderColor = (Color) captureGP.getStroke();

        Bounds gpStartBounds = this.localToScene(this.gamePiece.getBoundsInLocal());
        Bounds capturePieceBounds = captureGP.localToScene(captureGP.getBoundsInLocal());

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(0));
        pauseTransition.setOnFinished(_ ->{
            this.gamePiece.setVisible(false);
            captureGP.setStroke(Color.TRANSPARENT);
            board.setRunningAnimation(true);
        });

        // Translate `panelGamePiece` to match the animateGP start position
        TranslateTransition captureMoveTransition = new TranslateTransition(Duration.millis(500), captureGP);
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
//            this.gamePiece.setVisible(true);
            captureGP.setStroke(borderColor);
            board.updateGameStatus();
            board.setRunningAnimation(false);
        });
    }

    // update the game piece on this game space
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
