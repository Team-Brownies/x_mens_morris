package sprint3.product.GUI;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.util.Duration;
import java.io.File;

//Change this import for every sprint to render the board


public class NewGameScreen {
    private Stage primaryStage;
    private Scene scene;

    private Button gridSizeButton5x5;
    private Button gridSizeButton9x9;
    private Button gameModeButtonP1vsP2;
    private Button gameModeButtonP1vsPC;

    private Button selectedGridButton;
    private Button selectedGameModeButton;
    int gridSelection = 9;
    // Constructor
    public NewGameScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Show the New Game screen with the animation and options
    public void showNewGameScreen() {
        // Title text as a single string
        String titleText = "New Game";

        // Create a container for individual letters (Text nodes)
        HBox titleBox = new HBox(5); // Spacing between letters
        titleBox.setAlignment(Pos.CENTER); // Centering the text in the container

        // Create individual Text nodes for each letter
        for (char c : titleText.toCharArray()) {
            Text letter = new Text(String.valueOf(c));
            letter.setStyle("-fx-font-size: 72px; -fx-font-weight: bold;");
            letter.setFill(Color.WHITE);
            letter.setEffect(new DropShadow(3.0, 3.0, 3.0, Color.BLACK)); // Drop shadow for better contrast
            titleBox.getChildren().add(letter);
        }

        // Apply the wave animation to each letter
        createWaveAnimation(titleBox);

        // Button for Go Back at the bottom
        Button goBackButton = createButton("Go Back");
        goBackButton.setOnAction(e -> goBackToHomeScreen());

        // "Choose Grid Size" section
        Text gridSizeTitle = new Text("Grid Size");
        gridSizeTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        gridSizeTitle.setFill(Color.BLACK);
//        gridSizeTitle.setEffect(new DropShadow(3.0, 3.0, 3.0, Color.BLACK)); // Drop shadow for better contrast

        // Grid size buttons within a bordered box
        HBox gridSizeLayout = new HBox(10);
        gridSizeLayout.setAlignment(Pos.CENTER);
        gridSizeLayout.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2px; -fx-padding: 10px; -fx-background-radius: 10px;");

        gridSizeButton5x5 = createOptionButton("5x5");
        gridSizeButton5x5.setOnAction(e -> chooseGridSize("5x5"));
        gridSizeButton9x9 = createOptionButton("9x9");
        gridSizeButton9x9.setOnAction(e -> chooseGridSize("9x9"));

        // Set default selection for 9x9
        selectedGridButton = gridSizeButton9x9;
        gridSizeButton9x9.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: black; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-border-color: white; -fx-border-width: 2px;");

        gridSizeLayout.getChildren().addAll(gridSizeTitle, gridSizeButton5x5, gridSizeButton9x9);

        // "Choose Game Mode" section
        Text gameModeTitle = new Text("Choose Player");
        gameModeTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        gameModeTitle.setFill(Color.BLACK);
//        gameModeTitle.setEffect(new DropShadow(3.0, 3.0, 3.0, Color.BLACK)); // Drop shadow for better contrast

        // Game mode buttons within a bordered box
        HBox gameModeLayout = new HBox(10);
        gameModeLayout.setAlignment(Pos.CENTER);
        gameModeLayout.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2px; -fx-padding: 10px; -fx-background-radius: 10px;");

        gameModeButtonP1vsP2 = createOptionButton("P1 vs P2");
        gameModeButtonP1vsP2.setOnAction(e -> chooseGameMode("P1 vs P2"));
        gameModeButtonP1vsPC = createOptionButton("P1 vs PC");
        gameModeButtonP1vsPC.setOnAction(e -> chooseGameMode("P1 vs PC"));

        // Set default selection for P1 vs P2
        selectedGameModeButton = gameModeButtonP1vsP2;
        gameModeButtonP1vsP2.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: black; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-border-color: white; -fx-border-width: 2px;");

        gameModeLayout.getChildren().addAll(gameModeTitle, gameModeButtonP1vsP2, gameModeButtonP1vsPC);

        // Create a container for the Play button
        Button playButton = createButton("Play Game ➔");
        playButton.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: #444444; -fx-text-fill: white; -fx-padding: 10px 20px;");
        playButton.setOnAction(e -> playGame());

        // Create a layout for the Go Back button and Play button at the bottom
        HBox bottomButtonsLayout = new HBox(20);
        bottomButtonsLayout.setAlignment(Pos.CENTER);
        bottomButtonsLayout.getChildren().addAll(goBackButton, playButton);

        // Main layout
        VBox mainLayout = new VBox(40);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(titleBox, gridSizeLayout, gameModeLayout, bottomButtonsLayout);

        // Set the background image for the new game screen
        setBackgroundImage(mainLayout);

        // Scene setup
        scene = new Scene(mainLayout, 800, 600); // Set initial window size
        primaryStage.setTitle("New Game - Nine Men's Morris");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);  // Allow resizing of the window
        primaryStage.show();
    }

    // Helper method to set the background image for the New Game screen
    private void setBackgroundImage(Pane layout) {
        File imageFile = new File("x_mens_morris/src/nmmBg.jpg"); // Image located in the root directory
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString());
            BackgroundImage backgroundImage = new BackgroundImage(image,
                    BackgroundRepeat.NO_REPEAT,   // No repeat horizontally
                    BackgroundRepeat.NO_REPEAT,   // No repeat vertically
                    BackgroundPosition.CENTER,    // Center the image
                    new BackgroundSize(100, 100, false, false, true, true)); // Scale image to fit window
            layout.setBackground(new Background(backgroundImage));
        } else {
            System.out.println("Image file not found.");
        }
    }

    // Action for going back to the home screen
    private void goBackToHomeScreen() {
        Main homeScreen = new Main();
        homeScreen.start(primaryStage);
    }

    // Action for choosing grid size (5x5 or 9x9)
    private void chooseGridSize(String gridSize) {
        System.out.println("Grid Size Selected: " + gridSize);
        updateButtonStyle(gridSizeButton5x5, gridSizeButton9x9, gridSize.equals("5x5") ? gridSizeButton5x5 : gridSizeButton9x9);

        gridSelection = gridSize.equals("9x9") ? 9 : 5;
    }

    // Action for choosing game mode (P1 vs P2 or P1 vs PC)
    private boolean chooseGameMode(String gameMode) {
        System.out.println("Game Mode Selected: " + gameMode);
        updateButtonStyle(gameModeButtonP1vsP2, gameModeButtonP1vsPC, gameMode.equals("P1 vs P2") ? gameModeButtonP1vsP2 : gameModeButtonP1vsPC);
        return gameMode.equals("P1 vs P2") ? true : false;
    }

    // Action for playing the game
    private void playGame() {
        System.out.println("Starting the game...");
        Board gui = new Board();
        gui.start(primaryStage);
    }

    // Helper method to create general buttons
    private Button createButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: #444444; -fx-text-fill: white; -fx-padding: 10px 20px;");
        return button;
    }

    // Helper method to create option buttons (with hover effect)
    private Button createOptionButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: white; -fx-text-fill: black; -fx-padding: 10px 20px; -fx-border-color: black; -fx-border-width: 2px;");
        return button;
    }

    // Helper method to update button styles when selected
    private void updateButtonStyle(Button button1, Button button2, Button selectedButton) {
        button1.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: white; -fx-text-fill: black; -fx-padding: 10px 20px; -fx-border-color: black; -fx-border-width: 2px;");
        button2.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: white; -fx-text-fill: black; -fx-padding: 10px 20px; -fx-border-color: black; -fx-border-width: 2px;");
        selectedButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: black; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-border-color: white; -fx-border-width: 2px;");
    }

    // Wave animation for the title text
    private void createWaveAnimation(HBox titleBox) {
        Timeline timeline = new Timeline();
        for (int i = 0; i < titleBox.getChildren().size(); i++) {
            Text letter = (Text) titleBox.getChildren().get(i);
            KeyValue keyValue1 = new KeyValue(letter.scaleXProperty(), 1.2, Interpolator.EASE_BOTH);
            KeyValue keyValue2 = new KeyValue(letter.scaleXProperty(), 1, Interpolator.EASE_BOTH);
            KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(i * 0.1), keyValue1);
            KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(i * 0.1 + 0.3), keyValue2);
            timeline.getKeyFrames().addAll(keyFrame1, keyFrame2);
        }
        timeline.setCycleCount(1);
        timeline.play();
    }
}
