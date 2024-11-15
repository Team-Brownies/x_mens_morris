package sprint3.product.Game;

import sprint3.product.Cell;
import sprint3.product.CheckMill;
import sprint3.product.GUI.Board;
import sprint3.product.Player.CPUPlayer;
import sprint3.product.Player.HumanPlayer;
import sprint3.product.Player.Player;

import java.util.*;

public abstract class Game {
	private Cell[][] grid;
	private final int size;
	private Player redPlayer;
	private Player bluePlayer;
	private Player turnPlayer;
	private Player opponentPlayer;
	private Board gui;

	public Game(int pieces, int size) {
		this.size = size;
		this.grid = new Cell[this.size][this.size];
		setValid();
		this.redPlayer = new CPUPlayer('R',pieces, this);
		this.bluePlayer = new HumanPlayer('B',pieces, this);
		this.turnPlayer = this.redPlayer;
		this.opponentPlayer = this.bluePlayer;
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
//			//left
//			checkAdjacentValid(1, 0, coords);
//			//right
//			checkAdjacentValid(-1, 0, coords);
//			//up
//			checkAdjacentValid(0, 1, coords);
//			//down
//			checkAdjacentValid(0, -1, coords);
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
	public Cell movingOrFlying(){
		Cell cell;
		cell = (this.turnPlayer.getPlayersGamestate() == GameState.MOVING) ? Cell.MOVEVALID : Cell.EMPTY;
		return cell;
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
	public void clearMoveValids(){
		for (int row = 0; row < this.size; ++row)
			for (int col = 0; col < this.size; ++col)
				if(getCell(row, col) == Cell.MOVEVALID)
					this.grid[row][col] = Cell.EMPTY;
	}

	// update a player game state based on  the board pieces
	public abstract void updateGameState();

	// changes turn to next player and updates
	public void changeTurn() {
		this.turnPlayer = (this.turnPlayer.getColor() == 'R') ? this.bluePlayer : this.redPlayer;
		this.opponentPlayer = (this.opponentPlayer.getColor() == 'B') ? this.redPlayer : this.bluePlayer;

		gui.changeTurnPlayerPanel();

		this.updateGameState();
		this.gameOver();
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
	private void gameOver() {
		if(this.turnPlayer.getPlayersGamestate() == GameState.MOVING || this.turnPlayer.getPlayersGamestate() == GameState.FLYING) {
			GameState state;
			if(this.turnPlayer.getColor() == 'R'){
				state = GameState.BLUE_WON;
			}
			else{
				state = GameState.RED_WON;
			}
			if (this.turnPlayer.numberOfBoardPieces() < 3) {
				this.turnPlayer.setPlayersGamestate(state);
				this.opponentPlayer.setPlayersGamestate(state);
				if (this.turnPlayer.getColor() == 'R') {
					System.out.println("Blue player won");
				} else {
					System.out.println("Red player won");
				}
			} else {
				if(!this.turnPlayer.canPiecesMove()) {
					this.turnPlayer.setPlayersGamestate(state);
					this.opponentPlayer.setPlayersGamestate(state);
					if (this.turnPlayer.getColor() == 'R') {
						System.out.println("Blue player won");
					} else {
						System.out.println("Red player won");
					}
				}
			}
		}
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
		if(millChecker.checkMillAllDirections(row, col)){
			millMates.add(new int[]{row,col});
			millMates.addAll(millChecker.getMillMates(row, col));
			sortedMillMates = millChecker.sortMillMatesBySharedPosition(millMates);

			gui.animateMillForm(() -> {
				// don't change the turn if the player has formed the mill
				// switch to mill state after animation plays
				this.turnPlayer.setPlayersGamestate(GameState.MILLING);
				gui.updateGameStatus();
				this.letCPUMove();
			},sortedMillMates);
		}
		else{
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
}
