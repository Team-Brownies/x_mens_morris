package sprint3.product.Player;

import sprint3.product.Cell;
import sprint3.product.CheckMill;
import sprint3.product.Game.Game;
import sprint3.product.Game.GameState;
import sprint3.product.GamePiece;

import java.util.*;

public class CPUPlayer extends Player{
    private enum Difficulty {
        EASY, NORMAL, HARD
    }
    private Game game;
    private int gridSize;
    private Difficulty difficulty;

    // changes player so a cpu can control
    public CPUPlayer(char color, Game game) {
        super(color, game);
        setCpu(true);
        this.game = this.getGame();
        this.gridSize = this.game.getSize();
        setDifficulty(3);
    }

    public CPUPlayer(char color, Game game, boolean startDisable) {
        this(color, game);
        // starts as disabled to set up board
        setCpu(false);
        // starts at hardest difficulty
        setDifficulty(3);
    }

    // sets Difficulty of the CPU Player
    public void setDifficulty(int d) {
        if (d>=3)
            this.difficulty=Difficulty.HARD;
        else if(d==2)
            this.difficulty=Difficulty.NORMAL;
        else
            this.difficulty=Difficulty.EASY;
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
        int[] randomMove;

        List<int[]> possibleMoves = game.getAllCellsOfAType(Cell.EMPTY);
        possibleMoves.addAll(game.getAllCellsOfAType(Cell.MOVEVALID));

        //Find if Mill can be formed
        for (int[] move : possibleMoves ) {
            millMove = findMillMove(move, makeTempGrid(), this.getPlayerTag());
            if (blockMove == null) {
                //Saves a block move for later
                Cell[][] tempGrid =makeTempGrid();
                blockMove = findMillMove(move, tempGrid, this.getOpponentTag());
            }
            //Form First Mill found
            if (millMove!=null)
                break;
        }

        randomMove =possibleMoves.get(getRandom(possibleMoves.size()));
        System.out.println("mill: "+ Arrays.toString(millMove) +" block: "+ Arrays.toString(blockMove) +" random: "+ Arrays.toString(randomMove));
        return getMoveBaseOnDifficulty(millMove, blockMove, randomMove);
    }

    // generates coords the best move to make this turn
    private int[][] genMove(){
        List<int[]> piecesMoves;
        GamePiece randomPiece;
        int[] randomPiecesMove;
        int[][] millMove;
        int[][] blockMove = null;
        int[][] randomMove;

        List<GamePiece> movablePieces = playersMovablePieces(this);
        // Find Mill if found
        millMove = findMillOrBlock(this, "Mill");
        // Find Block
        if (millMove==null) {
            blockMove = findMillOrBlock(this, "Block");
        }
        // Find random move
        randomPiece = movablePieces.get(getRandom(movablePieces.size()));
        piecesMoves = randomPiece.getValidMovesLocations();
        randomPiecesMove = piecesMoves.get(getRandom(piecesMoves.size()));
        randomMove = new int[][]{randomPiece.getLocation(), randomPiecesMove};

        System.out.println("mill: "+ Arrays.deepToString(millMove) +" block: "+ Arrays.deepToString(blockMove) +" random: "+ Arrays.deepToString(randomMove));

        return getMoveBaseOnDifficulty(millMove, blockMove, randomMove);
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
    // find what piece that when moved can result in a Mill or Block
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

    // generates coords the best remove to make this turn
    private int[] genRemove(){
        int[] removePiece;
        int[] randomRemove = getRandomRemovePiece();
        GameState oppGameState = game.getOpponentPlayer().getPlayersGamestate();

        removePiece = switch (oppGameState) {
            case PLACING -> getRemovePieceForPlacing();
            case MOVING, FLYING -> getRemovePieceForMoving();
            default -> null;
        };

        // remove a piece that can
        return getMoveBaseOnDifficulty(null, removePiece, randomRemove);
    }
    // returns a random piece that is able to be removed
    private int[] getRandomRemovePiece() {
        CheckMill gameMillCecker = new CheckMill(game.getGrid());

        List<int[]> oppRemovablePieces = new ArrayList<>();
        List<int[]> oppGameSpaces = game.getAllCellsOfAType(this.getOpponentTag());
        for (int[] piece:oppGameSpaces){
            if(!(gameMillCecker.checkMillAllDirections(piece[0], piece[1]) && game.getOppFreePieces())){
                oppRemovablePieces.add(new int[]{piece[0], piece[1]});
            }
        }
        return oppRemovablePieces.get(getRandom(oppRemovablePieces.size()));
    }
    // return a mill mate of a mill the Opponent could make there next turn (during placing phase)
    private int[] getRemovePieceForPlacing() {
        List<int[]> possibleMoves = game.getAllCellsOfAType(Cell.EMPTY);
        possibleMoves.addAll(game.getAllCellsOfAType(Cell.MOVEVALID));
        int[] oppMillMove = null;

        //Find if Mill can be formed by Opponent Next Turn
        for (int[] move : possibleMoves ) {
            oppMillMove = findMillMove(move, makeTempGrid(), this.getOpponentTag());
            if (oppMillMove!= null)
                break;
        }
        return getMillMatesForRemove(oppMillMove);
    }
    // return a mill mate of a mill the Opponent could make there next turn (during moving phase)
    private int[] getRemovePieceForMoving(){
        int[][] possibleMillMove = findMillOrBlock(this.game.getOpponentPlayer(), "Mill");
        System.out.println("removePiece");
        System.out.println(Arrays.toString(getMillMatesForRemove(possibleMillMove)));
        return getMillMatesForRemove(possibleMillMove);
    }
    // handle to convect to 2d array
    private int[] getMillMatesForRemove(int[] possibleMillMove) {
       return getMillMatesForRemove(new int[][]{possibleMillMove});
    }
    // return a mill mate of a mill the Opponent could make there next turn
    private int[] getMillMatesForRemove(int[][] possibleMillMove){
        int[] removePiece = null;
        Cell[][] tempGrid = makeTempGrid();
        CheckMill testMillChecker = new CheckMill(tempGrid);
        CheckMill gameMillCecker = new CheckMill(game.getGrid());
        Set<int[]> millMates = new HashSet<>();
        Cell oppTag = this.getOpponentTag();

        System.out.println(Arrays.deepToString(possibleMillMove));

        if (possibleMillMove!=null && possibleMillMove[0]!=null){
            // set up for moving to form mill
            if (possibleMillMove.length>1) {
                tempGrid[possibleMillMove[0][0]][possibleMillMove[0][1]] = Cell.EMPTY;
                tempGrid[possibleMillMove[1][0]][possibleMillMove[1][1]] = oppTag;
                millMates.addAll(testMillChecker.getMillMates(possibleMillMove[1][0], possibleMillMove[1][1]));
            }
            // set up for placing to form mill
            else {
                System.out.println(possibleMillMove[0][0]+" "+possibleMillMove[0][1]);
                tempGrid[possibleMillMove[0][0]][possibleMillMove[0][1]] = oppTag;
                millMates.addAll(testMillChecker.getMillMates(possibleMillMove[0][0], possibleMillMove[0][1]));
            }

            // finds piece that isn't in another mill
            removePiece = millMates.stream()
                    .filter(piece -> !gameMillCecker.checkMillAllDirections(piece[0], piece[1]))
                    .findFirst()
                    .orElse(null);  // returns null if no matching element is found
        }
        return removePiece;
    }
    // handler for placing and removing a game place on a certain difficulty
    private int[] getMoveBaseOnDifficulty(int[] millMove, int[] blockMove, int[] randomMove) {
        int[][] returnStatement = getMoveBaseOnDifficulty(
                new int[][]{millMove},
                new int[][]{blockMove},
                new int[][]{randomMove}
        );
        return returnStatement[0];
    }
    // make the correct move based on certain
    private int[][] getMoveBaseOnDifficulty(int[][] millMove, int[][] blockMove, int[][] randomMove) {
        switch (this.difficulty) {
            case HARD:
                // Attempt to form a mill if possible
                // Skips if the game state is in milling
                if (millMove != null && millMove[0] != null) {
                    return millMove;
                }
                // Block the opponent if no mill can be formed
                // Blocks Mill from be formed next turn if
                // game state is milling
                if (blockMove != null && blockMove[0] != null) {
                    return blockMove;
                }
            case NORMAL:
                // Prioritize forming a mill
                if (millMove != null && millMove[0] != null) {
                    return millMove;
                }
            default:
                // Default to a random move if difficulty is unrecognized or Mill and block or null
                return randomMove;
        }
    }

    // find what type of move to make based on the game state
    @Override
    public void makeCPUMove() {
        GameState gameState = this.getPlayersGamestate();
//        Board gui = getGame().getGui();
//
//        gui.updateGameStatus();
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
                removeLogic();
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

    // remove a game piece
    private void removeLogic() {
        int[] removingGP = this.genRemove();
        this.removePiece(removingGP[0], removingGP[1]);
    }

}