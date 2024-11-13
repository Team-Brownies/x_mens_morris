package sprint3.product.Game;

import sprint3.product.Player.Player;

public class NineMMGame extends Game {

	private static final int PIECES = 9;
	private static final int SIZE = 7;

	public NineMMGame() {
		super(PIECES, SIZE);
	}

	@Override
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
}
