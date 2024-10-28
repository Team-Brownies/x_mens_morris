package sprint1.product;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Player {
    private char color;
    private Deque<GamePiece> gamePieces = new ArrayDeque<>();
    private List<GamePiece> boradPieces = new ArrayList<>();

    public Player(char color, int pieces) {
        this.color = color;
        for (int i = 1; i <= pieces; i++) {
            this.gamePieces.push(new GamePiece(i, color));
        }
    }

    public int numberOfGamePieces() {
        return gamePieces.size();
    }

    public void getGamePiece(int row, int col) {
        GamePiece gamePiece;
        if (gamePieces.peek()!=null){
            gamePiece = gamePieces.poll();
            gamePiece.setLocation(row, col);
            boradPieces.add(gamePiece);
        }
    }

    public char getColor() {
        return color;
    }

    public Deque<GamePiece> getGamePieces() {
        return gamePieces;
    }
}
