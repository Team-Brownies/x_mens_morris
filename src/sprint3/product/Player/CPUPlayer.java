package sprint3.product.Player;

import sprint3.product.Cell;
import sprint3.product.CheckMill;
import sprint3.product.GUI.Board;
import sprint3.product.GUI.GameSpace;
import sprint3.product.Game.Game;
import sprint3.product.Game.GameState;
import sprint3.product.GamePiece;

import java.util.*;

public class CPUPlayer extends Player{
    private Game game;
    private int gridSize;

    // changes player so a cpu can control
    public CPUPlayer(char color, int pieces, Game game) {
        super(color, pieces, game);
        setToCpu();
        this.game = this.getGame();
        this.gridSize = this.game.getSize();
    }

    // find a move that will result in a mill be formed
    private int[] findMillMove(int[] move, Cell[][] tempGrid, Cell tag){
        CheckMill millChecker;
        tempGrid[move[0]][move[1]] = tag;

        millChecker = new CheckMill(tempGrid);

        if (millChecker.checkMillAllDirections(move[0], move[1])) {
            return move;
        }
        return null;
    }

    // generates random number to pick from list of valid moves
    private int getRandom(int n){
        int random = 0;

        random = (int) (Math.random() * n);
        return random;
    }

    // make a temp grid to test outcome of a possible move
    private Cell[][] makeTempGrid(){
        Cell[][] grid = this.game.getGrid();
        return Arrays.stream(grid)
                .map(Cell[]::clone)
                .toArray(Cell[][]::new);
    }

    // generates coords the best move to make this turn
    private int[] genPlace(){
        int[] millMove = null;
        int[] blockMove = null;

        List<int[]> possibleMoves = game.getAllCellsOfAType(Cell.EMPTY);
        possibleMoves.addAll(game.getAllCellsOfAType(Cell.MOVEVALID));

        //Find if Mill can be formed
        for (int[] move : possibleMoves ) {
            millMove = findMillMove(move, makeTempGrid(), this.getPlayerTag());
            if (blockMove == null) {
                Cell[][] tempGrid =makeTempGrid();
                blockMove = findMillMove(move, tempGrid, this.getOpponentTag());
            }
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

    // generates coords the best move to make this turn
    private int[][] genMove(){
        List<GamePiece> playersBoardPieces = this.getBoardPieces();
        int[] millMove = null;
        int[] blockMove = null;
        int[][] returnStatement = null;

        List<GamePiece> movablePieces = playersMovablePieces(this);
        // return Mill if found
        returnStatement = findMillOrBlock(this, "Mill");
        // if mill is not found search for a block
        if (returnStatement==null) {
            returnStatement = findMillOrBlock(this, "Block");
        }
        // Make random move if no mill or block is possible
        if (returnStatement==null) {
            GamePiece randomPiece = movablePieces.get(getRandom(movablePieces.size()));
            List<int[]> piecesMoves = randomPiece.getValidMovesLocations();
            int[] randomMove = piecesMoves.get(getRandom(piecesMoves.size()));
            return new int[][]{randomPiece.getLocation(), randomMove};
        }

        return returnStatement;
    }

    private int[][] findMillOrBlock(Player player, String type) {
        int[] millMove = null;
        int[] blockMove = null;
        List<GamePiece> movablePieces = playersMovablePieces(player);

        Cell playerTag = player.getPlayerTag();
        Cell oppTag = player.getOpponentTag();


        for (GamePiece piece : movablePieces){
            int[] coords = piece.getLocation();
            List<int[]> validMoveSpaces = piece.getValidMovesLocations();

            for (int[] newSpace : validMoveSpaces){
                Cell[][] tempGrid = makeTempGrid();
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

    // generates coords the best move to make this turn
    private int[] genRemove(){
        Set<int[]> millMates = new HashSet<>();
        Cell[][] tempGrid = makeTempGrid();
        CheckMill millChecker = new CheckMill(tempGrid);
        Cell[][] grid = this.game.getGrid();
        Cell oppTag = this.getOpponentTag();
        int[] removePiece;

        int[][] possibleMillMove = findMillOrBlock(this.game.getOpponentPlayer(), "Mill");
        System.out.println("possibleMillMove");
        System.out.println(Arrays.deepToString(possibleMillMove));
        if (possibleMillMove!=null){
            tempGrid[possibleMillMove[0][0]][possibleMillMove[0][1]] = Cell.EMPTY;
            tempGrid[possibleMillMove[1][0]][possibleMillMove[1][1]] = oppTag;
            millMates.addAll(millChecker.getMillMates(possibleMillMove[1][0], possibleMillMove[1][1]));
            System.out.println("millMates");
            System.out.println(millMates);
            removePiece = millMates.stream().findFirst().orElse(null);
            System.out.println("Arrays.toString(removePiece)");
            System.out.println(Arrays.toString(removePiece));

            return removePiece;
        }
        List<int[]> emptyGameSpaces = game.getAllCellsOfAType(Cell.EMPTY);
//        List<int[]> potentialMills = new ArrayList<>;
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

    // find what type of move to make based on the game state
    @Override
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

    // places a game piece
    private void placingLogic() {
        int[] placingGP = this.genPlace();
        this.placePiece(placingGP[0], placingGP[1]);
    }

    // moves a game piece
    private void movingLogic() {
        int[][] movingCoords = this.genMove();
        int[] placingGP = new int[]{movingCoords[1][0], movingCoords[1][1]};
        int[] movingGP = new int[]{movingCoords[0][0], movingCoords[0][1]};
        this.movePiece(placingGP[0], placingGP[1], movingGP[0], movingGP[1]);
    }

    // fly a game piece
    private void flyingLogic() {
        List<GamePiece> pieces = this.getBoardPieces();
        for (GamePiece p : pieces){
            p.setCellStateForFlying();
        }
        movingLogic();
    }

}