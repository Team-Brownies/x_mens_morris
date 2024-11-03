package sprint2.product;

import java.util.List;

public class NMMGame {

	public enum Cell {
		INVALID, EMPTY, BLUE, RED, MOVEVALID
	}
	public enum GameMode {
		NINE, FIVE
	}
	public enum GameState {
		PLACING, MOVING, MILLING, FLYING, GAMEOVER
	}
	private Cell[][] grid;
	private GameState currentGamestate;
	private Player turnPlayer;
	private int size;
	private Player redPlayer;
	private Player bluePlayer;
	private GameMode gameMode;

	public NMMGame(int gameMode) {
		int pieces;
		this.gameMode = (gameMode == 9) ? GameMode.NINE : GameMode.FIVE;
		if (this.gameMode == GameMode.NINE){
			pieces = 9;
			this.size = 7;
		} else {
			pieces = 5;
			this.size = 5;
		}
		grid = new Cell[this.size][this.size];
		setValid();
		this.redPlayer = new Player('R',pieces);
		this.bluePlayer = new Player('B',pieces);
		this.turnPlayer = this.redPlayer;
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

	public Player getTurnPlayer() {
		return this.turnPlayer;
	}

	public void setTurnPlayer(Player turnPlayer) {
		this.turnPlayer = turnPlayer;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean placePiece(int row, int col) {
		if (getCell(row, col)== Cell.EMPTY||getCell(row,col)==Cell.MOVEVALID){
			if (this.turnPlayer.getColor() == 'R')
				this.grid[row][col] = Cell.RED;
			else
				this.grid[row][col] = Cell.BLUE;
			this.turnPlayer.getGamePiece(row, col);
			this.changeTurn();
			this.updateGameState();
			System.out.println(canPlayerMovePiece());
			if (!canPlayerMovePiece()){
				//turnPlayer loses
				setCurrentGamestate(NMMGame.GameState.GAMEOVER);
			}
			return true;
		}
		return false;
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
		cell = (this.currentGamestate == NMMGame.GameState.MOVING) ? NMMGame.Cell.MOVEVALID : Cell.EMPTY;
		return cell;
	}

	public void movePiece(int row, int col, int movingRow, int movingCol) {
		if (placePiece(row,col)) {
			this.grid[movingRow][movingCol] = Cell.EMPTY;
			clearHighlightCells();
		}
	}

	public boolean canPlayerMovePiece(){
		List<GamePiece> pieces = this.turnPlayer.getBoradPieces();
		System.out.println(this.turnPlayer.getColor());
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
				System.out.println("("+row+", "+col+") to ("+ud_mag+", "+rl_mag+")");
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
		System.out.println(this.currentGamestate);
		System.out.println(this.turnPlayer.totalNumberOfPieces());
		if (this.redPlayer.numberOfGamePieces() == 0 && this.bluePlayer.numberOfGamePieces() == 0)
			this.currentGamestate = GameState.MOVING;
		if (this.turnPlayer.totalNumberOfPieces() <= 3 && this.currentGamestate == GameState.MOVING) {
			this.currentGamestate = GameState.FLYING;
		}
	}

	public void changeTurn() {
		this.turnPlayer = (this.turnPlayer.getColor() == 'R') ? this.bluePlayer : this.redPlayer;
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

	public GameMode getGameMode() {
		return gameMode;
	}
}
