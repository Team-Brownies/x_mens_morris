package sprint2.product;

import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;


public class GUI extends Application {
	private GameSpace[][] gameSpaces;
	private Label gameStatus = new Label("RED's Turn");
	private NMMGame game;
	private int gameSize = 0;

	private GameSpace movingGamePiece;

	@Override
	public void start(Stage primaryStage) {
		if (game == null) {
			game = new NMMGame(9);
		}
		updateGameStatus();
		gameSize = game.getSize();
		GridPane pane = new GridPane();
		gameSpaces = new GameSpace[gameSize][gameSize];
		for (int row = 0; row < gameSize; row++)
			for (int col = 0; col < gameSize; col++)
				if (game.getCell(row, col) == NMMGame.Cell.EMPTY)
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
			for (int row = 0; row < gameSize; row++)
				for (int col = 0; col < gameSize; col++)
					if ((game.getCell(row, col) == NMMGame.Cell.MOVEVALID
							&& game.getCurrentGamestate() == NMMGame.GameState.MOVING)
							|| (game.getCell(row, col) == NMMGame.Cell.EMPTY
							&& game.getCurrentGamestate() == NMMGame.GameState.FLYING))
						gameSpaces[row][col].point.setStroke(Color.GREEN);
					else
						gameSpaces[row][col].point.setStroke(Color.TRANSPARENT);
		}

		private void handleMouseClick() {
			char turnPlayerColor = game.getTurnPlayer().getColor();
			if (game.getCurrentGamestate() == NMMGame.GameState.PLACING){
				game.placePiece(this.row, this.col);
			} else if (game.getCurrentGamestate() == NMMGame.GameState.MOVING ||
					game.getCurrentGamestate() == NMMGame.GameState.FLYING) {
				handleMovingFlying(turnPlayerColor);
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
			} else if (game.getCell(this.row, this.col) == game.movingOrFlying())
				game.movePiece(this.row, this.col, movingGP.row, movingGP.col);
			highlightCells();
		}
	}

	private void updateCells() {
		NMMGame.Cell cell;
		Circle gamePiece;
		char color;

		for (int row = 0; row < gameSize; row++){
			for (int col = 0; col < gameSize; col++){
				cell = game.getCell(row, col);
				gamePiece = gameSpaces[row][col].gamePiece;

				if (cell == NMMGame.Cell.RED) {
					gamePiece.setFill(Color.RED);
					color = 'R';
				}
				else if (cell == NMMGame.Cell.BLUE)  {
					gamePiece.setFill(Color.BLUE);
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