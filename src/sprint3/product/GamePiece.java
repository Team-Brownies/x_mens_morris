package sprint3.product;

public class GamePiece {
    private String id;
    private int row, col;
    private boolean inPlay = true;

    public GamePiece(int i, char color) {
        this.id = color+String.valueOf(i);
    }

    public void setLocation(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int[] getLocation() {
        return new int[]{row, col};
    }

    public String getIdByLocation(int row, int col) {
        return id;
    }

    public boolean getPieceByLocation(int row, int col) {
        //returns true if this piece is at the given location
        return this.row == row && this.col == col;
    }

    public void removeFromPlay() {
        this.inPlay = false;
    }
    public void printLocation(){
        if (this.inPlay)
            System.out.println("ID: "+id+" ("+ this.row+", "+this.col+")");
    }
}
