package sprint2.product;


public class CheckMill {
    private final Cell[][] grid;
    public CheckMill(Cell[][] grid) {
        this.grid = grid;

    }
    public boolean checkMillAllDireactions(int row, int col) {
        //check for mill around the piece in all direations
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
                return true;
            }  // return true if the mill is formed
        }
        return false; //if the loop terminates without retun than its not mill
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
                return true;
            }  // return true if the mill is formed
        }
        return false; //if the loop terminates without retun than its not mill
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
                return true;
            }  // return true if the mill is formed
        }
        return false;
    }
    private boolean checkHorizontalMillMiddle(int row, int col) {
        try{
            return ((grid[row][col - 1] == Cell.RED && grid[row][col] == Cell.RED && grid[row][col + 1] == Cell.RED) ||
                    (grid[row][col - 1] == Cell.BLUE && grid[row][col] == Cell.BLUE  && grid[row][col + 1] == Cell.BLUE)) ||
                    ((grid[row][col - 2] == Cell.RED && grid[row][col] == Cell.RED && grid[row][col + 2] == Cell.RED) ||
                            (grid[row][col - 2] == Cell.BLUE && grid[row][col] == Cell.BLUE  && grid[row][col + 2] == Cell.BLUE)) ||
                    ((grid[row][col - 3] == Cell.RED && grid[row][col] == Cell.RED && grid[row][col + 3] == Cell.RED) ||
                            (grid[row][col- 3] == Cell.BLUE && grid[row][col] == Cell.BLUE && grid[row][col + 3] == Cell.BLUE));
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
    private boolean checkVerticalMillMiddle(int row, int col) {
        try{
            return ((grid[row - 1][col] == Cell.RED && grid[row][col] == Cell.RED && grid[row + 1][col] == Cell.RED) ||
                    (grid[row - 1][col] == Cell.BLUE && grid[row][col] == Cell.BLUE  && grid[row + 1][col] == Cell.BLUE)) ||
                    ((grid[row - 2][col] == Cell.RED && grid[row][col] == Cell.RED && grid[row + 2][col] == Cell.RED) ||
                            (grid[row - 2][col] == Cell.BLUE && grid[row][col] == Cell.BLUE  && grid[row + 2][col] == Cell.BLUE)) ||
                    ((grid[row - 3][col] == Cell.RED && grid[row][col] == Cell.RED && grid[row + 3][col] == Cell.RED) ||
                            (grid[row - 3][col] == Cell.BLUE && grid[row][col] == Cell.BLUE && grid[row + 3][col] == Cell.BLUE));
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
}
