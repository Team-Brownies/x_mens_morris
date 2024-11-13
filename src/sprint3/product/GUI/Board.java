package sprint3.product.GUI;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import sprint3.product.Cell;
import sprint3.product.Game.Game;
import sprint3.product.Game.NMMGame;
import sprint3.product.GUI.GameSpace;
import sprint3.product.GamePiece;
import sprint3.product.Player.Player;

import java.util.Arrays;


public class Board extends Application {
	private GameSpace[][] gameSpaces;
	private Label gameStatus = new Label("RED's Turn");
	private Game game;
	private int gameSize = 0;

	private Color red = Color.RED;
	private Color blue = Color.BLUE;

	private GameSpace movingGamePiece;

	@Override
	public void start(Stage primaryStage) {
		if (game == null) {
			game = new NMMGame();
		}
		game.setGui(this);
		updateGameStatus();
		gameSize = game.getSize();
		GridPane pane = new GridPane();
		gameSpaces = new GameSpace[gameSize][gameSize];
		for (int row = 0; row < gameSize; row++)
			for (int col = 0; col < gameSize; col++)
				if (game.getCell(row, col) == Cell.EMPTY)
					pane.add(gameSpaces[row][col] = new GameSpace(row,col, true, this), col, row);
				else
					pane.add(gameSpaces[row][col] = new GameSpace(row,col, false, this), col, row);

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(pane);
		borderPane.setBottom(gameStatus);

		Scene scene = new Scene(borderPane, 450, 450);
		primaryStage.setTitle("Nine Men's Morris");
		primaryStage.setScene(scene);
		primaryStage.show();
		game.letCPUMove();
	}

	public void updateGameStatus(){
		String updateGameStatus = "Turn: " ;
		String gameState;
		gameState = String.valueOf(game.getTurnPlayer().getPlayersGamestate());
		updateGameStatus += (game.getTurnPlayer().getColor()=='R') ? "RED" : "BLUE";
		updateGameStatus += "\nGame State: "+gameState;
		updateGameStatus += "\nPieces left: " + game.getTurnPlayer().numberOfGamePieces();
		gameStatus.setText(updateGameStatus);
		updateCells();
	}

    public GameSpace getMovingGamePiece() {
        return movingGamePiece;
    }

    public void setMovingGamePiece(GameSpace movingGamePiece) {
        this.movingGamePiece = movingGamePiece;
    }

//	public class GameSpace extends Pane {
//		private int row, col;
//		private char color;
//		private Circle gamePiece = new Circle();
//		private Circle point = new Circle();
//
//		public GameSpace(int row, int col, boolean valid) {
//			this.row = row;
//			this.col = col;
//			this.setPrefSize(2000, 2000);
//			if (valid){
//				drawPoint();
//				this.setOnMouseClicked(e -> handleMouseClick());
//				getChildren().add(new Label(row+", "+col));
//			} else {
//				drawLine();
//			}
//		}
//
//		public Circle getGamePiece() {
//			return gamePiece;
//		}
//
//		private void drawPoint() {
//			this.point.centerXProperty().bind(this.widthProperty().divide(2));
//			this.point.centerYProperty().bind(this.heightProperty().divide(2));
//			this.point.radiusProperty().bind(this.widthProperty().divide(8));
//			this.point.setFill(Color.BLACK);
//			this.point.setStrokeWidth(2);
//
//			if (validLine(this.col, this.row, -1))
//				drawPointLine(0.5, 0.5, 1.0, 0.5);  // Right Line
//			if (validLine(this.row, this.col, -1))
//				drawPointLine(0.5, 0.5, 0.5, 1.0);  // Down Line
//			if (validLine(this.col, this.row, 1))
//				drawPointLine(0.0, 0.5, 0.5, 0.5);  // Left Line
//			if (validLine(this.row, this.col, 1))
//				drawPointLine(0.5, 0.0, 0.5, 0.5);  // Up Line
//			getChildren().add(this.point);
//			drawGamePiece();
//		}
//		private boolean validLine(int x, int y, int d ){
//			int start, end, center;
//			int max = gameSize-1;
//			int middle = max/2;
//
//			if (d==1){
//				start = middle;
//				end = 0;
//				center = max;
//			} else {
//				start = 0;
//				end = max;
//				center = middle;
//			}
//
//			return ((x>=start&&x<=center)
//					&&!(x==middle+d&&y==middle)
//					||(y==middle&&!(x==end||x==middle+d)));
//		}
//
//		private void drawPointLine(double startX, double startY, double endX, double endY) {
//			Line line = new Line();
//			line.setStroke(Color.BLACK);
//			line.setStrokeWidth(5.0);
//			line.startXProperty().bind(this.widthProperty().multiply(startX));
//			line.startYProperty().bind(this.heightProperty().multiply(startY));
//			line.endXProperty().bind(this.widthProperty().multiply(endX));
//			line.endYProperty().bind(this.heightProperty().multiply(endY));
//			getChildren().add(line);
//		}
//
//		private void drawLine() {
//			int max = gameSize-1;
//			Line line = new Line();
//			line.setStroke(Color.BLACK);
//			line.setStrokeWidth(5.0);
//			if (this.row == 0 || this.row == max || (this.row == 1 && (this.col>0 && this.col<max))
//					|| (this.row == max-1 && (this.col > 0 && this.col< max))) {
//				line.startYProperty().bind(this.heightProperty().divide(2));
//				line.endXProperty().bind(this.widthProperty());
//				line.endYProperty().bind(this.heightProperty().divide(2));
//			}
//			else if (this.row==this.col){
//				line.setStroke(Color.TRANSPARENT);
//			}
//			else {
//				line.startXProperty().bind(this.widthProperty().divide(2));
//				line.endXProperty().bind(this.widthProperty().divide(2));
//				line.endYProperty().bind(this.heightProperty());
//			}
//			getChildren().add(line);
//		}
//
//		private void drawGamePiece() {
//			this.gamePiece.centerXProperty().bind(this.widthProperty().divide(2));
//			this.gamePiece.centerYProperty().bind(this.heightProperty().divide(2));
//			this.gamePiece.radiusProperty().bind(this.widthProperty().divide(4));
//			this.gamePiece.setStrokeWidth(2);
//			this.gamePiece.setFill(Color.TRANSPARENT);
//
//			getChildren().add(this.gamePiece);
//		}
//		private Circle drawAnimateGamePiece(GameSpace gp, Color color) {
//			Circle animateGP = new Circle();
//
//			animateGP.centerXProperty().bind(this.widthProperty().divide(2));
//			animateGP.centerYProperty().bind(this.heightProperty().divide(2));
//			animateGP.radiusProperty().bind(this.widthProperty().divide(4));
//			animateGP.setStrokeWidth(2);
//
//			gp.getChildren().add(animateGP);
//			animateGP.setFill(color);
//			gp.toFront();
//			animateGP.toFront();
//
//			animateGP.setVisible(false);
//
//			return animateGP;
//		}
//
//		private void highlightCells(){
//			GameSpace movingGP = getMovingGamePiece();
//			for (int row = 0; row < gameSize; row++)
//				for (int col = 0; col < gameSize; col++)
//					if (game.getCell(row, col) == game.movingOrFlying() && movingGP != null)
//						gameSpaces[row][col].point.setStroke(Color.GREEN);
//					else
//						gameSpaces[row][col].point.setStroke(Color.TRANSPARENT);
//		}
//
//		private void handleMouseClick() {
//			Player turnPlayer = game.getTurnPlayer();
//			GameState gameState = turnPlayer.getPlayersGamestate();
//			if(!turnPlayer.isCPU()) {
//				System.out.println(Arrays.toString(new int[]{this.row, this.col}));
//				System.out.println(game.getCell(this.row,this.col));
//				switch (gameState) {
//					case PLACING:
//						if(game.canPlacePiece(this.row, this.col))
//							turnPlayer.placePiece(this.row, this.col);
//						break;
//					case MOVING,FLYING:
//						handleMovingFlying(turnPlayer);
//						break;
//					case MILLING:
//						turnPlayer.removePiece(this.row, this.col);
//						break;
//				}
//			}
//		}
//		private void handleMovingFlying(Player turnPlayer) {
//			GameSpace movingGP = getMovingGamePiece();
//			GamePiece piece = turnPlayer.getGamePieceByLocation(this.row, this.col);
//			if (this.color == turnPlayer.getColor()) {
//
//				if (movingGP!=null) {
//					movingGP.gamePiece.setStroke(Color.TRANSPARENT);
//					game.clearHighlightCells();
//				}
//				setMovingGamePiece(this);
//
//				this.gamePiece.setStroke(Color.GREEN);
//				if (turnPlayer.getPlayersGamestate() == GameState.MOVING )
//					piece.updateValidMovesLocations();
//				piece.printValidMoves();
//				highlightCells();
//			} else if (game.getCell(this.row, this.col) == game.movingOrFlying() && movingGP != null) {
//				turnPlayer.movePiece(this.row, this.col, movingGP.row, movingGP.col);
//				setMovingGamePiece(null);
//			}
//			highlightCells();
//		}
//
//		public void animatePlacePiece(Runnable onFinished) {
//			Color color = (game.getTurnPlayer().getColor()=='R') ? red : blue;
//			Circle animateGP = drawAnimateGamePiece(this,color);
//			this.gamePiece.setVisible(false);
//
//			PauseTransition pauseTransition = new PauseTransition(Duration.millis(0));
//			pauseTransition.setOnFinished(event ->{
//				animateGP.setVisible(true);
//			});
//
//			TranslateTransition translateTransition = new TranslateTransition(Duration.millis(500), animateGP);
//			translateTransition.setFromY(-200);
//			translateTransition.setToY(0);
//			translateTransition.setInterpolator(javafx.animation.Interpolator.EASE_IN);
//
//			SequentialTransition sequentialTransition = new SequentialTransition(pauseTransition, translateTransition);
//			sequentialTransition.play();
//
//			sequentialTransition.setOnFinished(event -> {
//				if (onFinished != null) {
//					onFinished.run();
//				}
//				this.gamePiece.setVisible(true);
//				updateGameStatus();
//				getChildren().remove(animateGP);
//			});
//		}
//		public void animateMovePiece(Runnable onFinished, GameSpace movingGP) {
//			Color color = (game.getTurnPlayer().getColor()=='R') ? red : blue;
//			Circle animateGP = drawAnimateGamePiece(movingGP,color);
//			int rowDiff = this.row-movingGP.row;
//			int colDiff = this.col-movingGP.col;
//
//			PauseTransition pauseTransition = new PauseTransition(Duration.millis(0));
//			pauseTransition.setOnFinished(event ->{
//				animateGP.setVisible(true);
//				updateCells();
//			});
//
//			TranslateTransition transition = new TranslateTransition();
//			transition.setNode(animateGP);
//			transition.setToX(animateGP.getCenterX()*colDiff*2);
//			transition.setToY(animateGP.getCenterY()*rowDiff*2);
//			transition.setInterpolator(Interpolator.LINEAR);
//
//			SequentialTransition sequentialTransition = new SequentialTransition(pauseTransition, transition);
//			sequentialTransition.play();
//
//
//			animateGP.setTranslateX(0);
//			animateGP.setTranslateY(0);
//
//			transition.setOnFinished(event -> {
//				if (onFinished != null) {
//					onFinished.run();
//				}
//				updateGameStatus();
//				movingGP.getChildren().remove(animateGP);
//			});
//
//		}
//
//		public void animateRemovePiece(Runnable onFinished) {
//			Color color = (game.getOpponentPlayer().getColor()=='R') ? red : blue;
//			Circle animateGP = drawAnimateGamePiece(this,color);
//
//			PauseTransition pauseTransition = new PauseTransition(Duration.millis(0));
//			pauseTransition.setOnFinished(event ->{
//				animateGP.setVisible(true);
//				updateCells();
//			});
//
//			TranslateTransition flingTransition = new TranslateTransition(Duration.millis(500), animateGP);
//
//			flingTransition.setToX(800);
//			flingTransition.setToY(-200);
//
//			flingTransition.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
//
//			SequentialTransition sequentialTransition = new SequentialTransition(pauseTransition, flingTransition);
//			sequentialTransition.play();
//
//			flingTransition.setOnFinished(event -> {
//				if (onFinished != null) {
//					onFinished.run();
//				}
//				//updateMill();
//				updateGameStatus();
//				getChildren().remove(animateGP);
//			});
//		}
//	}

	public GameSpace getGameSpace(int row, int col) {
		return gameSpaces[row][col];
	}

	public void updateCells() {
		for (int row = 0; row < gameSize; row++){
			for (int col = 0; col < gameSize; col++){
				gameSpaces[row][col].updateCell();
			}
		}
	}

	public Game getGame() {
		return game;
	}

	public int getGameSize() {
		return gameSize;
	}

	public Color getRed() {
		return red;
	}

	public Color getBlue() {
		return blue;
	}

	public static void main(String[] args) {
		launch(args);
	}
}