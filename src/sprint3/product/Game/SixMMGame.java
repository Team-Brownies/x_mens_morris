package sprint3.product.Game;

import sprint3.product.Player.Player;

public class SixMMGame extends Game {

	private static final int PIECES = 6;
	private static final int SIZE = 5;
	private static final GameMode GAME_MODE = GameMode.SIX;

	public SixMMGame() {
		super(PIECES, SIZE, GAME_MODE);
	}

	// Flying is not used in Six Man's Morris
	public void updateGameStatePerPlayer(Player player){
		if (player.numberOfGamePieces() == 0 ) {
			player.setPlayersGamestate(GameState.MOVING);
		}
		if (player.numberOfGamePieces() != 0) {
			player.setPlayersGamestate(GameState.PLACING);
		}
	}
}