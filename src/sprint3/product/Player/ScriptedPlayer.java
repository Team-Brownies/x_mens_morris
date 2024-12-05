package sprint3.product.Player;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import sprint3.product.GUI.Board;
import sprint3.product.Game.Game;
import sprint3.product.Game.GameState;

public class ScriptedPlayer extends Player{
    private JsonArray moves;
    private int turnNumber = 0;
    private boolean playing = true;
    private int playTo;
    private final Board board;

    public ScriptedPlayer(char color, Game game, JsonArray moveArray, Board board) {
        super(color, game);
        setCpu(true);
        this.moves = moveArray;
        this.board = board;
    }

    // find what type of move to make based on the game state
    @Override
    public void makeCPUMove() {
        GameState gameState = this.getPlayersGamestate();
        boolean moved = false;
        if((turnNumber>this.playTo)){
            board.restartReplay();
        }
        while (!moved && playing && turnNumber<=this.playTo) {
            if (turnNumber==this.playTo) {
                board.setAnimationSpeed(1);
            }
            switch (gameState) {
                case PLACING:
                    if (playerMove(turnNumber)) {
                        placingLogic();
                        moved = true;
                    }
                    break;
                case MOVING, FLYING:
                    if (playerMove(turnNumber)) {
                        movingLogic();
                        moved = true;
                    }
                    break;
                case MILLING:
                    if (!playerMove(turnNumber)) {
                        removeLogic();
                        moved = true;
                    }
                    break;
                default:
                    return;
            }
            turnNumber++;
        }
        board.setReplaySeekSlider(turnNumber);
    }

    private void placingLogic() {
        JsonObject piece = moves.get(turnNumber).getAsJsonObject();
        int row = piece.get("Row").getAsInt();
        int col = piece.get("Col").getAsInt();
        placePiece(row, col);
    }

    private void movingLogic() {
        JsonObject piece = moves.get(turnNumber).getAsJsonObject();
        String gp = piece.get("Piece").getAsString();
        int row = piece.get("Row").getAsInt();
        int col = piece.get("Col").getAsInt();

        int[] coords = this.getGamePieceCoordsById(gp);
        movePiece(row,col,coords[0],coords[1]);
    }

    private void removeLogic() {
        JsonObject piece = moves.get(turnNumber).getAsJsonObject();
        String gp = piece.get("Piece").getAsString();

        int[] coords = getGame().getOpponentPlayer().getGamePieceCoordsById(gp);
        removePiece(coords[0],coords[1]);
    }
    private boolean playerMove(int i){
        JsonObject piece = moves.get(i).getAsJsonObject();

        String gp = piece.get("Piece").getAsString();
        int row = piece.get("Row").getAsInt();
        String color = String.valueOf(this.getColor());
        return gp.startsWith(color) && row!=-1;
    }

    public void setPlayTo(int playTo) {
        this.playTo = playTo;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
}