package sprint3.product.GUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sprint3.product.Cell;
import sprint3.product.Game.Game;
import sprint3.product.Game.GameState;
import sprint3.product.Game.NineMMGame;
import sprint3.product.GamePiece;
import sprint3.product.Player.Player;

import java.util.List;


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
			game = new NineMMGame();
		}
		game.setGui(this);
		updateGameStatus();
		gameSize = game.getSize();
		GridPane pane = new GridPane();
		gameSpaces = new GameSpace[gameSize][gameSize];

		// added gameSpace objects to the game grid
		// set empty cell is valid and invalid is not valid
		for (int row = 0; row < gameSize; row++)
			for (int col = 0; col < gameSize; col++)
				if (game.getCell(row, col) == Cell.EMPTY) {
					pane.add(gameSpaces[row][col] = new GameSpace(row, col, true, this), col, row);
				}else {
					pane.add(gameSpaces[row][col] = new GameSpace(row, col, false, this), col, row);
				}
		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(pane);
		borderPane.setBottom(gameStatus);

		Scene scene = new Scene(borderPane, 450, 450);
		primaryStage.setTitle("Nine Men's Morris");
		primaryStage.setScene(scene);
		primaryStage.show();
		game.letCPUMove();
	}

	// updates game status bar
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

	// returns the game piece selected to be moved
    public GameSpace getMovingGamePiece() {
        return movingGamePiece;
    }

	// set the game piece selected to be moved
    public void setMovingGamePiece(GameSpace movingGamePiece) {
        this.movingGamePiece = movingGamePiece;
    }

	// return a gameSpace based on it location
	public GameSpace getGameSpace(int row, int col) {
		return gameSpaces[row][col];
	}

	// updates the game piece on all the game spaces
	public void updateCells() {
		for (int row = 0; row < gameSize; row++){
			for (int col = 0; col < gameSize; col++){
				gameSpaces[row][col].updateCell();
			}
		}
	}

	// give the game spaces adjacent to the selected game piece for player to see where the piece can be moved to
	public void highlightCells(){
		System.out.println("highlightCells");
		Player turnPlayer = game.getTurnPlayer();
		GameState gameState = turnPlayer.getPlayersGamestate();
		clearHighlights();
		switch (gameState) {
			case MOVING:
				highlightForMoving();
				break;
			case FLYING:
				highlightForFlying();
				break;
		}
	}
	// clear highlights on all points
    public void clearHighlights(){
		for (int row = 0; row < this.getGameSize(); row++)
			for (int col = 0; col < this.getGameSize(); col++)
				this.getGameSpace(row, col).getPoint().setStroke(Color.TRANSPARENT);
	}
	// highlights valid Game Spaces for moving game piece
	private void highlightForMoving(){
		GameSpace movingGP = this.getMovingGamePiece();
		Player turnPlayer = game.getTurnPlayer();
		if(movingGP != null) {
			GamePiece gp = turnPlayer.getGamePieceByLocation(movingGP.getRow(), movingGP.getCol());
			List<int[]> validGameSpaces = gp.getValidMovesLocations();
			for (int[] g : validGameSpaces) {
				this.getGameSpace(g[0], g[1]).getPoint().setStroke(Color.GREEN);
			}
		}
	}
	// highlights all empty Game Spaces for moving game piece
	private void highlightForFlying(){
		GameSpace movingGP = this.getMovingGamePiece();
		for (int row = 0; row < this.getGameSize(); row++)
			for (int col = 0; col < this.getGameSize(); col++)
				if (game.getCell(row, col) == game.movingOrFlying() && movingGP != null)
					this.getGameSpace(row, col).getPoint().setStroke(Color.GREEN);
	}

	// return the game the gui is using
	public Game getGame() {
		return game;
	}

	// return the game's board size
	public int getGameSize() {
		return gameSize;
	}

	// returns color used for the red player (player one)
	public Color getRed() {
		return red;
	}

	// returns color used for the blue player (player two)
	public Color getBlue() {
		return blue;
	}

	public static void main(String[] args) {
		launch(args);
	}
}