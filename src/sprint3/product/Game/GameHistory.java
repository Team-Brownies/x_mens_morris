
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
    private static final String TMP_FILE_PATH = "savedGames/tmp.txt";
    private static final String LOG_FILE_PATH = "savedGames/gamelog.txt";
    private BufferedWriter gameLogWriter;
    private List<String> moveList = new ArrayList<>();
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
    }

    public void writeMoves(){
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
            this.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Close the log writer when done
    private void close() {
        try {
            if (gameLogWriter != null) {
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
