package sprint3.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sprint3.product.Cell;
import sprint3.product.Game.GameState;
import sprint3.product.Game.NineMMGame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TestFlyPiece {

	private NineMMGame board;

	@Before
	public void setUp() throws Exception {
		board = new NineMMGame();
		board.getRedPlayer().setGamePiecesForFlying();
		board.getBluePlayer().setGamePiecesForFlying();
		board.getTurnPlayer().placePiece(0, 0);
		board.getTurnPlayer().placePiece(1, 1);
		board.getTurnPlayer().placePiece(2, 2);
		board.getTurnPlayer().placePiece(4, 4);
		board.getTurnPlayer().placePiece(5, 5);
		board.getTurnPlayer().placePiece(6, 6);
	}

	@After
	public void tearDown() throws Exception {
	}

	// acceptance criterion 5.1
	@Test
	public void testFlyPieceToEmptyCell() {
		assertEquals("", board.getTurnPlayer().getPlayersGamestate(), GameState.FLYING);
		board.getTurnPlayer().movePiece(3, 0, 0, 0);
		assertEquals("", board.getCell(3, 0), Cell.RED);
		assertEquals("", board.getCell(0, 0), Cell.EMPTY);
		assertEquals("", board.getTurnPlayer().getColor(), 'B');
	}

	// acceptance criterion 5.2
	@Test
	public void testFlyPieceToNonEmptyCell() {
		Cell beforeMove;
		assertEquals("", board.getTurnPlayer().getPlayersGamestate(), GameState.FLYING);

		beforeMove = board.getCell(1, 1);

		board.getTurnPlayer().movePiece(1, 1, 0, 0);

		assertSame(board.getCell(1, 1), beforeMove);
		assertEquals("", board.getCell(0, 0), Cell.RED);
		assertEquals("", board.getTurnPlayer().getColor(), 'R');
	}
}
