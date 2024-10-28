package sprint1.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sprint1.product.NMMGame;

public class TestPlacePiece {

	private NMMGame board;

	@Before
	public void setUp() throws Exception {
		board = new NMMGame(9);
	}

	@After
	public void tearDown() throws Exception {
	}

	// acceptance criterion 3.1
	@Test
	public void testPlacePieceOnEmptyCell() {
		board.placePiece(0, 0);
		assertEquals("", board.getCell(0, 0), NMMGame.Cell.RED);
		assertEquals("", board.getTurnPlayer().getColor(), 'B');
	}

	// acceptance criterion 3.2
	@Test
	public void testPlacePieceOnNonEmptyCell() {
		board.placePiece(0, 0);
		board.placePiece(0, 0);
		assertEquals("", board.getCell(0, 0), NMMGame.Cell.RED);
		assertEquals("", board.getTurnPlayer().getColor(), 'B');
	}
	// acceptance criterion 3.3
	@Test
	public void testCrossTurnMoveNonVacantCell() {
		board.placePiece(1, 0);
		assertEquals("", board.getCell(1, 0), NMMGame.Cell.INVALID);
		assertEquals("", board.getTurnPlayer().getColor(), 'R');
	}
}
