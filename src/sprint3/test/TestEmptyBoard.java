package sprint3.test;

import org.junit.Test;
import sprint3.product.Cell;
import sprint3.product.Game.NineMMGame;
import sprint3.product.Game.Game;
import sprint3.product.Game.SixMMGame;
import sprint3.product.Player.HumanPlayer;

import static org.junit.Assert.assertEquals;

public class TestEmptyBoard {

	// acceptance criterion 1.1
	@Test
	public void testNewNineMensMorrisBoard() {
		Game game = new NineMMGame();
		int size = game.getSize();
		int middle = (size-1)/2;

		game.setRedPlayer(new HumanPlayer('R', game));
		game.setBluePlayer(new HumanPlayer('B', game));

		assertEquals("", size, 7);

		for (int row = 0; row<size; row++) {
			for (int column = 0; column<size; column++) {
				if (row == middle && column == middle)
					assertEquals("", game.getCell(row, column), null);
				else if (row == column || row == (size-1-column) || row == middle || column == middle)
					assertEquals("", game.getCell(row, column), Cell.EMPTY);
				else
					assertEquals("", game.getCell(row, column), Cell.INVALID);
			}
		}
		assertEquals("", game.getTurnPlayer().getColor(), 'R');

		assertEquals("", game.getRedPlayer().getGamePieces().size(), 9);
		assertEquals("", game.getBluePlayer().getGamePieces().size(), 9);
	}
	// acceptance criterion 1.2
	@Test
	public void testNewSixMensMorrisBoard() {
		Game game = new SixMMGame();
		int size = game.getSize();
		int middle = (size-1)/2;

		game.setRedPlayer(new HumanPlayer('R', game));
		game.setBluePlayer(new HumanPlayer('B', game));

		assertEquals("", size, 5);
		for (int row = 0; row<size; row++) {
			for (int column = 0; column<size; column++) {
				if (row == middle && column == middle)
					assertEquals("", game.getCell(row, column), null);
				else if (row == column || row == (size-1-column) || row == middle || column == middle)
					assertEquals("", game.getCell(row, column), Cell.EMPTY);
				else
					assertEquals("", game.getCell(row, column), Cell.INVALID);
			}
		}
		assertEquals("", game.getTurnPlayer().getColor(), 'R');

		assertEquals("", game.getRedPlayer().getGamePieces().size(), 6);
		assertEquals("", game.getBluePlayer().getGamePieces().size(), 6);
	}
}
