package sprint3.product;

import sprint3.product.Game.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GamePiece {
    private final String id;
    private int[] location = new int[2];
    private boolean inPlay = true;
    private List<int[]> validMovesLocations = new ArrayList<>();
    private final Game game;
    private Cell cellState = Cell.MOVEVALID;

    public GamePiece(int i, char color, Game game) {
        this.id = color+String.valueOf(i);
        this.game = game;
    }

    public void setLocation(int row, int col) {
        this.location[0] = row;
        this.location[1] = col;
        this.updateValidMovesLocations();
    }

    public void setLocation(int[] location) {
        this.location = location;
    }

    public int[] getLocation() {
        return location;
    }

    public String getIdByLocation(int row, int col) {
        return id;
    }

    public String getId() {
        return id;
    }

    public boolean isInPlay() {
        return inPlay;
    }

    public boolean getPieceByLocation(int row, int col) {
        //returns true if this piece is at the given location
        return this.location[0] == row && this.location[1] == col;
    }

    public void removeFromPlay() {
        this.inPlay = false;
        //give out of play Location
        this.setLocation(-1,-1);
        this.updateValidMovesLocations();
    }
    public void printLocation(){
        if (this.inPlay)
            System.out.println("ID: "+id+" ("+ this.location[0]+", "+this.location[1]+")");
    }

    public List<int[]> getValidMovesLocations() {
        return validMovesLocations;
    }

    public void setCellStateForFlying() {
        this.cellState = Cell.EMPTY;
    }

    public void updateValidMovesLocations() {
        List<int[]> validMoves;

        game.clearHighlightCells();

        this.clearValidMovesLocation();


        // only findAdjacentCells for inPlay Pieces
        if (inPlay){
            game.findAdjacentCells(this.location);
            validMoves = game.getCellsByCellType(this.cellState);

            this.validMovesLocations.addAll(validMoves);
        }
        if (!validMovesLocations.isEmpty() && id.contains("R")) {
            printLocation();
            System.out.println("valid moves: ");
            printValidMoves();
        }
    }

    public void clearValidMovesLocation() {
        this.validMovesLocations.clear();
    }

    public void printValidMoves() {
        for (int[] move:validMovesLocations){
            System.out.println("("+move[0]+", "+move[1]+")");
        }
    }
}
