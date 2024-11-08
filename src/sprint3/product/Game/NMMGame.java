package sprint3.product.Game;

import sprint3.product.Cell;
import sprint3.product.CheckMill;
import sprint3.product.GamePiece;
import sprint3.product.Player.Player;

import java.util.List;

public class NMMGame extends Game {

	private static final int PIECES = 9;
	private static final int SIZE = 7;

	public NMMGame() {
		super(PIECES, SIZE);
	}
}
