package sprint3.product;


import java.util.*;

public class CheckMill {
    private final Cell[][] GRID;
    private final int MIDDLE;
    public CheckMill(Cell[][] grid) {
        this.GRID = grid;
        this.MIDDLE = (grid[0].length-1)/2;
    }

    // finds if mill is in a shared row or col (returns 0 for row, 1 for col)
    public int findCommonIndex(List<int[]> sortedMillMates) {
        int inCommonIndex = 0;
        int inCommon = sortedMillMates.getFirst()[inCommonIndex];

        for (int[] p: sortedMillMates) {
            if (!(inCommon==p[inCommonIndex])) {
                inCommonIndex = 1;
                inCommon = sortedMillMates.getFirst()[inCommonIndex];
            }
        }
        return inCommonIndex;

    }

    // sorts mill mates by the non-shared position
    public List<int[]> sortMillMatesBySharedPosition(Set<int[]> millMates) {
        List<int[]> sortedMillMates = new ArrayList<>(millMates);
        int inCommonIndex = findCommonIndex(sortedMillMates);

        int nonCommonIndex = (inCommonIndex==0) ? 1:0;
        sortedMillMates.sort(Comparator.comparingInt(arr -> arr[nonCommonIndex]));

        return sortedMillMates;
    }

    // return a set of pieces a mill is in with
    public Set<int[]> getMillMates(int row, int col) {
        Set<int[]> millMates = new HashSet<>();
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        int[] coords = new int[]{row,col};

        if (checkMillAllDirections(row, col)) {
            for (int i = 0; i < directions.length; i++) {
                if (i % 2 == 0) {
                    millMates = new HashSet<>();
                }
                millMates.addAll(searchForMillMates(directions[i][0], directions[i][1], coords));

                if (millMates.size() == 3) {
                    return millMates;
                }
            }
        } else {
            return null;
        }
        return millMates;
    }
    // searches for pieces a mill is in with
    private Set<int[]> searchForMillMates(int xDirection, int yDirection, int[] coords) {
        int xMagnitude, yMagnitude;
        int row = coords[0];
        int col = coords[1];
        Cell color = GRID[row][col];
        Cell testCell;
        Set<int[]> millMates = new HashSet<>();

        for (int i = 1; i < GRID.length; i++) {
            xMagnitude = col+(i*xDirection);
            yMagnitude = row+(i*yDirection);
            if (xMagnitude< GRID.length && yMagnitude< GRID.length
                    && xMagnitude>=0 && yMagnitude>=0) {
                testCell = GRID[yMagnitude][xMagnitude];
                if (testCell == color && this.checkMillAllDirections(yMagnitude,xMagnitude)) {
                    millMates.add(new int[]{yMagnitude,xMagnitude});
                }
                else if (testCell != Cell.INVALID) {
                    break;
                }
            }
        }
        return millMates;
    }
    public boolean checkMillAllDirections(int row, int col) {
        //check for mill around the piece in all directions
        //return true if mill is formed else false

        return checkVerticalMillDown(row, col) ||
                checkVerticalMillTop(row, col) ||
                checkHorizontalMillRight(row, col) ||
                checkHorizontalMillLeft(row, col) ||
                checkMillMiddle(row, col);
    }
    private boolean checkMillCombo(int red_piece, int blue_piece) {
        if (red_piece > 0 && blue_piece > 0) { //if opponent piece was found on mill
            return false;
        }
        return red_piece == 3 || blue_piece == 3; // return true if the mill is formed
    }
    private boolean checkVerticalMillDown(int row, int col) {
        int red_piece = 0;
        int blue_piece = 0;
        int invalid_point = 0;
        for (int i = row; i < GRID[0].length; i++) {
            if (GRID[i][col] == Cell.RED) {
                red_piece++;
            } else if (GRID[i][col] == Cell.BLUE) {
                blue_piece++;
            } else if (GRID[i][col] == Cell.INVALID || GRID[i][col] == null) {
                invalid_point++;
            }
            if (col == MIDDLE && invalid_point != 0) {
                break;
            }
            if (red_piece > 0 && blue_piece > 0) { //if opponent piece was found on mill
                return false;
            }
            if (red_piece == 3 || blue_piece == 3) {
                return true;
            }  // return true if the mill is formed
        }
        return false; //if the loop terminates without return than it's not mill
    }
    private boolean checkVerticalMillTop(int row, int col) {
        int red_piece = 0;
        int blue_piece = 0;
        int invalid_point = 0;
        for (int i = row; i >= 0; i--) {
            if (GRID[i][col] == Cell.RED) {
                red_piece++;
            } else if (GRID[i][col] == Cell.BLUE) {
                blue_piece++;
            }else if (GRID[i][col] == Cell.INVALID || GRID[i][col] == null) {
                invalid_point++;
            }
            if (col == MIDDLE && invalid_point != 0) {
                break;
            }
            if (red_piece > 0 && blue_piece > 0) { //if opponent piece was found on mill
                return false;
            }
            if (red_piece == 3 || blue_piece == 3) {
                return true;
            }  // return true if the mill is formed
        }
        return false; //if the loop terminates without return than it's not mill
    }
    private boolean checkHorizontalMillRight(int row, int col) {
        int red_piece = 0;
        int blue_piece = 0;
        int invalid_point = 0;
        for (int i = col; i < GRID[0].length; i++) {

            if (GRID[row][i] == Cell.RED) {
                red_piece++;
            } else if (GRID[row][i] == Cell.BLUE) {
                blue_piece++;
            }else if (GRID[row][i] == Cell.INVALID || GRID[row][i] == null) {
                invalid_point++;
            }
            if (row == MIDDLE && invalid_point != 0) {
                break;
            }
            if (red_piece > 0 && blue_piece > 0) { //if opponent piece was found on mill
                return false;
            }
            if (red_piece == 3 || blue_piece == 3) {
                return true;
            }  // return true if the mill is formed
        }
        return false;
    }
    private boolean checkHorizontalMillLeft(int row, int col) {
        int red_piece = 0;
        int blue_piece = 0;
        int invalid_point = 0;
        for(int i = col; i >=0; i--) {
            if (GRID[row][i] == Cell.RED) {
                red_piece++;
            } else if (GRID[row][i] == Cell.BLUE) {
                blue_piece++;
            }else if (GRID[row][i] == Cell.INVALID || GRID[row][i] == null) {
                invalid_point++;
            }
            if (row == MIDDLE && invalid_point != 0) {
                break;
            }
            if (red_piece > 0 && blue_piece > 0) { //if opponent piece was found on mill
                return false;
            }
            if (red_piece == 3 || blue_piece == 3) {
                return true;
            }  // return true if the mill is formed
        }
        return false;
    }
    private boolean checkMillMiddle(int row, int col) {
        int millHorizontalPieces = 1;
        int millVerticalPieces = 1;

        // searches for players piece on neighboring right cols
        millHorizontalPieces += scanNeighbors(row, col, 0, 1);
        // searches for players piece on neighboring left cols
        millHorizontalPieces += scanNeighbors(row, col, 0, -1);

        // searches for players piece on neighboring down rows
        millVerticalPieces += scanNeighbors(row, col, 1, 0);
        // searches for players piece on neighboring up rows
        millVerticalPieces += scanNeighbors(row, col, -1, 0);

        return millHorizontalPieces == 3 || millVerticalPieces == 3;
    }

    private int scanNeighbors(int row, int col, int xDir, int yDir) {
        Cell searchTag = GRID[row][col];
        Cell testPiece = null;
        int millPieces = 0;
        for (int i = 1; i < GRID[0].length; i++) {
            // makes sure the row or col are in range
            try{
                testPiece = GRID[row+(i*xDir)][col+(i*yDir)];
            } catch (ArrayIndexOutOfBoundsException _) {
                // break if row or col leaves range
                break;
            }
            // see if the game piece is the one matching the players game piece
            if (testPiece == searchTag) {
                millPieces++;
            }
            // breaks if an empty game space or another players game piece is found
            else if (testPiece != Cell.INVALID) {
                break;
            }
        }
        return millPieces;
    }
}
