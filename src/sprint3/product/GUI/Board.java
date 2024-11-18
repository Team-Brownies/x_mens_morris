package sprint3.product.GUI;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import sprint3.product.Cell;
import sprint3.product.CheckMill;
import sprint3.product.Game.Game;
import sprint3.product.Game.GameState;
import sprint3.product.Game.NineMMGame;
import sprint3.product.Game.SixMMGame;
import sprint3.product.GameHistory;
import sprint3.product.GamePiece;
import sprint3.product.Player.CPUPlayer;
import sprint3.product.Player.HumanPlayer;
import sprint3.product.Player.Player;

import java.util.*;

public class Board extends Application {
	private final double sceneSize = 450;
	private GameSpace[][] gameSpaces;
	private PlayerPanel redPanel;
	private PlayerPanel bluePanel;
//	private Label gameStatus = new Label("RED's Turn");
	private Game game;
	private int gameSize = 0;
	private PlayerPanel turnPlayerPanel;
	private PlayerPanel oppPlayerPanel;

	private Color red = Color.RED;
	private Color blue = Color.BLUE;

	private GameSpace movingGamePiece;
	private boolean runningAnimation;

	@Override
	public void start(Stage primaryStage) {
//		GameHistory history = new GameHistory(this);

		double playerPaneSize = sceneSize/3;
		if (game == null) {
			game = new SixMMGame();
			game.setRedPlayer(new HumanPlayer('R', game));
			game.setBluePlayer(new CPUPlayer('B', game));
//		this.redPlayer = ;
//		this.bluePlayer = new HumanPlayer('B',pieces, this);
		}
		game.setGui(this);
		gameSize = game.getSize();
		GridPane pane = new GridPane();
		pane.setBackground(Background.fill(Color.WHITE));
		pane.setStyle("-fx-border-color: gray; -fx-border-width: 7px;");
		gameSpaces = new GameSpace[gameSize][gameSize];

		setUpPlayerPanels(playerPaneSize);

		// added gameSpace objects to the game grid
		// set empty cell is valid and invalid is not valid
		for (int row = 0; row < gameSize; row++)
			for (int col = 0; col < gameSize; col++)
				if (game.getCell(row, col) == Cell.EMPTY) {
					pane.add(gameSpaces[row][col] = new GameSpace(row, col, true, this), col, row);
				}else {
					pane.add(gameSpaces[row][col] = new GameSpace(row, col, false, this), col, row);
				}

		// Exit button
		Button exitButton = new Button("Exit");
		exitButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Arial';");
		exitButton.setOnAction(e -> exitGame(primaryStage));

		// Create the options layout (HBox)
		HBox optionsLayout = new HBox(20);  // 20px space between children (you can adjust)
		optionsLayout.setStyle("-fx-padding: 10px; -fx-alignment: TOP_RIGHT;");  // Optional padding for overall HBox

		// Add exit button to the left
		optionsLayout.getChildren().add(exitButton);

		// Add the undo button to the right
		optionsLayout.getChildren().add(createRedoButton());

		BorderPane MainPane = new BorderPane();
		MainPane.setCenter(pane);
		MainPane.setTop(optionsLayout);
//		MainPane.setBottom(gameStatus);

		MainPane.setLeft(redPanel);
		MainPane.setRight(bluePanel);

		Scene scene = new Scene(MainPane, sceneSize+(playerPaneSize*2), sceneSize);
		primaryStage.setTitle("Nine Men's Morris");
		primaryStage.setScene(scene);
		primaryStage.show();
		game.letCPUMove();

		updateGameStatus();
	}

	private void setUpPlayerPanels(double playerPaneSize) {
		redPanel = new PlayerPanel(playerPaneSize, red, blue, game.getRedPlayer());
		bluePanel = new PlayerPanel(playerPaneSize, blue, red, game.getBluePlayer());

		turnPlayerPanel = redPanel;
		oppPlayerPanel = bluePanel;
	}

	// updates game status bar
	public void updateGameStatus(){
		turnPlayerPanel.updatePlayerStatus();
		oppPlayerPanel.updatePlayerStatus();

		turnPlayerPanel.setGlowVisible(true);
		oppPlayerPanel.setGlowVisible(false);

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

	// Function to handle exiting the game or going back to main menu
	private void exitGame(Stage primaryStage) {
		nineMensMorris.Main homeScreen = new nineMensMorris.Main();
		homeScreen.start(primaryStage);
	}

	//undo for undoButton
	private void undoAction() {
		// Retrieve the last move from the history
//		gameHistory.undoMove(game);
	}

	// Helper method to create the Undo button
	private Button createRedoButton() {
		// Create a new button
		Button redoButton = new Button("Undo");

		redoButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Arial';");

		redoButton.setOnAction(e -> {
			System.out.println("Redo action triggered");
			undoAction();
		});

		return redoButton;
	}


	// clear highlights on all points
    public void clearHighlights(){
		for (int row = 0; row < this.getGameSize(); row++)
			for (int col = 0; col < this.getGameSize(); col++) {
				this.getGameSpace(row, col).setPointGlow(Color.TRANSPARENT);
			}
	}
	// highlights valid Game Spaces for moving game piece
	private void highlightForMoving(){
		GameSpace movingGP = this.getMovingGamePiece();
		Player turnPlayer = game.getTurnPlayer();

		if(movingGP != null) {
			GamePiece gp = turnPlayer.getGamePieceByLocation(movingGP.getRow(), movingGP.getCol());
			List<int[]> validGameSpaces = gp.getValidMovesLocations();
			for (int[] g : validGameSpaces) {
				this.getGameSpace(g[0], g[1]).setPointGlow(Color.GREEN.brighter());
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
	// run animation each piece in the mill
	private SequentialTransition animateEachMillPiece(Point3D axis, List<int[]> sortedMillMates){
		GameSpace gameSpace;
		Circle animateGP;
		SequentialTransition finalTransition = new SequentialTransition();
		for (int[] coords:sortedMillMates) {
			gameSpace = this.getGameSpace(coords[0],coords[1]);
			animateGP = gameSpace.getGamePiece();
			gameSpace.toFront();
			animateGP.toFront();

			ScaleTransition growTransition = new ScaleTransition(Duration.millis(250), animateGP);
			growTransition.setFromY(1.0);
			growTransition.setFromX(1.0);
			growTransition.setToY(2.0);
			growTransition.setToX(2.0);
			growTransition.setInterpolator(Interpolator.EASE_OUT);

			ScaleTransition shrinkTransition = new ScaleTransition(Duration.millis(250), animateGP);
			shrinkTransition.setFromY(2.0);
			shrinkTransition.setFromX(2.0);
			shrinkTransition.setToY(1.0);
			shrinkTransition.setToX(1.0);
			shrinkTransition.setInterpolator(Interpolator.EASE_IN);

			// Create a flipping transition
			RotateTransition flipTransition = new RotateTransition(Duration.millis(500), animateGP);
			flipTransition.setAxis(axis);
			flipTransition.setFromAngle(0);
			flipTransition.setToAngle(360);  // Full rotation for a coin flip effect
			flipTransition.setInterpolator(Interpolator.EASE_BOTH);


			// Add pause and parallel transitions into a sequential transition to start with a pause
			SequentialTransition scaleTransition = new SequentialTransition(growTransition, shrinkTransition);

			// Combine both the movement, fall and flip sequence into a parallel transition
			ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, flipTransition);

			finalTransition.getChildren().add(parallelTransition);
		}

		return finalTransition;
	}
	// run animation for when a mill is formed
	public void animateMillForm(Runnable onFinished, List<int[]> millMates) {
		CheckMill millChecker = new CheckMill(game.getGrid());

		int inCommonIndex = millChecker.findCommonIndex(millMates);
		Point3D axis = (inCommonIndex==0) ? Rotate.Y_AXIS:Rotate.X_AXIS;

		PauseTransition pauseTransition = new PauseTransition(Duration.millis(0));
		pauseTransition.setOnFinished(_ ->{
			this.setRunningAnimation(true);
		});

		// Add pause and parallel transitions into a sequential transition to start with a pause
		SequentialTransition sequentialTransition = new SequentialTransition(pauseTransition, animateEachMillPiece(axis, millMates));

		sequentialTransition.play();

		sequentialTransition.setOnFinished(_ -> {
			if (onFinished != null) {
				onFinished.run();
			}
			this.setRunningAnimation(false);
		});
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

	public PlayerPanel getTurnPlayerPanel() {
		return turnPlayerPanel;
	}

	public PlayerPanel getOppPlayerPanel() {
		return oppPlayerPanel;
	}

	public void changeTurnPlayerPanel() {
		this.turnPlayerPanel = (this.turnPlayerPanel.getPlayerColor() == red) ? this.bluePanel : this.redPanel;
		this.oppPlayerPanel = (this.oppPlayerPanel.getPlayerColor() == blue) ? this.redPanel : this.bluePanel;
//		this.turnPlayer = (this.turnPlayer.getColor() == 'R') ? this.bluePlayer : this.redPlayer;
//		this.opponentPlayer = (this.opponentPlayer.getColor() == 'B') ? this.redPlayer : this.bluePlayer;
	}



	public static void main(String[] args) {
		launch(args);
	}

	public boolean isRunningAnimation() {
		return runningAnimation;
	}

	public void setRunningAnimation(boolean runningAnimation) {
		this.runningAnimation = runningAnimation;
	}
}