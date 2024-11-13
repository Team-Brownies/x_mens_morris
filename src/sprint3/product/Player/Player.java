package sprint3.product.Player;

import sprint3.product.Cell;
import sprint3.product.CheckMill;
import sprint3.product.GUI.Board;
import sprint3.product.GUI.GameSpace;
import sprint3.product.Game.Game;
import sprint3.product.Game.GameState;
import sprint3.product.GamePiece;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public abstract class Player {
    private char color;
    private Deque<GamePiece> gamePieces = new ArrayDeque<>();
    private List<GamePiece> boardPieces = new ArrayList<>();
    private int pieceCount = 0;
    private Cell playerTag;
    private Cell opponentTag;
    private Game game;
    private boolean cpu;
    private GameState playersGamestate;

    public Player(char color, int pieces, Game game) {
        this.color = color;
        this.game = game;
        this.cpu = false;
        for (int i = pieces; i >= 1; i--) {
            this.gamePieces.push(new GamePiece(i, color, game));
        }
        if (color == 'R') {
            this.playerTag = Cell.RED;
            this.opponentTag = Cell.BLUE;
        }
        else {
            this.playerTag = Cell.BLUE;
            this.opponentTag = Cell.RED;
        }
        this.playersGamestate = GameState.PLACING;
    }

    public int numberOfGamePieces() {
        return gamePieces.size();
    }

    public int totalNumberOfPieces() {
        return pieceCount+gamePieces.size();
    }

    public char getColor() {
        return color;
    }

    public Deque<GamePiece> getGamePieces() {
        return gamePieces;
    }

    public List<GamePiece> getBoardPieces() {
        return boardPieces;
    }

    public int numberOfBoardPieces() {return pieceCount;}

    public void setGamePiecesForFlying(){
        while(this.numberOfGamePieces()>3){
            gamePieces.pop();
        }
    }

    public void removeBoardPiece(int row, int col) {
        GamePiece gp = this.getGamePiece(row, col);
        if(gp!=null) {
            gp.removeFromPlay();
            System.out.println("removeBoardPiece");
            gp.printLocation();
            pieceCount--;
        }
    }
    public void placePiece(int row, int col) {
        Board gui = game.getGui();
        GameSpace animateGP = gui.getGameSpace(row, col);
        game.setGrid(row, col, this.playerTag);
        this.setGamePiece(row, col);
        if (this.playersGamestate==GameState.PLACING) {
            animateGP.animatePlacePiece(() -> {
                //don't change the turn if the player has formed the mill
                game.checkMill(row, col);
            });
        } else {
            System.out.println("place check mill");
            game.checkMill(row, col);
        }
    }

    public void movePiece(int row, int col, int movingRow, int movingCol) {
        GamePiece gp = this.getGamePiece(movingRow, movingCol);
        Board gui = game.getGui();
        GameSpace animateGP = gui.getGameSpace(row, col);
        GameSpace movingGP = gui.getGameSpace(movingRow, movingCol);

        //clear old game space
        game.setGrid(movingRow, movingCol, Cell.EMPTY);

        animateGP.animateMovePiece(() -> {
            //place on new game space
            placePiece(row,col);
            game.clearHighlightCells();
            assert gp != null;
            //update pieces Location
            gp.setLocation(row,col);
        },movingGP);
    }

    public boolean removePiece(int row, int col) {
        CheckMill millChecker = new CheckMill(game.getGrid());
        Board gui = game.getGui();
        GameSpace animateGP = gui.getGameSpace(row, col);
        //only lets player from removing their opponent piece
        if(game.getCell(row, col) != opponentTag){
            return false;
        }
        else{
            //prevent player from removing the pieces in the mill
            if(millChecker.checkMillAllDirections(row, col) && game.getOppFreePieces()){
                return false;
            }
            else{
                game.setGrid(row, col, Cell.EMPTY);
                //removing the players piece from the pieces list
                game.getOpponentPlayer().removeBoardPiece(row, col);
                animateGP.animateRemovePiece(() -> {
                    game.changeTurn();
                });
                return true; //player can remove the opp player piece nit in the mill
            }
        }
    }

    public GamePiece getGamePiece(int row, int col){
        for (GamePiece gamePiece : boardPieces){
            if(gamePiece.getPieceByLocation(row, col)) {
                return gamePiece;
            }
        }
        return null;
    }

    private void setGamePiece(int row, int col) {
        GamePiece gamePiece;
        if (gamePieces.peek()!=null){
            gamePiece = gamePieces.poll();
            gamePiece.setLocation(row, col);
            boardPieces.add(gamePiece);
            pieceCount++;
        }
    }

    public Cell getPlayerTag() {
        return playerTag;
    }

    public Cell getOpponentTag() {
        return opponentTag;
    }

    public boolean isCPU() {
        return cpu;
    }

    public void setCpu(boolean cpu) {
        this.cpu = cpu;
    }

    public Game getGame() {
        return game;
    }

    public GameState getPlayersGamestate() {
        return playersGamestate;
    }

    public void setPlayersGamestate(GameState playersGamestate) {
        this.playersGamestate = playersGamestate;
    }

    public abstract int[] genPlace();

    public abstract void makeCPUMove();

    public GamePiece getGamePieceByLocation(int row, int col) {
        for (GamePiece p : boardPieces){
            if (p.getPieceByLocation(row, col))
                return p;
        }
        return null;
    }
    public void updatePieces(){
        for (GamePiece p : boardPieces) {
            p.updateValidMovesLocations();
        }
    }
    public boolean canPiecesMove(){
        for (GamePiece p : boardPieces) {
            if(!p.getValidMovesLocations().isEmpty())
                return true;
        }
        System.out.println(color+" color has no movable pieces.");
        return false;
    }
}
