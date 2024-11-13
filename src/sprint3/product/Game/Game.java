package sprint3.product.Game;

import sprint3.product.Cell;
import sprint3.product.CheckMill;
import sprint3.product.GUI.Board;
import sprint3.product.Player.CPUPlayer;
import sprint3.product.Player.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class Game {
	private Cell[][] grid;
	private int size;
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
		this.bluePlayer = new CPUPlayer('B',pieces, this);
		this.turnPlayer = this.redPlayer;
		this.opponentPlayer = this.bluePlayer;
	}

	public void setValid() {
		int middle = (this.size-1)/2;
		for (int row = 0; row < this.size; ++row) {
			for (int col = 0; col < this.size; ++col) {
				//Makes diagonal cells and the middle rows and columns valid spaces
				if (row == col || row == (this.size-1-col) || row == middle || col == middle)
					this.grid[row][col] = Cell.EMPTY;
				else
					this.grid[row][col] = Cell.INVALID;
				//Makes middle cell invalid
				if (row == middle && col == middle)
					this.grid[row][col] = null;
			}
		}
	}

	public Cell getCell(int row, int column) {
		if (row >= 0 && row < this.size && column >= 0 && column < this.size)
			return this.grid[row][column];
		else
			return null;
	}

	public Cell[][] getGrid() {
		return grid;
	}

	public void setGrid(int row, int col, Cell cellColor) {
		this.grid[row][col] = cellColor;
	}

	public Player getTurnPlayer() {
		return this.turnPlayer;
	}

	public Player getOpponentPlayer() {
		return opponentPlayer;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void findAdjacentCells(int[] coords){
		//left
		checkAdjacentValid(1,0, coords);
		//right
		checkAdjacentValid(-1,0, coords);
		//up
		checkAdjacentValid(0,1, coords);
		//down
		checkAdjacentValid(0,-1, coords);
	}

	private void checkAdjacentValid(int rl, int ud, int[] coords){
		int rl_mag, ud_mag;
		int row = coords[0];
		int col = coords[1];

		for (int i = 1; i <= 3; i++) {
			rl_mag = col+(i*rl);
			ud_mag = row+(i*ud);
			if(getCell(ud_mag, rl_mag) == Cell.EMPTY) {
				this.grid[ud_mag][rl_mag] = Cell.MOVEVALID;
				break;
			}
			else if (getCell(ud_mag, rl_mag) != Cell.INVALID)
				break;
		}
	}

	public Cell movingOrFlying(){
		Cell cell;
		cell = (this.turnPlayer.getPlayersGamestate() == GameState.MOVING) ? Cell.MOVEVALID : Cell.EMPTY;
		return cell;
	}

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

	public void clearHighlightCells(){
		for (int row = 0; row < this.size; ++row)
			for (int col = 0; col < this.size; ++col)
				if(getCell(row, col) == Cell.MOVEVALID)
					this.grid[row][col] = Cell.EMPTY;
	}

	public void updateGameState(){
		Player turnPlayer = this.getTurnPlayer();
		if (turnPlayer.numberOfGamePieces() > 0)
			turnPlayer.setPlayersGamestate(GameState.PLACING);
		if (turnPlayer.numberOfGamePieces() <= 0 )
			turnPlayer.setPlayersGamestate(GameState.MOVING);
		if (turnPlayer.totalNumberOfPieces() <= 3) {
			turnPlayer.setPlayersGamestate(GameState.FLYING);
		}
	}

	public void changeTurn() {
		this.turnPlayer = (this.turnPlayer.getColor() == 'R') ? this.bluePlayer : this.redPlayer;
		this.opponentPlayer = (this.opponentPlayer.getColor() == 'B') ? this.redPlayer : this.bluePlayer;


		this.updateGameState();
		this.gameOver();
		this.letCPUMove();
	}

	public void letCPUMove(){
		if(this.turnPlayer.isCPU()){
			this.turnPlayer.makeCPUMove();
		}
	}

	public Player getBluePlayer() {
		return bluePlayer;
	}

	public Player getRedPlayer() {
		return redPlayer;
	}

	public Board getGui() {
		return gui;
	}

	public void setGui(Board gui) {
		this.gui = gui;
	}

	public void gameOver() {

		this.redPlayer.updatePieces();
		this.bluePlayer.updatePieces();

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

	public boolean canPlacePiece(int row, int col) {
		return this.getCell(row, col)== Cell.EMPTY||this.getCell(row,col)== Cell.MOVEVALID;
	}

	public void checkMill(int row, int col) {
		CheckMill millChecker = new CheckMill(this.getGrid());
		if(millChecker.checkMillAllDirections(row, col)){
			this.turnPlayer.setPlayersGamestate(GameState.MILLING);
			this.letCPUMove();
		}
		else{
			this.changeTurn();
		}
	}

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
