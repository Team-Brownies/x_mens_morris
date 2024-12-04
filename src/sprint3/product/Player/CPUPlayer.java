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
    private final Game game;
    private Difficulty difficulty;

    // changes player so a cpu can control
    public CPUPlayer(char color, Game game, int difficulty) {
        super(color, game);
        setCpu(true);
        this.game = this.getGame();
        setDifficulty(difficulty);
    }

    public CPUPlayer(char color, Game game, boolean startDisable) {
        this(color, game, 3);
        // starts as disabled to set up board
        setCpu(!startDisable);
    }

    // sets Difficulty of the CPU Player
    public void setDifficulty(int d) {
        this.difficulty = switch (d) {
            case 3 -> Difficulty.HARD;
            case 2 -> Difficulty.NORMAL;
            default -> Difficulty.EASY;
        };
    }



    // generates random number to pick from list of valid moves
    private int getRandom(int n){
        int random;

        random = (int) (Math.random() * n);
        return random;
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
            millMove = findMillMove(move, game.makeTempGrid(), this.getPlayerTag());
            if (blockMove == null) {
                //Saves a block move for later
                Cell[][] tempGrid = game.makeTempGrid();
                blockMove = findMillMove(move, tempGrid, this.getOpponentTag());
            }
            //Form First Mill found
            if (millMove!=null)
                break;
        }

        randomMove =possibleMoves.get(getRandom(possibleMoves.size()));
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

        return getMoveBaseOnDifficulty(millMove, blockMove, randomMove);
    }


    // generates coords the best remove to make this turn
    private int[] genRemove(){
        int[] removePiece;
        int[] randomRemove = getRandomRemovePiece();
        int[] blockRemove = getNonTrappedRemove();
        GameState oppGameState = game.getOpponentPlayer().getPlayersGamestate();

        removePiece = switch (oppGameState) {
            case PLACING -> getRemovePieceForPlacing();
            case MOVING, FLYING -> getRemovePieceForMoving();
            default -> null;
        };



        // remove a piece that can
        return getMoveBaseOnDifficulty(removePiece, blockRemove, randomRemove);
    }
    // returns a random piece that is able to be removed
    private int[] getRandomRemovePiece() {
        List<int[]> oppRemovablePieces = new ArrayList<>();
        List<int[]> oppGameSpaces = game.getAllCellsOfAType(this.getOpponentTag());

        for (int[] piece:oppGameSpaces){
            if(validForRemove(piece)){
                    oppRemovablePieces.add(piece);
            }
        }
        return oppRemovablePieces.get(getRandom(oppRemovablePieces.size()));
    }

    private int[] getNonTrappedRemove(){
        List<int[]> oppRemovablePieces = new ArrayList<>();
        Player oppPlayer = game.getOpponentPlayer();

        List<GamePiece> movablePieces = playersMovablePieces(oppPlayer);

        for (GamePiece piece:movablePieces){
            int[] loc = piece.getLocation();
            if(validForRemove(loc)){
                oppRemovablePieces.add(loc);
            }
        }
        return oppRemovablePieces.get(getRandom(oppRemovablePieces.size()));

    }

    private boolean validForRemove(int[] piece){
        CheckMill gameMillChecker = new CheckMill(game.getGrid());
        return !(gameMillChecker.checkMillAllDirections(piece[0], piece[1]) &&
                game.getOppFreePieces());
    }
    // return a mill mate of a mill the Opponent could make there next turn (during placing phase)
    private int[] getRemovePieceForPlacing() {
        List<int[]> possibleMoves = game.getAllCellsOfAType(Cell.EMPTY);
        possibleMoves.addAll(game.getAllCellsOfAType(Cell.MOVEVALID));
        int[] oppMillMove = null;

        //Find if Mill can be formed by Opponent Next Turn
        for (int[] move : possibleMoves ) {
            oppMillMove = findMillMove(move, game.makeTempGrid(), this.getOpponentTag());
            if (oppMillMove!= null)
                break;
        }
        return getMillMatesForRemove(oppMillMove);
    }
    // return a mill mate of a mill the Opponent could make there next turn (during moving phase)
    private int[] getRemovePieceForMoving(){
        int[][] possibleMillMove = findMillOrBlock(this.game.getOpponentPlayer(), "Mill");

        return getMillMatesForRemove(possibleMillMove);
    }
    // handle to convect to 2d array
    private int[] getMillMatesForRemove(int[] possibleMillMove) {
       return getMillMatesForRemove(new int[][]{possibleMillMove});
    }
    // return a mill mate of a mill the Opponent could make there next turn
    private int[] getMillMatesForRemove(int[][] possibleMillMove){
        int[] removePiece = null;
        Cell[][] tempGrid = game.makeTempGrid();
        CheckMill testMillChecker = new CheckMill(tempGrid);
        CheckMill gameMillChecker = new CheckMill(game.getGrid());
        Set<int[]> millMates = new HashSet<>();
        Cell oppTag = this.getOpponentTag();

        if (possibleMillMove!=null && possibleMillMove[0]!=null){
            // set up for moving to form mill
            if (possibleMillMove.length>1) {
                tempGrid[possibleMillMove[0][0]][possibleMillMove[0][1]] = Cell.EMPTY;
                tempGrid[possibleMillMove[1][0]][possibleMillMove[1][1]] = oppTag;
                millMates.addAll(testMillChecker.getMillMates(possibleMillMove[1][0], possibleMillMove[1][1]));
            }
            // set up for placing to form mill
            else {
                tempGrid[possibleMillMove[0][0]][possibleMillMove[0][1]] = oppTag;
                millMates.addAll(testMillChecker.getMillMates(possibleMillMove[0][0], possibleMillMove[0][1]));
            }

            // finds piece that isn't in another mill
            removePiece = millMates.stream()
                    .filter(piece -> !gameMillChecker.checkMillAllDirections(piece[0], piece[1]))
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
        switch (gameState) {
            case PLACING:
                placingLogic();
                break;
            case MOVING, FLYING:
                movingLogic();
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

    // moves or flies a game piece
    private void movingLogic() {
        int[][] movingCoords = this.genMove();
        int[] placingGP = new int[]{movingCoords[1][0], movingCoords[1][1]};
        int[] movingGP = new int[]{movingCoords[0][0], movingCoords[0][1]};
        this.movePiece(placingGP[0], placingGP[1], movingGP[0], movingGP[1]);
    }

    // remove a game piece
    private void removeLogic() {
        int[] removingGP = this.genRemove();
        this.removePiece(removingGP[0], removingGP[1]);
    }

}