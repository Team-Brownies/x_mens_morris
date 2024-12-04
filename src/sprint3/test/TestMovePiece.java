package sprint3.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sprint3.product.Cell;
import sprint3.product.Game.GameState;
import sprint3.product.Game.NineMMGame;
import sprint3.product.Player.HumanPlayer;

import static org.junit.Assert.*;

public class TestMovePiece {

	private NineMMGame board;

	@Before
	public void setUp() throws Exception {
		board = new NineMMGame();

		board.setRedPlayer(new HumanPlayer('R', board));
		board.setBluePlayer(new HumanPlayer('B', board));

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
		assertEquals("", board.getTurnPlayer().getPlayersGamestate(), GameState.MOVING);
		board.getTurnPlayer().movePiece(3, 0, 0, 0);
		assertEquals("", board.getCell(3, 0), Cell.RED);
		Cell cell = board.getCell(0, 0);
		assertTrue("",cell == Cell.EMPTY || cell == Cell.MOVEVALID);
		assertEquals("", board.getTurnPlayer().getColor(), 'B');
	}

	// acceptance criterion 4.2
	@Test
	public void testMovePieceToNonEmptyCell() {
		Cell beforeMove;
		assertEquals("", board.getTurnPlayer().getPlayersGamestate(), GameState.MOVING);

		beforeMove = board.getCell(0, 3);

		board.getTurnPlayer().movePiece(0, 3, 0, 0);

        assertSame(board.getCell(0, 3), beforeMove);
		assertEquals("", board.getCell(0, 0), Cell.RED);
		assertEquals("", board.getTurnPlayer().getColor(), 'R');
	}
}