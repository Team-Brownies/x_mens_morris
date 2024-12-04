package sprint3.product;

import sprint3.product.Game.Game;
import sprint3.product.Game.GameHistory;

import java.util.*;

public class GamePiece {
    private final String id;
    private int[] location = new int[2];
    private boolean inPlay = true;
    private List<int[]> validMovesLocations = new ArrayList<>();
    private final Game game;
    private Set<Cell> cellState = new HashSet<>();
    private final GameHistory gameHistory;

    // a game piece use on game spaces
    public GamePiece(int i, char color, Game game) {
        this.id = color+String.valueOf(i);
        this.game = game;
        this.gameHistory = game.getGameHistory();
        this.cellState.add(Cell.MOVEVALID);
    }

    // set the coords of this game piece
    public void setLocation(int row, int col) {
        this.location[0] = row;
        this.location[1] = col;
        this.updateValidMovesLocations();

        //(-1,-1) was removed this turn
        gameHistory.logMove(this.id, row, col);
    }

    // get the coord of this game piece
    public int[] getLocation() {
        return location;
    }

    // is this game piece in play or have it been milled
    public boolean isInPlay() {
        return inPlay;
    }

    // returns turn if this game piece is at the given coords
    public boolean getPieceByLocation(int row, int col) {
        //returns true if this piece is at the given location
        return this.location[0] == row && this.location[1] == col;
    }

    // removes the piece from play
    public void removeFromPlay() {
        this.inPlay = false;
        //update location to one that is not in play
        this.setLocation(-1,-1);
        this.updateValidMovesLocations();
    }

    // return list of space the game piece is able to move to
    public List<int[]> getValidMovesLocations() {
        return validMovesLocations;
    }

    // changes the cell type this game piece is looking
    // for with it searches for a Valid Moves Locations
    public void setCellStateForFlying() {
        this.cellState.add(Cell.EMPTY);
    }

    // update list of Valid Moves Locations
    public void updateValidMovesLocations() {
        List<int[]> validMoves = new ArrayList<>();
        if(game!=null){
            game.clearMoveValid();

            this.clearValidMovesLocation();

            // only findAdjacentCells for inPlay Pieces
            if (inPlay) {
                game.findAdjacentCells(this.location);
                for (Cell state:this.cellState) {
                    validMoves.addAll(game.getCellsByCellType(state));
                }

                this.validMovesLocations=validMoves;
            }
        }
    }

    // clear list of Valid Moves Locations
    public void clearValidMovesLocation() {
        this.validMovesLocations.clear();
    }

    public boolean getPieceById(String id) {
        return Objects.equals(this.id, id);
    }
}
