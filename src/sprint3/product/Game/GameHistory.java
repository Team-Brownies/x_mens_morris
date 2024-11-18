
package sprint3.product;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import sprint3.product.GUI.Board;
import sprint3.product.GUI.GameSpace;


public class GameHistory {
    private Deque<String> gameHistory = new LinkedList<String>();
    private BufferedWriter gameLogWriter;
    private static final String LOG_FILE_PATH = "x_mens_morris/src/gamelog.txt";
//    private Board board;  // Reference to GUI board for move actions
    private GameSpace gameSpace;

    public GameHistory(GameSpace board) {
        this.gameSpace = board;  // Store the board reference

        try {
            File logFile = new File(LOG_FILE_PATH);

            // Check if the file exists and clear it (or create a new one if it doesn't)
            if (logFile.exists()) {
                gameLogWriter = new BufferedWriter(new FileWriter(logFile, false)); // false = overwrite
            } else {
                gameLogWriter = new BufferedWriter(new FileWriter(logFile, true)); // true = append mode
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logMove(GameSpace game) {
        String moveLog = "Row: " + game.getRow() + ", Col: " + game.getCol();

        gameHistory.push(moveLog);

        try {
            gameLogWriter.write(moveLog);
            gameLogWriter.newLine();
            gameLogWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Undo the last move (example implementation)
//    public boolean undoMove(Game game) {
//        if (!gameHistory.isEmpty()) {
//            String lastMove = gameHistory.pop();
//            String[] moveParts = lastMove.split(", ");
//            int row = Integer.parseInt(moveParts[0].split(": ")[1]);
//            int col = Integer.parseInt(moveParts[1].split(": ")[1]);
//            String stateStr = moveParts[2].split(": ")[1];
//            char playerTurn = moveParts[3].split(": ")[1].charAt(0);
//
//            switch (stateStr) {
//                case "PLACING":
//                    // Undo placing a piece (example logic)
//                    System.out.println("Undoing PLACING at Row: " + row + ", Col: " + col);
//                    board.deletePiece(row, col); // Call a method on Board to delete the piece
//                    break;
//                case "MOVING":
//                case "FLYING":
//                    // Undo moving or flying
//                    System.out.println("Undoing MOVING/FLYING");
//                    board.updateCells();  // Refresh the board
//                    board.updateGameStatus();  // Update the game status
//                    break;
//                case "MILLING":
//                    System.out.println("Undoing MILLING");
//                    break;
//                default:
//                    System.out.println("Unknown game state: " + stateStr);
//                    return false;
//            }
//
//            return true;
//        }
//
//        System.out.println("No moves to undo.");
//        return false;
//    }

    // Close the log writer when done
    public void close() {
        try {
            if (gameLogWriter != null) {
                gameLogWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
