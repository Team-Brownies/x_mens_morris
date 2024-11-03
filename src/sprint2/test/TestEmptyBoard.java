package sprint2.test;

import static org.junit.Assert.*;

import org.junit.Test;

import sprint1.product.NMMGame;

public class TestEmptyBoard {

	// acceptance criterion 2.1
	@Test
	public void testNewNineMensMorrisBoard() {
		NMMGame game = new NMMGame(9);
		int size = game.getSize();
		int middle = (size-1)/2;

		assertEquals("", size, 7);

		for (int row = 0; row<size; row++) {
			for (int column = 0; column<size; column++) {
				if (row == middle && column == middle)
					assertEquals("", game.getCell(row, column), NMMGame.Cell.INVALID);
				else if (row == column || row == (size-1-column) || row == middle || column == middle)
					assertEquals("", game.getCell(row, column), NMMGame.Cell.EMPTY);
				else
					assertEquals("", game.getCell(row, column), NMMGame.Cell.INVALID);
			}
		}
		assertEquals("", game.getTurnPlayer().getColor(), 'R');

		assertEquals("", game.getRedPlayer().getGamePieces().size(), 9);
		assertEquals("", game.getBluePlayer().getGamePieces().size(), 9);
	}
	@Test
	public void testNewFiveMensMorrisBoard() {
		NMMGame game = new NMMGame(5);
		int size = game.getSize();
		int middle = (size-1)/2;

		assertEquals("", size, 5);
		for (int row = 0; row<size; row++) {
			for (int column = 0; column<size; column++) {
				if (row == middle && column == middle)
					assertEquals("", game.getCell(row, column), NMMGame.Cell.INVALID);
				else if (row == column || row == (size-1-column) || row == middle || column == middle)
					assertEquals("", game.getCell(row, column), NMMGame.Cell.EMPTY);
				else
					assertEquals("", game.getCell(row, column), NMMGame.Cell.INVALID);
			}
		}
		assertEquals("", game.getTurnPlayer().getColor(), 'R');

		assertEquals("", game.getRedPlayer().getGamePieces().size(), 5);
		assertEquals("", game.getBluePlayer().getGamePieces().size(), 5);
	}
}
