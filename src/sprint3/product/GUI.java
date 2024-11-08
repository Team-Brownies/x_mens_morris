package sprint3.product;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import sprint3.product.Game.Game;
import sprint3.product.Game.NMMGame;
import sprint3.product.Game.SixMMGame;
import sprint3.product.Player.Player;


public class GUI extends Application {
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
		updateGameStatus();
		gameSize = game.getSize();
		GridPane pane = new GridPane();
		gameSpaces = new GameSpace[gameSize][gameSize];
		for (int row = 0; row < gameSize; row++)
			for (int col = 0; col < gameSize; col++)
				if (game.getCell(row, col) == Cell.EMPTY)
					pane.add(gameSpaces[row][col] = new GameSpace(row,col, true), col, row);
				else
					pane.add(gameSpaces[row][col] = new GameSpace(row,col, false), col, row);

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(pane);
		borderPane.setBottom(gameStatus);

		Scene scene = new Scene(borderPane, 450, 450);
		primaryStage.setTitle("Nine Men's Morris");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void updateGameStatus(){
		String updateGameStatus = "Turn: " ;
		String gameState;
		gameState = String.valueOf(game.getCurrentGamestate());
		updateGameStatus += (game.getTurnPlayer().getColor()=='R') ? "RED" : "BLUE";
		updateGameStatus += "\nGame State: "+gameState;
		updateGameStatus += "\nPieces left: " + game.getTurnPlayer().numberOfGamePieces();
		gameStatus.setText(updateGameStatus);
	}

    public GameSpace getMovingGamePiece() {
        return movingGamePiece;
    }

    public void setMovingGamePiece(GameSpace movingGamePiece) {
        this.movingGamePiece = movingGamePiece;
    }

	public class GameSpace extends Pane {
		private int row, col;
		private char color;
		private Circle gamePiece = new Circle();
		private Circle point = new Circle();

		public GameSpace(int row, int col, boolean valid) {
			this.row = row;
			this.col = col;
			this.setPrefSize(2000, 2000);
			if (valid){
				drawPoint();
				this.setOnMouseClicked(e -> handleMouseClick());
				getChildren().add(new Label(row+", "+col));
			} else {
				drawLine();
			}
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
			int max = gameSize-1;
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
			int max = gameSize-1;
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

		private void highlightCells(){
			GameSpace movingGP = getMovingGamePiece();
			for (int row = 0; row < gameSize; row++)
				for (int col = 0; col < gameSize; col++)
					if (game.getCell(row, col) == game.movingOrFlying() && movingGP != null)
						gameSpaces[row][col].point.setStroke(Color.GREEN);
					else
						gameSpaces[row][col].point.setStroke(Color.TRANSPARENT);
		}

		private void handleMouseClick() {
			Player turnPlayer = game.getTurnPlayer();
			char turnPlayerColor = turnPlayer.getColor();
			if(!turnPlayer.isCPU()) {
				if (game.getCurrentGamestate() == NMMGame.GameState.PLACING) {
					turnPlayer.placePiece(this.row, this.col);

				} else if (game.getCurrentGamestate() == NMMGame.GameState.MOVING ||
						game.getCurrentGamestate() == NMMGame.GameState.FLYING) {
					handleMovingFlying(turnPlayerColor);
				} else if (game.getCurrentGamestate() == NMMGame.GameState.MILLING) {
					turnPlayer.removePiece(this.row, this.col);
				}
			}
			updateCells();
			updateGameStatus();
		}
		private void handleMovingFlying(char turnPlayerColor) {
			GameSpace movingGP = getMovingGamePiece();
			if (this.color == turnPlayerColor) {
				if (movingGP!=null) {
					movingGP.gamePiece.setStroke(Color.TRANSPARENT);
					game.clearHighlightCells();
				}
				setMovingGamePiece(this);
				this.gamePiece.setStroke(Color.GREEN);
				if (game.getCurrentGamestate() == NMMGame.GameState.MOVING )
					game.findAdjacentCells(row, col);
				highlightCells();
			} else if (game.getCell(this.row, this.col) == game.movingOrFlying() && movingGP != null) {
				animateMovePiece(this, movingGP);
				setMovingGamePiece(null);
			}
			highlightCells();
		}

		private void animateMovePiece(GameSpace gp, GameSpace movingGP) {
			Player turnPlayer = game.getTurnPlayer();
			int rowDiff = gp.row-movingGP.row;
			int colDiff = gp.col-movingGP.col;

			TranslateTransition transition = new TranslateTransition();
			movingGP.toFront();
			transition.setNode(movingGP.gamePiece);
			transition.setToX(movingGP.gamePiece.getCenterX()*colDiff*2);
			transition.setToY(movingGP.gamePiece.getCenterY()*rowDiff*2);
			transition.setInterpolator(Interpolator.LINEAR);
			transition.play();

			movingGP.gamePiece.setTranslateX(0);
			movingGP.gamePiece.setTranslateY(0);

			transition.setOnFinished(event -> {
				movingGP.gamePiece.setTranslateX(0);
				movingGP.gamePiece.setTranslateY(0);
				turnPlayer.movePiece(gp.row, gp.col, movingGP.row, movingGP.col);
				updateCells();
				updateGameStatus();
			});

		}
	}


	private void updateCells() {
		Cell cell;
		Circle gamePiece;
		char color;

		for (int row = 0; row < gameSize; row++){
			for (int col = 0; col < gameSize; col++){
				cell = game.getCell(row, col);
				gamePiece = gameSpaces[row][col].gamePiece;

				if (cell == Cell.RED) {
					gamePiece.setFill(red);
					color = 'R';
				}
				else if (cell == Cell.BLUE)  {
					gamePiece.setFill(blue);
					color = 'B';
				} else {
					gamePiece.setFill(Color.TRANSPARENT);
					gamePiece.setStroke(Color.TRANSPARENT);
					color = ' ';
				}
				gameSpaces[row][col].color = color;
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}