package sprint3.product.Game;

public class SixMMGame extends Game {

	private static final int PIECES = 6;
	private static final int SIZE = 5;

	public SixMMGame() {
		super(PIECES, SIZE);
	}

	public void updateGameState(){
		if (this.getRedPlayer().numberOfGamePieces() == 0 && this.getBluePlayer().numberOfGamePieces() == 0)
			this.setCurrentGamestate(GameState.MOVING);
		if (this.getRedPlayer().numberOfGamePieces() != 0 && this.getBluePlayer().numberOfGamePieces() != 0)
			this.setCurrentGamestate(GameState.PLACING);
	}
}
