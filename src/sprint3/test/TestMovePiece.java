package sprint3.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sprint3.product.Cell;
import sprint3.product.Game.NMMGame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TestMovePiece {

	private NMMGame board;

	@Before
	public void setUp() throws Exception {
		board = new NMMGame();
		board.getTurnPlayer().placePiece(0, 0);
		board.getTurnPlayer().placePiece(1, 1);
		board.getTurnPlayer().placePiece(2, 2);
		board.getTurnPlayer().placePiece(4, 4);
		board.getTurnPlayer().placePiece(5, 5);
		board.getTurnPlayer().placePiece(6, 6);

		board.getTurnPlayer().placePiece(0, 6);
		board.getTurnPlayer().placePiece(1, 5);
		board.getTurnPlayer().placePiece(2, 4);
		board.getTurnPlayer().placePiece(4, 2);
		board.getTurnPlayer().placePiece(5, 1);
		board.getTurnPlayer().placePiece(6, 0);


		board.getTurnPlayer().placePiece(1, 3);
		board.getTurnPlayer().placePiece(0, 3);
		board.getTurnPlayer().placePiece(4, 3);
		board.getTurnPlayer().placePiece(2, 3);
		board.getTurnPlayer().placePiece(6, 3);
		board.getTurnPlayer().placePiece(5, 3);
	}

	@After
	public void tearDown() throws Exception {
	}

	// acceptance criterion 4.1
	@Test
	public void testMovePieceToEmptyCell() {
		assertEquals("", board.getCurrentGamestate(), NMMGame.GameState.MOVING);
		board.getTurnPlayer().movePiece(3, 0, 0, 0);
		assertEquals("", board.getCell(3, 0), Cell.RED);
		assertEquals("", board.getCell(0, 0), Cell.EMPTY);
		assertEquals("", board.getTurnPlayer().getColor(), 'B');
	}

	// acceptance criterion 4.2
	@Test
	public void testMovePieceToNonEmptyCell() {
		Cell beforeMove;
		assertEquals("", board.getCurrentGamestate(), NMMGame.GameState.MOVING);

		beforeMove = board.getCell(0, 3);

		board.getTurnPlayer().movePiece(0, 3, 0, 0);

        assertSame(board.getCell(0, 3), beforeMove);
		assertEquals("", board.getCell(0, 0), Cell.RED);
		assertEquals("", board.getTurnPlayer().getColor(), 'R');
	}
}
