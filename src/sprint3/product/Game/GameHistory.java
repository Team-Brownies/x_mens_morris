
package sprint3.product.Game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import java.io.FileReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class GameHistory {
    private BufferedWriter gameLogWriter;
    private List<String> moveList = new ArrayList<>();
    private static final String TMP_FILE_PATH = "savedGames/tmp.txt";
    private static final String LOG_FILE_PATH = "savedGames/gamelog.txt";
    private GameMode replayGameMode;
    private JsonArray replayPiecesArray;

    public GameHistory() {
        System.out.println("Log file path: " + new File(TMP_FILE_PATH).getAbsolutePath());
        try {
            File logFile = new File(TMP_FILE_PATH);

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

    public void setGameMode(GameMode gameMode) {
        String moveLog = "{\"GameMode\": \""+gameMode+"\", ";

        try {
            gameLogWriter.write(moveLog);
            gameLogWriter.newLine();
            gameLogWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void logMove(String id, int row, int col) {
        String moveLog = "{\"Piece\": \""+id+"\", \"Row\": " + row + ", \"Col\": " + col+"}";
        moveList.add(moveLog);
//        gameHistory.push(moveLog);
//
//        try {
//            gameLogWriter.write(moveLog);
//            gameLogWriter.newLine();
//            gameLogWriter.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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

    private void writeMoves(){
        try{
            gameLogWriter.write("\"Pieces\": [\n");

            for (int i = 0; i < moveList.size(); i++) {
                gameLogWriter.write("  " + moveList.get(i));
                if (i < moveList.size() - 1) {
                    gameLogWriter.write(",\n");
                } else {
                    gameLogWriter.write("\n");
                }
            }
            gameLogWriter.write("]\n");
            gameLogWriter.write("}\n");
            gameLogWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Close the log writer when done
    public void close() {
        try {
            if (gameLogWriter != null) {
                writeMoves();
                Files.copy(Paths.get(TMP_FILE_PATH), Paths.get(LOG_FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File copied successfully!");
                gameLogWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void readFile(){
        try (FileReader reader = new FileReader(LOG_FILE_PATH)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            String gameMode = jsonObject.get("GameMode").getAsString();
            this.replayGameMode = (Objects.equals(gameMode, "NINE")) ? GameMode.NINE : GameMode.SIX;
            this.replayPiecesArray = jsonObject.getAsJsonArray("Pieces");

            System.out.println("GameMode: " + gameMode);
            System.out.println("Pieces: " + replayPiecesArray.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameMode getReplayGameMode() {
        try {
            File logFile = new File(LOG_FILE_PATH);

            if (logFile.exists()) {
                readFile();
                return replayGameMode;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public JsonArray getReplayPiecesArray() {
        return replayPiecesArray;
    }
}
