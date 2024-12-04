package sprint3.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sprint3.product.Cell;
import sprint3.product.Game.GameState;
import sprint3.product.Game.NineMMGame;
import sprint3.product.Player.HumanPlayer;

import static org.junit.Assert.*;

public class TestFlyPiece {

	private NineMMGame board;

	@Before
	public void setUp() throws Exception {
		board = new NineMMGame();
		board.setRedPlayer(new HumanPlayer('R', board));
		board.setBluePlayer(new HumanPlayer('B', board));

		board.getRedPlayer().setGamePiecesTo(4);
		board.getBluePlayer().setGamePiecesTo(4);

		board.getTurnPlayer().placePiece(0, 0); //red
		board.getTurnPlayer().placePiece(1, 1); //blue
		board.getTurnPlayer().placePiece(0, 3); //red
		board.getTurnPlayer().placePiece(4, 4); //blue
		board.getTurnPlayer().placePiece(0, 6); //red

		board.getTurnPlayer().removePiece(1,1); // remove blue

		board.getTurnPlayer().placePiece(6, 6); //blue
		board.getTurnPlayer().placePiece(2, 2); //red
		board.getTurnPlayer().placePiece(1, 1); //blue
		board.getTurnPlayer().movePiece(3, 0, 0, 0); //move red
	}

	@After
	public void tearDown() throws Exception {
	}

	// acceptance criterion 5.1
	@Test
	public void testFlyPieceToEmptyCell() {
		assertEquals("", board.getTurnPlayer().getPlayersGamestate(), GameState.FLYING);
		board.getTurnPlayer().movePiece(1, 3, 1, 1);
		assertEquals("", board.getCell(1, 3), Cell.BLUE);
		Cell cell = board.getCell(1, 1);
		assertTrue("",cell == Cell.EMPTY || cell == Cell.MOVEVALID);
		assertEquals("", board.getTurnPlayer().getColor(), 'R');
	}

	// acceptance criterion 5.2
	@Test
	public void testFlyPieceToNonEmptyCell() {
		Cell beforeMove;
		assertEquals("", board.getTurnPlayer().getPlayersGamestate(), GameState.FLYING);

		beforeMove = board.getCell(0, 3);

		board.getTurnPlayer().movePiece(0, 3, 1, 1);

		assertSame(board.getCell(0, 3), beforeMove);
		assertEquals("", board.getCell(1, 1), Cell.BLUE);
		assertEquals("", board.getTurnPlayer().getColor(), 'B');
	}
}
