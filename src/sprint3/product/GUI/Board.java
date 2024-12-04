package sprint3.product.GUI;

import com.google.gson.JsonArray;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import sprint3.product.Cell;
import sprint3.product.CheckMill;
import sprint3.product.Game.*;
import sprint3.product.GamePiece;
import sprint3.product.Player.CPUPlayer;
import sprint3.product.Player.HumanPlayer;
import sprint3.product.Player.Player;
import sprint3.product.Player.ScriptedPlayer;

import java.lang.reflect.Field;
import java.util.*;

public class Board extends Application {
	private final double sceneSize = 450;
	private final GameMode gameType;
	private final boolean isReplay;
	private final boolean isRedCPU;
	private final boolean isBlueCPU;
	private final NewGameScreen gameMenu;
	private final JsonArray moveArray;
	private final Main replayPage;
	private GameSpace[][] gameSpaces;
	private PlayerPanel redPanel;
	private PlayerPanel bluePanel;
	private Game game;
	private int gameSize = 0;
	private PlayerPanel turnPlayerPanel;
	private PlayerPanel oppPlayerPanel;

	private Color red = Color.RED;
	private Color blue = Color.BLUE;

	private GameSpace movingGamePiece;
	private boolean runningAnimation;
	private Label gameStatus = new Label("");
	private int redDifficulty;
	private int blueDifficulty;
	private ReplayControls replayControls;
	private boolean animateOff;

	public Board(
            GameMode gameType,
            boolean isRedCPU,
            boolean isBlueCPU,
            int redDifficulty,
            int blueDifficulty,
            NewGameScreen gameMenu
    ) {
		this.gameType = gameType;
        this.isReplay=false;
		this.moveArray = null;
        this.isRedCPU = isRedCPU;
        this.isBlueCPU = isBlueCPU;
		this.redDifficulty = redDifficulty;
		this.blueDifficulty = blueDifficulty;
		this.gameMenu = gameMenu;
		this.replayPage = null;
    }
	public Board(GameMode gameType, JsonArray moveArray, Main replayPage) {
        this.gameType = gameType;
		this.isReplay=true;
		this.isRedCPU = true;
		this.isBlueCPU = true;
		this.gameMenu = null;
		this.replayPage = replayPage;
		this.moveArray = moveArray;
	}

	@Override
	public void start(Stage primaryStage) {

		double playerPaneSize = sceneSize/3;
		if (game == null) {
			loadGameType();
			setPlayers();
		}
		game.setGui(this);
		gameSize = game.getSize();
		GridPane gamePane = new GridPane();
		gamePane.setBackground(Background.fill(Color.WHITE));
		gamePane.setStyle("-fx-border-color: gray; -fx-border-width: 7px;");
		gameSpaces = new GameSpace[gameSize][gameSize];

		setUpPlayerPanels(playerPaneSize);

		// added gameSpace objects to the game grid
		// set empty cell is valid and invalid is not valid
		for (int row = 0; row < gameSize; row++)
			for (int col = 0; col < gameSize; col++)
				if (game.getCell(row, col) == Cell.EMPTY) {
					gamePane.add(gameSpaces[row][col] = new GameSpace(row, col, true, this), col, row);
				}else {
					gamePane.add(gameSpaces[row][col] = new GameSpace(row, col, false, this), col, row);
				}

		// Exit button
		Button exitButton = new Button("Exit");
		exitButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Arial';");
		exitButton.setOnAction(e -> exitGame(primaryStage));
		// Restart button
		Button restartButton = new Button("Restart");
		restartButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Arial';");
		if (!isReplay)
			restartButton.setOnAction(e -> restartGame(gameMenu));
		else
			restartButton.setOnAction(e -> restartReplay());
		// Create the options layout (HBox) // 20px space between children (you can adjust)
		HBox optionsLayout = new HBox(20);
		optionsLayout.setStyle("-fx-padding: 10px; -fx-alignment: TOP_RIGHT; -fx-background-color: lightgrey;");  // Optional padding for overall HBox

		gameStatus.setStyle(
				"-fx-font-size: 14px; " +
						"-fx-font-size: 14px; " +
						"-fx-font-weight: bold; " +
						"-fx-text-alignment: center;" +
						"-fx-alignment: center;" +
						"-fx-background-color: lightgrey;"
		);
		optionsLayout.getChildren().add(restartButton);

		// Add exit button to the left
		optionsLayout.getChildren().add(exitButton);

		// Add the undo button to the right
		optionsLayout.getChildren().add(createRedoButton());

		BorderPane mainPane = new BorderPane();

		mainPane.setCenter(gamePane);
		mainPane.setLeft(redPanel);
		mainPane.setRight(bluePanel);
		mainPane.setTop(optionsLayout);
		mainPane.setBottom(gameStatus);

		if(isReplay)
			addReplayControls(optionsLayout);

		optionsLayout.setMinHeight(50);
		gameStatus.setMinHeight(50);

		gameStatus.setMinWidth(sceneSize+(playerPaneSize*2));
		Scene scene = new Scene(mainPane, sceneSize+(playerPaneSize*2), sceneSize+100);
		primaryStage.setTitle("Nine Men's Morris");
		primaryStage.setScene(scene);
		primaryStage.show();
		game.letCPUMove();

		updateGameStatus();
	}

	private void addReplayControls(HBox optionsLayout) {
		assert moveArray != null;
		this.replayControls = new ReplayControls(moveArray, game);
		optionsLayout.getChildren().add(replayControls);
	}

	public void setReplaySeekSlider(int turnNumber) {
		this.replayControls.setSeek(turnNumber);
	}

	private void setPlayers() {
		if (isReplay){
			animateOff = true;
			game.setRedPlayer(new ScriptedPlayer('R', game, moveArray, this));
			game.setBluePlayer(new ScriptedPlayer('B', game, moveArray, this));
		} else {
			animateOff = false;
			game.setRedPlayer(isRedCPU ? new CPUPlayer('R', game, this.redDifficulty) : new HumanPlayer('R', game));
			game.setBluePlayer(isBlueCPU ? new CPUPlayer('B', game, this.blueDifficulty) : new HumanPlayer('B', game));
		}
	}

	private void loadGameType() {
		switch(gameType) {
			case NINE:
				game = new NineMMGame();
				break;
			case SIX:
				game = new SixMMGame();
				break;

		}
	}

	private void setUpPlayerPanels(double playerPaneSize) {
		redPanel = new PlayerPanel(playerPaneSize, red, blue, game.getRedPlayer());
		bluePanel = new PlayerPanel(playerPaneSize, blue, red, game.getBluePlayer());

		turnPlayerPanel = redPanel;
		oppPlayerPanel = bluePanel;
	}

	// updates game status bar
	public void updateGameStatus(){
		if (game.isDeletedGame()) {
			cleanBoard();
			return; // Exit early if the game has ended
		}
		String gameState = String.valueOf(game.getTurnPlayer().getPlayersGamestate());
		Color turnColor = game.getTurnPlayer().getColor()=='R' ? red : blue;

		gameStatus.setText(gameState);
		gameStatus.setTextFill(turnColor.darker());

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
			case MOVING, FLYING:
				highlightForMoving();
				break;
        }
	}

	// Function to handle exiting the game or going back to main menu
	private void exitGame(Stage primaryStage) {
		game.endGame();
		Main homeScreen = new Main();
        homeScreen.start(primaryStage);

        System.gc();
	}

	private void restartGame(NewGameScreen gameMenu) {
		game.endGame();
		gameMenu.restartGame();

		System.gc();
	}

	public void restartReplay() {
		game.endGame();
        replayPage.viewReplay();

		System.gc();
	}
	private void cleanBoard() {
		if (game != null) {
			game = null;
		}

		if (gameSpaces != null) {
			for (int row = 0; row < gameSize; row++) {
				for (int col = 0; col < gameSize; col++) {
					gameSpaces[row][col] = null;
				}
			}
			gameSpaces = null;
		}
		redPanel = null;
		bluePanel = null;
		movingGamePiece = null;



		System.out.println("Board resources have been cleaned up. Exiting the game...");
		System.out.println("Board has ended.");
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
				if (game.movingOrFlying(row, col) && movingGP != null)
					this.getGameSpace(row, col).setPointGlow(Color.GREEN.brighter());
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
		if(animateOff){
			if (onFinished != null) {
				onFinished.run();
			}
			return;
		}
		CheckMill millChecker = new CheckMill(game.getGrid());

		int inCommonIndex = millChecker.findCommonIndex(millMates);
		Point3D axis = (inCommonIndex==0) ? Rotate.Y_AXIS:Rotate.X_AXIS;

		PauseTransition pauseTransition = new PauseTransition(Duration.millis(0));
		pauseTransition.setOnFinished(_ -> this.setRunningAnimation(true));

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

	// animation for a gameOver
	public void animateGameOver(List<int[]> loserPieces) {
		SequentialTransition finalTransition = new SequentialTransition();
		Color winnerColor = (game.getOpponentPlayer().getColor() == 'R') ? red : blue;

		PauseTransition piecePause = new PauseTransition(Duration.millis(0));
		piecePause.setOnFinished(_ -> {
			gameStatus.setTextFill(winnerColor);
			redPanel.setGlowVisible(false);
			bluePanel.setGlowVisible(false);
			this.setRunningAnimation(true);
		});

		finalTransition.getChildren().add(piecePause);

		for (int[] coords:loserPieces) {
			Circle gamePiece = this.getGameSpace(coords[0], coords[1]).getGamePiece();
			Color loserColor = (Color) gamePiece.getFill();

			// Wobble effect
			ScaleTransition wobble = new ScaleTransition(Duration.millis(50), gamePiece);
			wobble.setFromX(1.0);
			wobble.setFromY(1.0);
			wobble.setToX(1.2);
			wobble.setToY(1.2);
			wobble.setCycleCount(10);
			wobble.setAutoReverse(true);

			// Color pulsing effect
			FillTransition colorTransition = new FillTransition(Duration.millis(100), gamePiece);
			colorTransition.setFromValue(loserColor);
			colorTransition.setToValue(loserColor.darker());
			colorTransition.setCycleCount(5);
			colorTransition.setAutoReverse(true);
			
			// Grow effect
			ScaleTransition grow = new ScaleTransition(Duration.millis(500), gamePiece);
			grow.setFromX(1.0);
			grow.setFromY(1.0);
			grow.setToX(1.5);
			grow.setToY(1.5);

			// Fade Transition
			FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), gamePiece);
			fadeTransition.setFromValue(1.0);
			fadeTransition.setToValue(0);

			SequentialTransition sequentialTransition =
					new SequentialTransition(
							new ParallelTransition(wobble, colorTransition),
							new ParallelTransition(grow, fadeTransition)
					);

			finalTransition.getChildren().add(sequentialTransition);
		}

		finalTransition.play();

		finalTransition.setOnFinished(_ -> animateGameOverMessage(winnerColor));

	}

	public void animateGameOverMessage(Color winnerColor) {
		int d = (game.getOpponentPlayer().getColor() == 'R') ? -1 : 1;
		Label winner = new Label("");
		int middle = (this.gameSize-1)/2;
		double gameSpaceSize = this.getGameSpace(0, 0).getWidth();

		if (winnerColor==red ||winnerColor==blue)
			winner.setText(getColorName(winnerColor)+"\n"+"Wins");
		else {
			winner.setText("Draw");
		}

		winner.setStyle(
				"-fx-font-size: 14px; " +
				"-fx-font-weight: bold; " +
				"-fx-text-alignment: center;" +
				"-fx-text-fill: " +
				toRGBCode(winnerColor) + ";"
		);

		this.getGameSpace(middle, middle).getChildren().add(winner);
		winner.setVisible(false);

		winner.requestLayout();

		Platform.runLater(() -> {
			winner.setLayoutX((gameSpaceSize - winner.getWidth()) / 2);
			winner.setLayoutY((gameSpaceSize - winner.getHeight()) / 2);
		});

		PauseTransition textPause = new PauseTransition(Duration.millis(0));
		textPause.setOnFinished(_ -> {
			winner.setVisible(true);
			gameStatus.setTextFill(winnerColor.darker());
		});

		TranslateTransition moveText = new TranslateTransition(Duration.millis(500), winner);
		moveText.setFromX(d*sceneSize);
		moveText.setToX(0);
		moveText.setInterpolator(Interpolator.EASE_OUT);


		ScaleTransition growText = new ScaleTransition(Duration.millis(1000), winner);
		growText.setFromX(1.0);
		growText.setFromY(1.0);
		growText.setToX(2.0);
		growText.setToY(2.0);

		SequentialTransition sequentialTransition = new SequentialTransition(textPause,
				new ParallelTransition(moveText,growText, animateGlowEffect(winnerColor))
		);

		sequentialTransition.play();

		sequentialTransition.setOnFinished(_ -> {
			this.setRunningAnimation(false);
//			this.autoRestart(winnerColor, winner);
		});

	}

	public void tiedGame() {
		Color color1 = this.red;
		Color color2 = this.blue;
		double red = ((color1.getRed() + color2.getRed()) / 2);
		double green = ((color1.getGreen() + color2.getGreen()) / 2);
		double blue = ((color1.getBlue() + color2.getBlue()) / 2);

		animateGameOverMessage(new Color(red, green, blue, 1));
	}

	private void autoRestart(Color winnerColor, Label text) {
		SequentialTransition countdownSequence = new SequentialTransition();

		PauseTransition startPause = new PauseTransition(Duration.seconds(0));
		startPause.setOnFinished(_ -> {
			text.setStyle("-fx-font-size: 7px;"+
					"-fx-font-weight: bold; " +
					"-fx-text-alignment: center;" +
					"-fx-text-fill: " +
					toRGBCode(winnerColor) + ";");
					text.setText("Restarting");
		});

		startPause.play();

		for (int i = 5; i >= 0; i--) {
			StringBuilder dots = new StringBuilder();
			dots.append(" .".repeat(Math.max(0, i + 1)));

			PauseTransition countdownPause = new PauseTransition(Duration.seconds(1));
			countdownPause.setOnFinished(_ -> {
				text.setText(String.valueOf(dots));
			});

			countdownSequence.getChildren().add(countdownPause);
		}

		countdownSequence.play();
		countdownSequence.setOnFinished(_->restartGame(gameMenu));
	}

	private SequentialTransition animateGlowEffect(Color winnerColor) {
		ParallelTransition finalTransition = new ParallelTransition();
		Pane[] glows = new Pane[]{redPanel.getGlow(),bluePanel.getGlow()};

		PauseTransition pause = new PauseTransition(Duration.millis(0));
		pause.setOnFinished(_ -> {
			redPanel.winnerGlow(winnerColor);
			bluePanel.winnerGlow(winnerColor);
		});

//		finalTransition.getChildren().add(pause);

		for (Pane g : glows){
			// Create the FadeTransition to fade in the shadow
			FadeTransition fade = new FadeTransition(Duration.millis(1000), g);
			fade.setFromValue(0); // Start fully transparent
			fade.setToValue(1); // Fade to fully opaque
			fade.setInterpolator(Interpolator.EASE_IN);
			finalTransition.getChildren().add(fade);
		}

		return new SequentialTransition(pause, finalTransition);

	}

	private static String getColorName(Color c) {
		for (Field field : Color.class.getFields()) {
			if (field.getType().equals(Color.class)) {
				try {
					if (field.get(null).equals(c)) {
						String s = field.getName();
						return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
					}
				} catch (IllegalAccessException e) {
				}
			}
		}
		return "Unknown Color";
	}

	private String toRGBCode(Color color) {
		return String.format("#%02X%02X%02X",
				(int) (color.getRed() * 255),
				(int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
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

	public void changeTurnPlayerPanel() {
		this.turnPlayerPanel = (this.turnPlayerPanel.getPlayerColor() == red) ? this.bluePanel : this.redPanel;
		this.oppPlayerPanel = (this.oppPlayerPanel.getPlayerColor() == blue) ? this.redPanel : this.bluePanel;
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

	public boolean isAnimateOff() {
		return animateOff;
	}

	public void setAnimateOff(boolean animateOff) {
		this.animateOff = animateOff;
	}
}