package sprint3.product.Player;

import sprint3.product.Game.Game;

public class HumanPlayer extends Player{
    private boolean cpu;

    public HumanPlayer(char color, int pieces, Game game) {
        super(color, pieces, game);
        setCpu(false);
    }

    @Override
    public int[] genPlace() {
        return new int[0];
    }

    @Override
    public void makeCPUMove() {

    }
}