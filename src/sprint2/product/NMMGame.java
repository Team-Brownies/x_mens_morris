package sprint2.product;

import java.util.List;

public class NMMGame {

	public enum GameMode {
		NINE, FIVE
	}
	public enum GameState {
		PLACING, MOVING, MILLING, FLYING, GAMEOVER
	}
	private Cell[][] grid;
	private GameState currentGamestate;
	private Player turnPlayer;
	private Player opponentPlayer;
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
		CheckMill millChecker = new CheckMill(this.grid);
		if (getCell(row, col)== Cell.EMPTY||getCell(row,col)==Cell.MOVEVALID){
			if (this.turnPlayer.getColor() == 'R')
				this.grid[row][col] = Cell.RED;
			else
				this.grid[row][col] = Cell.BLUE;
			this.turnPlayer.getGamePiece(row, col);

			//don't change the turn if the player has formed the mill
			if(millChecker.checkMillAllDireactions(row, col)){
				setCurrentGamestate(GameState.MILLING);
			}
			else{
				this.changeTurn();
				this.updateGameState();
			}
			if (!canPlayerMovePiece()){
				//turnPlayer loses
				setCurrentGamestate(GameState.GAMEOVER);
			}

			this.gameOver();
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
		cell = (this.currentGamestate == GameState.MOVING) ? Cell.MOVEVALID : Cell.EMPTY;
		return cell;
	}
	private Cell getPlayerTag(char playerColor){
		if(playerColor == 'R'){
			return Cell.RED;
		}
		else{
			return Cell.BLUE;
		}
	}
	private Cell getOpponentTag(char playerColor){
		if(playerColor == 'R'){
			return Cell.BLUE;
		}
		else {
			return Cell.RED;
		}
	}
	public boolean getOppFreePieces(){
		CheckMill millChecker = new CheckMill(this.grid);
		for(int i = 0; i < this.grid.length; i++){
			for(int j = 0; j < this.grid.length; j++){
				if(getCell(i, j) == getOpponentTag(turnPlayer.getColor())){
					if(!millChecker.checkMillAllDireactions(i, j)){
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean removePiece(int row, int col) {
		CheckMill millChecker = new CheckMill(this.grid);
		//prevent player from removing their own piece
		if(getCell(row, col) == getPlayerTag(turnPlayer.getColor())){
			return false;
		}
		else{
			//prevent player from removing the pieces in the mill
			if(millChecker.checkMillAllDireactions(row, col) && getOppFreePieces()){
				return false;
			}
			else{
				grid[row][col] = Cell.EMPTY;
				this.opponentPlayer.removeBoradPiece(row, col); //removing the players piece from the pieces list
				this.updateGameState();

				this.gameOver();
				this.changeTurn();
				return true; //player can remove the opp player piece nit in the mill
			}
		}
	}

	public void movePiece(int row, int col, int movingRow, int movingCol) {
		if (placePiece(row,col)) {
			this.grid[movingRow][movingCol] = Cell.EMPTY;
			clearHighlightCells();
		}
	}

	public boolean canPlayerMovePiece(){
		List<GamePiece> pieces = this.turnPlayer.getBoradPieces();
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
		if (this.opponentPlayer.numberOfBoradPieces() <= 3 && this.currentGamestate == GameState.MOVING) {
			this.currentGamestate = GameState.FLYING;
		}
	}

	public void changeTurn() {
		this.turnPlayer = (this.turnPlayer.getColor() == 'R') ? this.bluePlayer : this.redPlayer;
		this.opponentPlayer = (this.opponentPlayer.getColor() == 'B') ? this.redPlayer : this.bluePlayer;
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

	public void gameOver() {

		if(currentGamestate == GameState.MOVING || currentGamestate == GameState.FLYING) {
			Cell PlayerColor = Cell.EMPTY;
			if(this.turnPlayer.getColor() == 'R'){
				PlayerColor = Cell.RED;
			}
			else{
				PlayerColor = Cell.BLUE;
			}
			System.out.println(turnPlayer.numberOfBoradPieces());
			System.out.println(this.opponentPlayer.numberOfBoradPieces());
			if (this.opponentPlayer.numberOfBoradPieces() < 3) {
				System.out.println("legalMove");
				System.out.println(LegalMove(PlayerColor));
				System.out.println(turnPlayer.numberOfBoradPieces());
				if (this.turnPlayer.getColor() == 'R') {
					this.currentGamestate = GameState.GAMEOVER;
				} else {
					this.currentGamestate = GameState.GAMEOVER;
					System.out.println("Red player won");
				}
			} else {
				if(!LegalMove(PlayerColor))
					this.currentGamestate = GameState.GAMEOVER;
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
