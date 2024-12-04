package sprint3.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sprint3.product.Cell;
import sprint3.product.Game.Game;
import sprint3.product.Game.GameState;
import sprint3.product.Game.NineMMGame;
import sprint3.product.Player.HumanPlayer;

import static org.junit.Assert.*;

public class TestRemovePiece {

	private Game board;

	@Before
	public void setUp() throws Exception {
		board = new NineMMGame();

		board.setRedPlayer(new HumanPlayer('R', board));
		board.setBluePlayer(new HumanPlayer('B', board));

		board.getTurnPlayer().placePiece(0, 0); //red
		board.getTurnPlayer().placePiece(3, 1); //blue
		board.getTurnPlayer().placePiece(0, 3); //red
		board.getTurnPlayer().placePiece(1, 1); //blue
		board.getTurnPlayer().placePiece(0, 6); //red

	}

	@After
	public void tearDown() throws Exception {
	}

	// acceptance criterion 6.1
	@Test
	public void testRemoveOpponentPiece() {
		// red turn
		assertEquals("", board.getTurnPlayer().getColor(), 'R');
		// milling
		assertEquals("", board.getTurnPlayer().getPlayersGamestate(), GameState.MILLING);
		// 1,1 is blue
		assertEquals("", board.getCell(1, 1), Cell.BLUE);
		board.getTurnPlayer().removePiece(1, 1); //remove blue
		// 1,1 is now empty
		assertEquals("", board.getCell(1, 1), Cell.EMPTY);
		// now blue's turn
		assertEquals("", board.getTurnPlayer().getColor(), 'B');
	}

	// acceptance criterion 6.2
	@Test
	public void testRemoveOpponentMillPiece() {

		board.getTurnPlayer().removePiece(1, 1); //remove blue

		board.getTurnPlayer().placePiece(5, 1); //blue
		board.getTurnPlayer().placePiece(6, 6); //red
		board.getTurnPlayer().placePiece(1, 1); //blue

		// blue turn
		assertEquals("", board.getTurnPlayer().getColor(), 'B');
		// milling
		assertEquals("", board.getTurnPlayer().getPlayersGamestate(), GameState.MILLING);

		Cell beforeCell = board.getCell(0, 0);
		// 0,0 is opponent's
		assertEquals("", beforeCell, Cell.RED);

		board.getTurnPlayer().removePiece(0, 0); //remove cell

		// 0,0 is now the same as before
		assertEquals("", board.getCell(0, 0), beforeCell);
		// still milling
		assertEquals("", board.getTurnPlayer().getPlayersGamestate(), GameState.MILLING);
		// turn remains red
		assertEquals("", board.getTurnPlayer().getColor(), 'B');
	}
}
