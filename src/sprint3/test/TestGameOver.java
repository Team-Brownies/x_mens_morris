package sprint3.test;

import org.junit.Before;
import org.junit.Test;
import sprint3.product.Game.Game;
import sprint3.product.Game.GameState;
import sprint3.product.Game.NineMMGame;
import sprint3.product.Player.HumanPlayer;

import static org.junit.Assert.*;

public class TestGameOver {

    private Game game;

    @Before
    public void setUp() throws Exception {
        game = new NineMMGame();
        game.setRedPlayer(new HumanPlayer('R', game));
        game.setBluePlayer(new HumanPlayer('B', game));
    }

    // acceptance criterion 7.1
    @Test
    public void testMillWin() {
        //precondition
        placePiecesForFlying();
        game.getTurnPlayer().movePiece(6, 6, 1, 3); //blue fly
        game.getTurnPlayer().movePiece(0, 0, 0, 3); //red move

        game.getTurnPlayer().removePiece(6,6); // remove blue


        assertEquals("", game.getTurnPlayer().getColor(), 'B');

        assertTrue(game.getTurnPlayer().totalNumberOfPieces() < 3);

        assertEquals("", game.getTurnPlayer().getPlayersGamestate(), GameState.GAMEOVER);
    }

    // acceptance criterion 7.2
    @Test
    public void testBlockWin() {
        //precondition
        placePiecesForBlocking();

        assertEquals("", game.getTurnPlayer().getColor(), 'R');

        assertFalse(game.getTurnPlayer().canPiecesMove());

        assertEquals("", game.getTurnPlayer().getPlayersGamestate(), GameState.GAMEOVER);
    }

    // acceptance criterion 7.2
    @Test
    public void testTiedGame() {
        //precondition
        placePiecesForFlying();

        game.getTurnPlayer().movePiece(1, 5, 3, 1); //blue fly

        game.getTurnPlayer().removePiece(3,0); // remove red

        assertEquals("", game.getTurnPlayer().getColor(), 'R');

        assertEquals("", game.getTurnPlayer().getPlayersGamestate(), GameState.DRAW);
    }

    private void placePiecesForFlying() {
        game.getRedPlayer().setGamePiecesTo(4);
        game.getBluePlayer().setGamePiecesTo(3);

        game.getTurnPlayer().placePiece(0,3); //red
        game.getTurnPlayer().placePiece(1,1); //blue
        game.getTurnPlayer().placePiece(3,0); //red
        game.getTurnPlayer().placePiece(1,3); //blue
        game.getTurnPlayer().placePiece(6,0); //red
        game.getTurnPlayer().placePiece(3,1); //blue
        game.getTurnPlayer().placePiece(5,5); //red


    }

    private void placePiecesForBlocking() {
        game.getTurnPlayer().placePiece(0, 0);//red
        game.getTurnPlayer().placePiece(6, 0);//blue
        game.getTurnPlayer().placePiece(3, 0);//red
        game.getTurnPlayer().placePiece(0, 6);//blue
        game.getTurnPlayer().placePiece(0, 3);//red
        game.getTurnPlayer().placePiece(4, 4);//blue

        game.getTurnPlayer().placePiece(1, 1);//red
        game.getTurnPlayer().placePiece(5, 1);//blue
        game.getTurnPlayer().placePiece(3, 1);//red
        game.getTurnPlayer().placePiece(1, 5);//blue
        game.getTurnPlayer().placePiece(1, 3);//red
        game.getTurnPlayer().placePiece(4, 3);//blue

        game.getTurnPlayer().placePiece(2, 2);//red
        game.getTurnPlayer().placePiece(2, 3);//blue
        game.getTurnPlayer().placePiece(2, 4);//red
        game.getTurnPlayer().placePiece(3, 2);//blue
        game.getTurnPlayer().placePiece(4, 2);//red
        game.getTurnPlayer().placePiece(3, 4);//blue
    }
}