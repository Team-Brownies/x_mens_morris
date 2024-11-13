package sprint3.product;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheckMill {
    private final Cell[][] grid;
    public CheckMill(Cell[][] grid) {
        this.grid = grid;

    }
    public Set<int[]> getMillMates(int row, int col){
        Set<int[]> millMates = new HashSet<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int[] coords = new int[]{row,col};

        if (checkMillAllDirections(row, col)){
            for (int[] dir : directions) {
                millMates.addAll(searchForMillMates(dir[0], dir[1], coords));
            }
            System.out.println(millMates);
        } else {
            return null;
        }
        return millMates;
    }
    private Set<int[]> searchForMillMates(int xDirection, int yDirection, int[] coords){
        int xMagnitude, yMagnitude;
        int row = coords[0];
        int col = coords[1];
        Cell color = grid[row][col];
        Cell testCell;
        Set<int[]> millMates = new HashSet<>();

        for (int i = 1; i <= 3; i++) {
            xMagnitude = col+(i*xDirection);
            yMagnitude = row+(i*yDirection);
            System.out.println("check: "+yMagnitude+", "+xMagnitude);
            System.out.println("grid.length: "+(grid.length-1));
            if(xMagnitude<grid.length && yMagnitude<grid.length){
                testCell = grid[yMagnitude][xMagnitude];
                if(testCell == color && this.checkMillAllDirections(yMagnitude,xMagnitude)) {
                    millMates.add(new int[]{yMagnitude,xMagnitude});
                    break;
                }
                else if (testCell != Cell.INVALID)
                    break;
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
                checkVerticalMillMiddle(row, col) ||
                checkHorizontalMillMiddle(row, col);
    }
    private boolean checkMillCombo(int red_piece, int blue_piece){
        if(red_piece > 0 && blue_piece > 0){ //if opponent piece was found on mill
            return false;
        }
        return red_piece == 3 || blue_piece == 3; // return true if the mill is formed
    }
    private boolean checkVerticalMillDown(int row, int col) {
        int red_piece = 0;
        int blue_piece = 0;
        int invalid_point = 0;
        for (int i = row; i < grid[0].length; i++) {
            if(grid[i][col] == Cell.RED){
                red_piece++;
            } else if (grid[i][col] == Cell.BLUE) {
                blue_piece++;
            }else if (grid[i][col] == Cell.INVALID || grid[i][col] == null) {
                invalid_point++;
            }
            if(col == 3 && invalid_point != 0){
                break;
            }
            if(red_piece > 0 && blue_piece > 0){ //if opponent piece was found on mill
                return false;
            }
            if(red_piece == 3 || blue_piece == 3){
                System.out.println("checkVerticalMillDown");
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
            if(grid[i][col] == Cell.RED){
                red_piece++;
            } else if (grid[i][col] == Cell.BLUE) {
                blue_piece++;
            }else if (grid[i][col] == Cell.INVALID || grid[i][col] == null) {
                invalid_point++;
            }
            if(col == 3 && invalid_point != 0){
                break;
            }
            if(red_piece > 0 && blue_piece > 0){ //if opponent piece was found on mill
                return false;
            }
            if(red_piece == 3 || blue_piece == 3){
                System.out.println("checkVerticalMillDown");
                return true;
            }  // return true if the mill is formed
        }
        return false; //if the loop terminates without return than it's not mill
    }
    private boolean checkHorizontalMillRight(int row, int col) {
        int red_piece = 0;
        int blue_piece = 0;
        int invalid_point = 0;
        for (int i = col; i < grid[0].length; i++) {

            if(grid[row][i] == Cell.RED){
                red_piece++;
            } else if (grid[row][i] == Cell.BLUE) {
                blue_piece++;
            }else if (grid[row][i] == Cell.INVALID || grid[row][i] == null) {
                invalid_point++;
            }
            if(row == 3 && invalid_point != 0){
                break;
            }
            if(red_piece > 0 && blue_piece > 0){ //if opponent piece was found on mill
                return false;
            }
            if(red_piece == 3 || blue_piece == 3){
                System.out.println("checkVerticalMillDown");
                return true;
            }  // return true if the mill is formed
        }
        return false;
    }
    private boolean checkHorizontalMillLeft(int row, int col) {
        int red_piece = 0;
        int blue_piece = 0;
        int invalid_point = 0;
        for(int i = col; i >=0; i--){
            if(grid[row][i] == Cell.RED){
                red_piece++;
            } else if (grid[row][i] == Cell.BLUE) {
                blue_piece++;
            }else if (grid[row][i] == Cell.INVALID || grid[row][i] == null) {
                invalid_point++;
            }
            if(row == 3 && invalid_point != 0){
                break;
            }
            if(red_piece > 0 && blue_piece > 0){ //if opponent piece was found on mill
                return false;
            }
            if(red_piece == 3 || blue_piece == 3){
                System.out.println("checkVerticalMillDown");
                return true;
            }  // return true if the mill is formed
        }
        return false;
    }
    private boolean checkHorizontalMillMiddle(int row, int col) {
        boolean returnStatment = false;
        try{
            returnStatment = ((grid[row][col - 1] == Cell.RED && grid[row][col] == Cell.RED && grid[row][col + 1] == Cell.RED) ||
                    (grid[row][col - 1] == Cell.BLUE && grid[row][col] == Cell.BLUE  && grid[row][col + 1] == Cell.BLUE)) ||
                    ((grid[row][col - 2] == Cell.RED && grid[row][col] == Cell.RED && grid[row][col + 2] == Cell.RED) ||
                            (grid[row][col - 2] == Cell.BLUE && grid[row][col] == Cell.BLUE  && grid[row][col + 2] == Cell.BLUE)) ||
                    ((grid[row][col - 3] == Cell.RED && grid[row][col] == Cell.RED && grid[row][col + 3] == Cell.RED) ||
                            (grid[row][col- 3] == Cell.BLUE && grid[row][col] == Cell.BLUE && grid[row][col + 3] == Cell.BLUE));
        } catch (ArrayIndexOutOfBoundsException _) {
        }
        if (returnStatment)
            System.out.println("checkHorizontalMillMiddle");

        return returnStatment;
    }
    private boolean checkVerticalMillMiddle(int row, int col) {
        boolean returnStatment = false;
        try{
            returnStatment = ((grid[row - 1][col] == Cell.RED && grid[row][col] == Cell.RED && grid[row + 1][col] == Cell.RED) ||
                    (grid[row - 1][col] == Cell.BLUE && grid[row][col] == Cell.BLUE  && grid[row + 1][col] == Cell.BLUE)) ||
                    ((grid[row - 2][col] == Cell.RED && grid[row][col] == Cell.RED && grid[row + 2][col] == Cell.RED) ||
                            (grid[row - 2][col] == Cell.BLUE && grid[row][col] == Cell.BLUE  && grid[row + 2][col] == Cell.BLUE)) ||
                    ((grid[row - 3][col] == Cell.RED && grid[row][col] == Cell.RED && grid[row + 3][col] == Cell.RED) ||
                            (grid[row - 3][col] == Cell.BLUE && grid[row][col] == Cell.BLUE && grid[row + 3][col] == Cell.BLUE));
        } catch (ArrayIndexOutOfBoundsException _) {
        }
        if (returnStatment)
            System.out.println("checkVerticalMillMiddle");

        return returnStatment;
    }
}
