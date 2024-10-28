package sprint1.product;

public class GamePiece {
    private String id;
    private int row, col;

    public GamePiece(int i, char color) {
        this.id = color+String.valueOf(i);
        System.out.println(this.id);
    }

    public void setLocation(int row, int col) {
        this.row = row;
        this.col = col;
        System.out.println("("+row+", "+col+")");
    }

    public String getIdByLocation(int row, int col) {
        return id;
    }
}
