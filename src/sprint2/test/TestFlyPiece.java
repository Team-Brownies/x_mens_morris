package sprint2.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sprint2.product.NMMGame;

public class TestFlyPiece {

	private NMMGame board;

	@Before
	public void setUp() throws Exception {
		board = new NMMGame(9);
		board.getRedPlayer().setGamePiecesForFlying();
		board.getBluePlayer().setGamePiecesForFlying();
		board.placePiece(0, 0);
		board.placePiece(1, 1);
		board.placePiece(2, 2);
		board.placePiece(4, 4);
		board.placePiece(5, 5);
		board.placePiece(6, 6);
	}

	@After
	public void tearDown() throws Exception {
	}

	// acceptance criterion 5.1
	@Test
	public void testFlyPieceToEmptyCell() {
		assertEquals("", board.getCurrentGamestate(), NMMGame.GameState.FLYING);
		board.movePiece(3, 0, 0, 0);
		assertEquals("", board.getCell(3, 0), NMMGame.Cell.RED);
		assertEquals("", board.getCell(0, 0), NMMGame.Cell.EMPTY);
		assertEquals("", board.getTurnPlayer().getColor(), 'B');
	}

	// acceptance criterion 5.2
	@Test
	public void testFlyPieceOnNonEmptyCell() {
		NMMGame.Cell beforeMove;
		assertEquals("", board.getCurrentGamestate(), NMMGame.GameState.FLYING);

		beforeMove = board.getCell(1, 1);

		board.movePiece(1, 1, 0, 0);

		assertSame(board.getCell(1, 1), beforeMove);
		assertEquals("", board.getCell(0, 0), NMMGame.Cell.RED);
		assertEquals("", board.getTurnPlayer().getColor(), 'R');
	}
}
