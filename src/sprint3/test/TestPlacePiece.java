package sprint3.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sprint3.product.Cell;
import sprint3.product.Game.Game;
import sprint3.product.Game.NineMMGame;
import sprint3.product.Player.HumanPlayer;

import static org.junit.Assert.assertEquals;

public class TestPlacePiece {

	private Game board;

	@Before
	public void setUp() throws Exception {
		board = new NineMMGame();

		board.setRedPlayer(new HumanPlayer('R', board));
		board.setBluePlayer(new HumanPlayer('B', board));
	}

	@After
	public void tearDown() throws Exception {
	}

	// acceptance criterion 3.1
	@Test
	public void testPlacePieceOnEmptyCell() {
		board.getTurnPlayer().placePiece(0, 0);
		assertEquals("", board.getCell(0, 0), Cell.RED);
		assertEquals("", board.getTurnPlayer().getColor(), 'B');
	}

	// acceptance criterion 3.2
	@Test
	public void testPlacePieceOnNonEmptyCell() {
		board.getTurnPlayer().placePiece(0, 0);
		board.getTurnPlayer().placePiece(0, 0);
		assertEquals("", board.getCell(0, 0), Cell.RED);
		assertEquals("", board.getTurnPlayer().getColor(), 'B');
	}
	// acceptance criterion 3.3
	@Test
	public void testCrossTurnMoveNonVacantCell() {
		board.getTurnPlayer().placePiece(1, 0);
		assertEquals("", board.getCell(1, 0), Cell.INVALID);
		assertEquals("", board.getTurnPlayer().getColor(), 'R');
	}
}