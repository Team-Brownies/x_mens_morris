package sprint3.product.Player;

import sprint3.product.Cell;
import sprint3.product.CheckMill;
import sprint3.product.GUI.Board;
import sprint3.product.GUI.GameSpace;
import sprint3.product.Game.Game;
import sprint3.product.Game.GameState;
import sprint3.product.GamePiece;

import java.util.*;

public abstract class Player {
    private char color;
    private Deque<GamePiece> gamePieces = new ArrayDeque<>();
    private List<GamePiece> boardPieces = new ArrayList<>();
    private int pieceCount = 0;
    private Cell playerTag;
    private Cell opponentTag;
    private Game game;
    private boolean cpu = false;
    private GameState playersGamestate;

    // player one for each user
    public Player(char color, Game game) {
        this.color = color;
        this.game = game;

        // assigns game pieces to player
        for (int i = game.getPieces(); i >= 1; i--) {
            this.gamePieces.push(new GamePiece(i, color, game));
        }
        // assign tags for players based on color
        this.playerTag = (color == 'R') ? Cell.RED : Cell.BLUE;
        this.opponentTag = (color == 'R') ? Cell.BLUE : Cell.RED;

        // starts player's game state in placing
        this.playersGamestate = GameState.PLACING;
    }

    // return the of number pieces in the place queue
    public int numberOfGamePieces() {
        return gamePieces.size();
    }

    // return the of total number pieces that are in play
    public int totalNumberOfPieces() {
        return pieceCount+gamePieces.size();
    }

    // Get List of pieces that are movable
    public List<GamePiece> playersMovablePieces(Player player){
        List<GamePiece> moveablePieces = new ArrayList<>();
        List<GamePiece> playersBoardPieces = player.getBoardPieces();
        player.updateValidMovesLocations();
        for (GamePiece piece : playersBoardPieces){
            piece.updateValidMovesLocations();
            if(!piece.getValidMovesLocations().isEmpty()) {
                moveablePieces.add(piece);
            }
        }
        return moveablePieces;
    }
    // return players color
    public char getColor() {
        return color;
    }

    // returns game pieces in place queue
    public Deque<GamePiece> getGamePieces() {
        return gamePieces;
    }

    // returns game pieces that are on the game board
    protected List<GamePiece> getBoardPieces() {
        return boardPieces;
    }

    // returns the coord of in play game pieces
    public List<int[]> getBoardPiecesCoords() {
        List<int[]> coords = new ArrayList<>();
        for (GamePiece gp:boardPieces){
            if (gp.isInPlay())
                coords.add(gp.getLocation());
        }
        return coords;
    }

    // returns the number game pieces that are in play
    public int numberOfBoardPieces() {
        return pieceCount;
    }

    // debug for testing changes the starting number of pieces to 3 to test flying
    public void setGamePiecesTo(int number){
        while(this.numberOfGamePieces()>number){
            gamePieces.pop();
        }
    }

    // remove a player's game piece from play and subtract it from the pieceCount
    private void removeBoardPiece(int row, int col) {
        GamePiece gp = this.getGamePieceByLocation(row, col);
        if(gp!=null) {
            gp.removeFromPlay();
            pieceCount--;
        }
    }

    // places a game piece on the given coordinates
    public void placePiece(int row, int col) {
        Board gui = game.getGui();
        if(game.canPlacePiece(row,col)) {
            game.setGrid(row, col, this.playerTag);
            this.setGamePieceStartingLocation(row, col);
            if (this.playersGamestate==GameState.PLACING && gui!=null) {
                GameSpace animateGP = gui.getGameSpace(row, col);
                animateGP.animatePlacePiece(() -> {
                    //don't change the turn if the player has formed the mill
                    game.checkMill(row, col);
                });
            } else {
                game.checkMill(row, col);
            }
        }
    }

    // moves a game piece (got with moving coordinates) and places it on the new given coordinates
    public void movePiece(int row, int col, int movingRow, int movingCol) {
        GamePiece gp = this.getGamePieceByLocation(movingRow, movingCol);
        Board gui = game.getGui();

        if(game.canPlacePiece(row,col)) {
            //clear old game space
            game.setGrid(movingRow, movingCol, Cell.EMPTY);

            if (gui != null) {
                GameSpace animateGP = gui.getGameSpace(row, col);
                GameSpace movingGP = gui.getGameSpace(movingRow, movingCol);
                animateGP.animateMovePiece(() -> {
                    //place on new game space
                    placePiece(row, col);
                    assert gp != null;
                    //update pieces Location
                    gp.setLocation(row, col);
                }, movingGP);
            } else {
                //place on new game space
                placePiece(row, col);
                assert gp != null;
                //update pieces Location
                gp.setLocation(row, col);
            }
        }
    }

    // remove a players game piece from game
    public boolean removePiece(int row, int col) {
        CheckMill millChecker = new CheckMill(game.getGrid());
        Board gui = game.getGui();
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
                //removes the player's piece from the pieces list
                game.getOpponentPlayer().removeBoardPiece(row, col);
                if (gui!=null) {
                    GameSpace animateGP = gui.getGameSpace(row, col);
                    animateGP.animateRemovePiece(() -> {
                        game.changeTurn();
                    });
                } else {
                    game.changeTurn();
                }
                return true; //player can remove the opp player piece nit in the mill
            }
        }
    }

    // update a game pieces coordinate when placed form the stack
    // and adds in to the piece count
    private void setGamePieceStartingLocation(int row, int col) {
        GamePiece gamePiece;
        if (gamePieces.peek()!=null){
            gamePiece = gamePieces.poll();
            gamePiece.setLocation(row, col);
            boardPieces.add(gamePiece);
            pieceCount++;
        }
    }

    // returns the player's tag
    public Cell getPlayerTag() {
        return playerTag;
    }

    // returns the opponent's tag
    public Cell getOpponentTag() {
        return opponentTag;
    }

    // returns if the player is a CPU or not
    public boolean isCPU() {
        return cpu;
    }

    // sets the player to CPU
    public void setCpu(boolean isCpu) {
        this.cpu = isCpu;
    }

    // return the game this player is in
    public Game getGame() {
        return game;
    }

    // return the game state of this player
    public GameState getPlayersGamestate() {
        return playersGamestate;
    }

    // set the game state of this player
    public void setPlayersGamestate(GameState playersGamestate) {
        this.playersGamestate = playersGamestate;
    }

    // method for CPUPlayer to use
    public void makeCPUMove(){}

    // searches for a player's GamePiece with the given coordinates
    public GamePiece getGamePieceByLocation(int row, int col) {
        for (GamePiece p : boardPieces){
            if (p.getPieceByLocation(row, col))
                return p;
        }
        return null;
    }

    // updates the valid moves locations of all of player's pieces
    private void updateValidMovesLocations(){
        for (GamePiece p : boardPieces) {
            p.updateValidMovesLocations();
        }
    }

    // check to see if the player can move any of their game piece
    public boolean canPiecesMove(){
        List<GamePiece> movablePieces = playersMovablePieces(this);

        if(movablePieces.isEmpty() && numberOfGamePieces()==0) {
            System.out.println(color+" color has no movable pieces.");
            return false;
        }
        return true;
    }

    public boolean canWinThisTurn(){
        return findMillOrBlock(this, "Mill")!=null;
    }

    // find what piece that when moved can result in a Mill or Block
    protected int[][] findMillOrBlock(Player player, String type) {
        int[] millMove = null;
        int[] blockMove = null;
        List<GamePiece> movablePieces = playersMovablePieces(player);

        Cell playerTag = player.getPlayerTag();
        Cell oppTag = player.getOpponentTag();

        for (GamePiece piece : movablePieces){
            int[] coords = piece.getLocation();
            List<int[]> validMoveSpaces;

            piece.updateValidMovesLocations();
            validMoveSpaces = piece.getValidMovesLocations();

            for (int[] newSpace : validMoveSpaces){
                Cell[][] tempGrid = game.makeTempGrid();
                tempGrid[coords[0]][coords[1]] = Cell.EMPTY;
                // return Mill if found
                switch (type){
                    case "Mill":
                        millMove = findMillMove(newSpace, tempGrid, playerTag);
                        // return Mill Move if found
                        if (millMove!=null) {
                            return new int[][]{coords, millMove};
                        }
                        break;
                    case "Block":
                        blockMove = findMillMove(newSpace, tempGrid, oppTag);
                        // return Block Move if found
                        if (blockMove!=null) {
                            return new int[][]{coords, blockMove};
                        }
                }
            }

        }
        return null;
    }

    // find a move that will result in a mill be formed
    protected int[] findMillMove(int[] move, Cell[][] tempGrid, Cell tag){
        CheckMill millChecker;
        tempGrid[move[0]][move[1]] = tag;

        millChecker = new CheckMill(tempGrid);

        if (millChecker.checkMillAllDirections(move[0], move[1])) {
            return move;
        }
        return null;
    }

    public void setGamePiecesToFlying() {
        for (GamePiece piece:boardPieces){
            piece.setCellStateForFlying();
        }
        for (GamePiece piece:gamePieces){
            piece.setCellStateForFlying();
        }
    }
}
