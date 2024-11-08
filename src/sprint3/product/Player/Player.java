package sprint3.product.Player;

import sprint3.product.Cell;
import sprint3.product.CheckMill;
import sprint3.product.Game.Game;
import sprint3.product.GamePiece;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public abstract class Player {
    private char color;
    private Deque<GamePiece> gamePieces = new ArrayDeque<>();
    private List<GamePiece> boradPieces = new ArrayList<>();
    private int pieceCount = 0;
    private Cell playerTag;
    private Cell opponentTag;
    private Game game;
    private boolean cpu;

    public Player(char color, int pieces, Game game) {
        this.color = color;
        this.game = game;
        this.cpu = false;
        for (int i = pieces; i >= 1; i--) {
            this.gamePieces.push(new GamePiece(i, color));
        }
        if (color == 'R') {
            this.playerTag = Cell.RED;
            this.opponentTag = Cell.BLUE;
        }
        else {
            this.playerTag = Cell.BLUE;
            this.opponentTag = Cell.RED;
        }
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
        return boradPieces;
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
            pieceCount--;
        }
    }

    public boolean placePiece(int row, int col) {
        CheckMill millChecker = new CheckMill(game.getGrid());
        if (game.getCell(row, col)== Cell.EMPTY||game.getCell(row,col)== Cell.MOVEVALID){
            game.setGrid(row, col, this.playerTag);
            this.setGamePiece(row, col);

            //don't change the turn if the player has formed the mill
            if(millChecker.checkMillAllDireactions(row, col)){
                game.setCurrentGamestate(Game.GameState.MILLING);
            }
            else{
                game.changeTurn();
                game.updateGameState();
            }
            if (!game.canPlayerMovePiece()){
                //turnPlayer loses
                game.setCurrentGamestate(Game.GameState.GAMEOVER);
            }

            game.gameOver();

//            for (GamePiece gp : boradPieces){
//                gp.printLocation();
//            }
            return true;
        }
        return false;
    }

    public void movePiece(int row, int col, int movingRow, int movingCol) {
        GamePiece gp = this.getGamePiece(movingRow, movingCol);
        if (placePiece(row,col)) {
            game.setGrid(movingRow, movingCol, Cell.EMPTY);
            game.clearHighlightCells();
            assert gp != null;
            gp.setLocation(row,col);
        }
    }

    public boolean removePiece(int row, int col) {
        CheckMill millChecker = new CheckMill(game.getGrid());
        //only lets player from removing their opponent piece
        if(game.getCell(row, col) != opponentTag){
            return false;
        }
        else{
            //prevent player from removing the pieces in the mill
            if(millChecker.checkMillAllDireactions(row, col) && game.getOppFreePieces()){
                return false;
            }
            else{
                game.setGrid(row, col, Cell.EMPTY);
                game.getOpponentPlayer().removeBoardPiece(row, col); //removing the players piece from the pieces list
                game.updateGameState();

                game.gameOver();
                game.changeTurn();
                return true; //player can remove the opp player piece nit in the mill
            }
        }
    }

    private GamePiece getGamePiece(int row, int col){
        for (GamePiece gamePiece : boradPieces ){
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
            boradPieces.add(gamePiece);
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

    public int[] genPlace() {
        return null;
    }
}
