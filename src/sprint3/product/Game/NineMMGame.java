package sprint3.product.Game;

import sprint3.product.Player.Player;

public class NineMMGame extends Game {

	private static final int pieces = 9;
	private static final int size = 7;
	private static final GameMode gameMode = GameMode.NINE;

	public NineMMGame() {
		super(pieces, size, gameMode);
	}

	@Override
	public void updateGameStatePerPlayer(Player player){
		if (player.numberOfGamePieces() > 0)
			player.setPlayersGamestate(GameState.PLACING);
		if (player.numberOfGamePieces() <= 0 )
			player.setPlayersGamestate(GameState.MOVING);
		if (player.totalNumberOfPieces() <= 3 && player.numberOfGamePieces()==0) {
			player.setPlayersGamestate(GameState.FLYING);
			player.setGamePiecesToFlying();
		}
	}
}
