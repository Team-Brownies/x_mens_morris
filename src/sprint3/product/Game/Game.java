package sprint3.product.Game;

import sprint3.product.Cell;
import sprint3.product.CheckMill;
import sprint3.product.GUI.Board;
import sprint3.product.Player.Player;

import java.util.*;

public abstract class Game {
	private int pieces;
	private Cell[][] grid;
	private final int size;
	private Player redPlayer;
	private Player bluePlayer;
	private Player turnPlayer;
	private Player opponentPlayer;
	private Board gui;
	private GameHistory gameHistory = new GameHistory();
	private boolean endingGame;
	private boolean deletedGame;

	public Game(int pieces, int size, GameMode gameMode) {
		this.size = size;
		this.pieces = pieces;
        this.grid = new Cell[this.size][this.size];
		this.endingGame=false;
		this.deletedGame=false;
		gameHistory.setGameMode(gameMode);
		setValid();
	}

	public void setRedPlayer(Player redPlayer) {
		this.redPlayer = redPlayer;
		this.turnPlayer = this.redPlayer;
	}

	public void setBluePlayer(Player bluePlayer) {
		this.bluePlayer = bluePlayer;
		this.opponentPlayer = this.bluePlayer;
	}

	public int getPieces() {
		return pieces;
	}

	// Mark gameSpaces as INVALID and EMPTY based on there position
	public void setValid() {
		int middle = (this.size-1)/2;
		for (int row = 0; row < this.size; ++row) {
			for (int col = 0; col < this.size; ++col) {
				//Makes diagonal cells and the middle rows and columns valid spaces
				if (row == col || row == (this.size-1-col) || row == middle || col == middle)
					this.grid[row][col] = Cell.EMPTY;
				else
					this.grid[row][col] = Cell.INVALID;
				//Makes middle cell null
				if (row == middle && col == middle)
					this.grid[row][col] = null;
			}
		}
	}

	// Returns the Cell class for the gameSpace on the grid based on position
	public Cell getCell(int row, int column) {
		if (row >= 0 && row < this.size && column >= 0 && column < this.size)
			return this.grid[row][column];
		else
			return null;
	}

	// Returns grid
	public Cell[][] getGrid() {
		return grid;
	}

	// Sets space on grid to a different Cell
	public void setGrid(int row, int col, Cell cellColor) {
		this.grid[row][col] = cellColor;
	}

	// Returns the player who is currently making a move
	public Player getTurnPlayer() {
		return this.turnPlayer;
	}

	// Returns the opponent of the player who is currently making a move
	public Player getOpponentPlayer() {
		return opponentPlayer;
	}

	// Returns the board size
	public int getSize() {
		return this.size;
	}

	// Finds the game spaces that are adjacent to game space with given coords
	public void findAdjacentCells(int[] coords){
		int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

		for (int[] dir : directions) {
			checkAdjacentValid(dir[0], dir[1], coords);
		}
	}

	// Find the game space that is adjacent to game space in a given direction
	private void checkAdjacentValid(int xDirection, int yDirection, int[] coords){
		int xMagnitude, yMagnitude;
		int row = coords[0];
		int col = coords[1];

		for (int i = 1; i <= 3; i++) {
			xMagnitude = col+(i*xDirection);
			yMagnitude = row+(i*yDirection);
			if(getCell(yMagnitude, xMagnitude) == Cell.EMPTY) {
				this.grid[yMagnitude][xMagnitude] = Cell.MOVEVALID;
				break;
			}
			else if (getCell(yMagnitude, xMagnitude) != Cell.INVALID)
				break;
		}
	}

	// Gets cell name for make a move in a moving or flying state
	public boolean movingOrFlying(int row, int col){
        if (Objects.requireNonNull(this.turnPlayer.getPlayersGamestate()) == GameState.FLYING) {
            if (this.getCell(row, col) == Cell.EMPTY)
                return true;
        }
        return this.getCell(row, col) == Cell.MOVEVALID;
    }

	// Checks to see if Opponent has any pieces not in mills before letting the player remove a piece in a mill
	public boolean getOppFreePieces(){
		CheckMill millChecker = new CheckMill(this.grid);
		for(int i = 0; i < this.grid.length; i++){
			for(int j = 0; j < this.grid.length; j++){
				if(getCell(i, j) == turnPlayer.getOpponentTag()){
					if(!millChecker.checkMillAllDirections(i, j)){
						return true;
					}
				}
			}
		}
		return false;
	}
	// gets list of game spaces with given cell type
	public List<int[]> getAllCellsOfAType(Cell cellType){
		Cell[][] grid = this.getGrid();
		List<int[]> cells = new ArrayList<>();

		for (int row = 0; row < this.size; row++) {
			for (int col = 0; col < this.size; col++) {
				if (grid[row][col] == cellType) {
					cells.add(new int[]{row, col});
				}
			}
		}
		return cells;
	}
	// Updated the game spaces to check for adjacent game spaces
	public void clearMoveValid(){
		for (int row = 0; row < this.size; ++row)
			for (int col = 0; col < this.size; ++col)
				if(getCell(row, col) == Cell.MOVEVALID)
					this.grid[row][col] = Cell.EMPTY;
	}

	// update a player game state based on  the board pieces
	public abstract void updateGameStatePerPlayer(Player player);

	private void updateGameState(){
		updateGameStatePerPlayer(redPlayer);
		updateGameStatePerPlayer(bluePlayer);
	}

	// changes turn to next player and updates
	public void changeTurn() {
		if (this.isEndingGame()) {
			deleteGame();
			this.deletedGame=true;
			return; // Exit early if the game has ended
		}
		this.turnPlayer = (this.turnPlayer.getColor() == 'R') ? this.bluePlayer : this.redPlayer;
		this.opponentPlayer = (this.opponentPlayer.getColor() == 'B') ? this.redPlayer : this.bluePlayer;

		this.updateGameState();

		if (gui!=null) {
			gui.changeTurnPlayerPanel();
			gui.updateGameStatus();
		}

		this.checkForGameOver();
		this.letCPUMove();
	}

	// method to allow the CPU players to make there moves
	public void letCPUMove(){
		if(this.turnPlayer.isCPU()){
			this.turnPlayer.makeCPUMove();
		}
	}

	// return the red player (player one)
	public Player getRedPlayer() {
		return redPlayer;
	}

	// return the blue player (player two)
	public Player getBluePlayer() {
		return bluePlayer;
	}

	// return the gui the game object is using
	public Board getGui() {
		return gui;
	}

	// set the GUI
	public void setGui(Board gui) {
		this.gui = gui;
	}

	// check to see if the game has ended
	public void checkForGameOver() {
		GameState state = this.turnPlayer.getPlayersGamestate();
		if(state == GameState.MOVING || state == GameState.FLYING) {
			if (this.turnPlayer.totalNumberOfPieces() < 3 || !this.turnPlayer.canPiecesMove()) {
				gameOver(GameState.GAMEOVER);
			}
			else if (this.turnPlayer.totalNumberOfPieces()==3
					&& this.opponentPlayer.totalNumberOfPieces()==3
				    && !this.turnPlayer.canWinThisTurn()
			){
				System.out.println(this.turnPlayer.getColor());
				System.out.println("draw");
				gameOver(GameState.DRAW);
			}
		}
	}

	private void gameOver(GameState state){
		this.turnPlayer.setPlayersGamestate(state);
		this.opponentPlayer.setPlayersGamestate(state);
		if(gui!=null){
			switch (state){
				case GAMEOVER -> gui.animateGameOver(this.turnPlayer.getBoardPiecesCoords());
				case DRAW -> gui.tiedGame();
			}
		} else {
			System.out.println(switch (state){
				case GAMEOVER -> ((this.turnPlayer.getColor() == 'R') ? "Blue":"Red")+" player won!";
				case DRAW -> "Game is a Draw";
				default -> "";
			});
		}
		System.out.println(grid[4][3]);
		gameHistory.writeMoves();
	}


	// check to see if a game piece can be placed based on location of board space
	public boolean canPlacePiece(int row, int col) {
		return this.getCell(row, col)== Cell.EMPTY||this.getCell(row,col)== Cell.MOVEVALID;
	}

	// check to see if a mill was formed before changing turns
	public void checkMill(int row, int col) {
			CheckMill millChecker = new CheckMill(this.getGrid());
			Set<int[]> millMates = new HashSet<>();
			List<int[]> sortedMillMates;
			if (millChecker.checkMillAllDirections(row, col)) {
				millMates.add(new int[]{row, col});
				millMates.addAll(millChecker.getMillMates(row, col));
				sortedMillMates = millChecker.sortMillMatesBySharedPosition(millMates);

				if (gui != null) {
					gui.animateMillForm(() -> {
						// don't change the turn if the player has formed the mill
						// switch to mill state after animation plays
						this.turnPlayer.setPlayersGamestate(GameState.MILLING);
						gui.updateGameStatus();
						this.letCPUMove();
					}, sortedMillMates);
				} else {
					this.turnPlayer.setPlayersGamestate(GameState.MILLING);
					this.letCPUMove();
				}
			} else {
				this.changeTurn();
			}
	}

	// returns a list of cell coords with a given cellType
	public List<int[]> getCellsByCellType(Cell cellType){
		Cell[][] grid = this.getGrid();
		List<int[]> cells = new ArrayList<>();
		for (int row = 0; row < this.size; row++) {
			for (int col = 0; col < this.size; col++) {
				if (grid[row][col] == cellType) {
					cells.add(new int[]{row, col});
				}
			}
		}
		return cells;
	}

    public GameHistory getGameHistory() {
        return gameHistory;
    }

	public void endGame() {
		this.endingGame=true;
	}
	private void deleteGame(){
		// Perform cleanup of resources
		this.grid = null;           // Release the grid
		this.redPlayer = null;      // Release player references
		this.bluePlayer = null;
		this.turnPlayer = null;
		this.opponentPlayer = null;
		this.gui = null;            // Release GUI reference
		this.gameHistory = null;    // Release game history

		System.out.println("Game resources have been cleaned up. Exiting the game...");
		System.out.println("Game has ended.");

		// Suggest garbage collection
		System.gc();
	}

	private boolean isEndingGame() {
		return endingGame;
	}

	public boolean isDeletedGame() {
		return deletedGame;
	}

	// make a temp grid to test outcome of a possible move
	public Cell[][] makeTempGrid(){
		Cell[][] grid = this.getGrid();
		return Arrays.stream(grid)
				.map(Cell[]::clone)
				.toArray(Cell[][]::new);
	}
}