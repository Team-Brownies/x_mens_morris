package sprint3.product.Player;

import sprint3.product.Cell;
import sprint3.product.CheckMill;
import sprint3.product.GUI.Board;
import sprint3.product.GUI.GameSpace;
import sprint3.product.Game.Game;
import sprint3.product.Game.GameState;
import sprint3.product.GamePiece;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CPUPlayer extends Player{
    private Game game;
    private int gridSize;
    private boolean cpu;

    public CPUPlayer(char color, int pieces, Game game) {
        super(color, pieces, game);
        setCpu(true);
        this.game = this.getGame();
        this.gridSize = this.game.getSize();
    }

    public int[] findMillMove(int[] move, Cell[][] tempGrid, Cell tag){
        CheckMill millChecker;
        tempGrid[move[0]][move[1]] = tag;

        millChecker = new CheckMill(tempGrid);

        if (millChecker.checkMillAllDirections(move[0], move[1])) {
            return move;
        }
        return null;
    }

    public List<int[]> getValidCells(Cell cellType){
        Cell[][] grid = this.game.getGrid();
        List<int[]> cells = new ArrayList<>();

        for (int row = 0; row < this.gridSize; row++) {
            for (int col = 0; col < this.gridSize; col++) {
                if (grid[row][col] == cellType) {
                    cells.add(new int[]{row, col});
                }
            }
        }
        return cells;
    }

    public int getRandom(int n){
        int random = 0;

        random = (int) (Math.random() * n);
        return random;
    }
    private Cell[][] makeTempGrid(){
        Cell[][] grid = this.game.getGrid();
        return Arrays.stream(grid)
                .map(Cell[]::clone)
                .toArray(Cell[][]::new);
    }

    public int[] genPlace(){
        int[] millMove = null;
        int[] blockMove = null;

        List<int[]> possibleMoves = getValidCells(Cell.EMPTY);
        possibleMoves.addAll(getValidCells(Cell.MOVEVALID));

        //Find if Mill can be formed
        for (int[] move : possibleMoves ) {
            millMove = findMillMove(move, makeTempGrid(), this.getPlayerTag());
            if (blockMove == null) {
                Cell[][] tempGrid =makeTempGrid();
                blockMove = findMillMove(move, tempGrid, this.getOpponentTag());

                System.out.println(tempGrid[0][0]+" "+tempGrid[0][3]+" "+tempGrid[0][6]);
            }
            System.out.println("place block move "+Arrays.toString(blockMove));
            //Form First Mill found
            if (millMove!=null)
                return millMove;
        }
        //Block if no Mills found
        if (blockMove!=null)
            return blockMove;
        //Random Move if no Mills or Blocks are found
        return  possibleMoves.get(getRandom(possibleMoves.size()));
    }

    public int[][] genMove(){
        List<GamePiece> playersBoardPieces = this.getBoardPieces();
        List<GamePiece> moveablePieces = new ArrayList<>();
        int[] millMove = null;
        int[] blockMove = null;
        int[][] returnBlocking = null;

        //Update List of Valid Moves Locations for each game Piece
        for (GamePiece piece : playersBoardPieces){
            piece.updateValidMovesLocations();
            if(!piece.getValidMovesLocations().isEmpty()) {
                moveablePieces.add(piece);
            }
        }

        //Checks to see if a mill can be form or block
        for (GamePiece piece : playersBoardPieces){
            int[] coords = piece.getLocation();
            int[] blockingCoords = null;
            List<int[]> validMoveSpaces = piece.getValidMovesLocations();

            for (int[] newSpace : validMoveSpaces){
                Cell[][] tempGrid = makeTempGrid();
                tempGrid[coords[0]][coords[1]] = Cell.EMPTY;

                millMove = findMillMove(newSpace, tempGrid, this.getPlayerTag());

                tempGrid = makeTempGrid();
                tempGrid[coords[0]][coords[1]] = Cell.EMPTY;

                if (blockMove == null) {
                    blockMove = findMillMove(newSpace, tempGrid, this.getOpponentTag());
                    blockingCoords = coords;
                }

                // Make First Mill
                if (millMove!=null)
                    return new int[][]{coords, millMove};
                // Save a Block Move for if no mill move is found
                if (blockMove!=null && blockingCoords!=null) {
                    returnBlocking = new int[][]{blockingCoords, blockMove};
                }
            }

        }
        // Return Block Move
        if(returnBlocking!= null){
            return returnBlocking;
        }
        // Make random move if no mill or block is possible
        GamePiece randomPiece = moveablePieces.get(getRandom(moveablePieces.size()));
        List<int[]> piecesMoves = randomPiece.getValidMovesLocations();
        int[] randomMove = piecesMoves.get(getRandom(piecesMoves.size()));

        return new int[][]{randomPiece.getLocation(), randomMove};
    }

    public int[] genRemove(){
        Cell[][] grid = this.game.getGrid();
        CheckMill millChecker = new CheckMill(game.getGrid());

        for (int row = 0; row < this.gridSize; row++) {
            for (int col = 0; col < this.gridSize; col++) {
                if (grid[row][col] == this.getOpponentTag()) {
                    if(!(millChecker.checkMillAllDirections(row, col) && game.getOppFreePieces())){
                        return new int[]{row, col};
                    }
                }
            }
        }
        return new int[]{-1, -1};
    }

    public void makeCPUMove() {
        GameState gameState = this.getPlayersGamestate();
        Board gui = getGame().getGui();
        GameSpace gp;
        int[] removingGP;

        gui.updateGameStatus();
        switch (gameState) {
            case PLACING:
                placingLogic();
                break;
            case MOVING:
                movingLogic();
                break;
            case FLYING:
                flyingLogic();
                break;
            case MILLING:
                removingGP = this.genRemove();
                gp = gui.getGameSpace(removingGP[0], removingGP[1]);
                this.removePiece(removingGP[0], removingGP[1]);
                break;
        }
    }

    private void placingLogic() {
        int[] placingGP = this.genPlace();
        this.placePiece(placingGP[0], placingGP[1]);
    }

    private void movingLogic() {
        int[][] movingCoords = this.genMove();
        int[] placingGP = new int[]{movingCoords[1][0], movingCoords[1][1]};
        int[] movingGP = new int[]{movingCoords[0][0], movingCoords[0][1]};
        this.movePiece(placingGP[0], placingGP[1], movingGP[0], movingGP[1]);
    }

    private void flyingLogic() {
        List<GamePiece> pieces = this.getBoardPieces();
        for (GamePiece p : pieces){
            p.setCellStateForFlying();
        }
        movingLogic();
    }

}