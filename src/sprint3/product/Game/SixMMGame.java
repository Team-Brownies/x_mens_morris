package sprint3.product.Game;

import sprint3.product.Player.Player;

public class SixMMGame extends Game {

	private static final int pieces = 6;
	private static final int size = 5;

	public SixMMGame() {
		super(pieces, size);
	}

	// Flying is not used in Six Man's Morris
	public void updateGameStatePerPlayer(Player player){
		if (player.numberOfGamePieces() == 0 )
			player.setPlayersGamestate(GameState.MOVING);
		if (player.numberOfGamePieces() != 0)
			player.setPlayersGamestate(GameState.PLACING);
	}
}
