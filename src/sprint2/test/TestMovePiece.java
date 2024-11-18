package sprint2.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sprint2.product.NMMGame;

public class TestMovePiece {

	private NMMGame board;

	@Before
	public void setUp() throws Exception {
		board = new NMMGame(9);
		board.placePiece(0, 0);
		board.placePiece(1, 1);
		board.placePiece(2, 2);
		board.placePiece(4, 4);
		board.placePiece(5, 5);
		board.placePiece(6, 6);

		board.placePiece(0, 6);
		board.placePiece(1, 5);
		board.placePiece(2, 4);
		board.placePiece(4, 2);
		board.placePiece(5, 1);
		board.placePiece(6, 0);

		board.placePiece(0, 3);
		board.placePiece(1, 3);
		board.placePiece(2, 3);
		board.placePiece(4, 3);
		board.placePiece(5, 3);
		board.placePiece(6, 3);
	}

	@After
	public void tearDown() throws Exception {
	}

	// acceptance criterion 4.1
	@Test
	public void testMovePieceToEmptyCell() {
		assertEquals("", board.getCurrentGamestate(), NMMGame.GameState.MOVING);
		board.movePiece(3, 0, 0, 0);
		assertEquals("", board.getCell(3, 0), NMMGame.Cell.RED);
		assertEquals("", board.getCell(0, 0), NMMGame.Cell.EMPTY);
		assertEquals("", board.getTurnPlayer().getColor(), 'B');
	}

	// acceptance criterion 4.2
	@Test
	public void testMovePieceToNonEmptyCell() {
		NMMGame.Cell beforeMove;
		assertEquals("", board.getCurrentGamestate(), NMMGame.GameState.MOVING);

		beforeMove = board.getCell(0, 3);

		board.movePiece(0, 3, 0, 0);

        assertSame(board.getCell(0, 3), beforeMove);
		assertEquals("", board.getCell(0, 0), NMMGame.Cell.RED);
		assertEquals("", board.getTurnPlayer().getColor(), 'R');
	}
}
