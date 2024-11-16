package sprint3.product.Game;

import sprint3.product.Player.Player;

public class SixMMGame extends Game {

	private static final int pieces = 6;
	private static final int size = 5;

	public SixMMGame() {
		super(pieces, size);
	}

	// Flying is not used in Six Man's Morris
	public void updateGameState(){
		Player turnPlayer = this.getTurnPlayer();
		if (turnPlayer.numberOfGamePieces() == 0 )
			turnPlayer.setPlayersGamestate(GameState.MOVING);
		if (turnPlayer.numberOfGamePieces() != 0)
			turnPlayer.setPlayersGamestate(GameState.PLACING);
	}
}
