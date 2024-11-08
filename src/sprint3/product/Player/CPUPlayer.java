package sprint3.product.Player;

import sprint3.product.Game.Game;

public class CPUPlayer extends Player{
    private boolean cpu;

    public CPUPlayer(char color, int pieces, Game game) {
        super(color, pieces, game);
        setCpu(true);
    }

    public int[] genPlace(){
        return new int[]{0, 0};
    }
}