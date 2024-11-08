package sprint3.product.Game;

import sprint3.product.Cell;
import sprint3.product.CheckMill;
import sprint3.product.GamePiece;
import sprint3.product.Player.CPUPlayer;
import sprint3.product.Player.HumanPlayer;
import sprint3.product.Player.Player;

import java.util.List;

public abstract class Game {
	public enum GameState {
		PLACING, MOVING, MILLING, FLYING, GAMEOVER, RED_WON, BLUE_WON
	}
	private Cell[][] grid;
	private GameState currentGamestate;
	private int size;
	private Player redPlayer;
	private Player bluePlayer;
	private Player turnPlayer;
	private Player opponentPlayer;

	public Game(int pieces, int size) {
		this.size = size;
		this.grid = new Cell[this.size][this.size];
		setValid();
		this.redPlayer = new HumanPlayer('R',pieces, this);
		this.bluePlayer = new HumanPlayer('B',pieces, this);
		this.turnPlayer = this.redPlayer;
		this.opponentPlayer = this.bluePlayer;
		this.currentGamestate = GameState.PLACING;
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

	public void setTurnPlayer(Player turnPlayer) {
		this.turnPlayer = turnPlayer;
	}

	public Player getOpponentPlayer() {
		return opponentPlayer;
	}

	public void setOpponentPlayer(Player opponentPlayer) {
		this.opponentPlayer = opponentPlayer;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void findAdjacentCells(int row, int col){
		//left
		checkAdjacentVaild(1,0, row, col);
		//right
		checkAdjacentVaild(-1,0, row, col);
		//up
		checkAdjacentVaild(0,1, row, col);
		//down
		checkAdjacentVaild(0,-1, row, col);
	}

	private void checkAdjacentVaild(int rl, int ud, int row, int col){
		int rl_mag, ud_mag;
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
		cell = (this.currentGamestate == GameState.MOVING) ? Cell.MOVEVALID : Cell.EMPTY;
		return cell;
	}

	public boolean getOppFreePieces(){
		CheckMill millChecker = new CheckMill(this.grid);
		for(int i = 0; i < this.grid.length; i++){
			for(int j = 0; j < this.grid.length; j++){
				if(getCell(i, j) == turnPlayer.getOpponentTag()){
					if(!millChecker.checkMillAllDireactions(i, j)){
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean canPlayerMovePiece(){
		List<GamePiece> pieces = this.turnPlayer.getBoardPieces();
		if (this.turnPlayer.getGamePieces().isEmpty()) {
			for (GamePiece p : pieces) {
				p.getLocation();
				if (hasAdjacentVailds(1, 0, p.getLocation()) ||
						hasAdjacentVailds(-1, 0, p.getLocation()) ||
						hasAdjacentVailds(0, 1, p.getLocation()) ||
						hasAdjacentVailds(0, -1, p.getLocation()))
					return true;
			}
		} else {
			return true;
		}
		return false;
	}

	private boolean hasAdjacentVailds(int rl, int ud, int[] location){
		int rl_mag, ud_mag;
		int row = location[0];
		int col = location[1];

		for (int i = 1; i <= 3; i++) {
			rl_mag = col+(i*rl);
			ud_mag = row+(i*ud);
			if(getCell(ud_mag, rl_mag) == Cell.EMPTY) {
				return true;
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
		if (this.redPlayer.numberOfGamePieces() == 0 && this.bluePlayer.numberOfGamePieces() == 0)
			this.currentGamestate = GameState.MOVING;
		if (this.redPlayer.numberOfGamePieces() != 0 && this.bluePlayer.numberOfGamePieces() != 0)
			this.currentGamestate = GameState.PLACING;
		System.out.println(opponentPlayer.getColor());
		if (this.opponentPlayer.numberOfBoardPieces() <= 3 && this.currentGamestate == GameState.MOVING) {
			this.currentGamestate = GameState.FLYING;
		}
	}

	public void changeTurn() {
		this.turnPlayer = (this.turnPlayer.getColor() == 'R') ? this.bluePlayer : this.redPlayer;
		this.opponentPlayer = (this.opponentPlayer.getColor() == 'B') ? this.redPlayer : this.bluePlayer;
		System.out.println(this.turnPlayer.isCPU());
		if(this.turnPlayer.isCPU()){
			int[] loc = this.turnPlayer.genPlace();
			this.turnPlayer.placePiece(loc[0],loc[1]);
		}
	}

	public GameState getCurrentGamestate() {
		return this.currentGamestate;
	}

	public void setCurrentGamestate(GameState currentGamestate) {
		this.currentGamestate = currentGamestate;
	}

	public Player getBluePlayer() {
		return bluePlayer;
	}

	public Player getRedPlayer() {
		return redPlayer;
	}

	public void gameOver() {

		if(currentGamestate == GameState.MOVING || currentGamestate == GameState.FLYING) {
			Cell PlayerColor = Cell.EMPTY;
			if(this.turnPlayer.getColor() == 'R'){
				PlayerColor = Cell.RED;
			}
			else{
				PlayerColor = Cell.BLUE;
			}
			System.out.println(turnPlayer.numberOfBoardPieces());
			System.out.println(this.opponentPlayer.numberOfBoardPieces());
			if (this.opponentPlayer.numberOfBoardPieces() < 3) {
				System.out.println("legalMove");
				System.out.println(LegalMove(PlayerColor));
				System.out.println(turnPlayer.numberOfBoardPieces());
				if (this.turnPlayer.getColor() == 'R') {
					this.currentGamestate = GameState.GAMEOVER;
				} else {
					this.currentGamestate = GameState.GAMEOVER;
					System.out.println("Red player won");
				}
			} else {
				if(!LegalMove(PlayerColor))
					if(this.turnPlayer.getColor() == 'R'){
						this.currentGamestate = GameState.BLUE_WON;
					}
					else{
						this.currentGamestate = GameState.RED_WON;
					}
			}
		}
	}

	private boolean LegalMove(Cell PlayerColor) {
		for(int i = 0; i < this.grid.length; i++){
			for(int j = 0; j < this.grid[i].length; j++){
				if(grid[i][j] == PlayerColor){
					if (checkAdjPos(i, j)) {
						System.out.println("row: "+i+" col:"+j);
						return true; // Found a legal move
					} //check again
				}
			}
		}
		return false;
	}

	private boolean checkAdjPos(int x, int y){
		//  direactions (up, down, left, right)
		int[][] directions = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };

		for (int[] dir : directions) {
			int newX = x + dir[0];
			int newY = y + dir[1];

			// Check if the new position is within bounds and empty
			if (isWithinBounds(newX, newY) && grid[newX][newY] == Cell.EMPTY) {
				return true;
			}
		}
		return false;
	}

	private boolean isWithinBounds(int x, int y) {
		return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;
	}
}
