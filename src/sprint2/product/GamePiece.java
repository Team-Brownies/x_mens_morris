package sprint2.product;

public class GamePiece {
    private String id;
    private int row, col;

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
}
