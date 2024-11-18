package sprint3.product.GUI;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.io.File;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        System.out.println("Working directory: " + System.getProperty("user.dir"));

        this.primaryStage = primaryStage;

        // Title text as a single string
        String titleText = "Nine Men's Morris";

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

        // "by Team Brownies" text directly below the title
        Text creditText = new Text("by Team Brownies");
        creditText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        creditText.setTextAlignment(TextAlignment.CENTER);

        // Add a brown drop shadow effect for the credit text
        DropShadow brownShadow = new DropShadow();
        brownShadow.setOffsetX(2.0);
        brownShadow.setOffsetY(2.0);
        brownShadow.setColor(Color.BROWN);  // Brown shadow color
        brownShadow.setRadius(5.0);
        creditText.setEffect(brownShadow);

        // Set the credit text color to white
        creditText.setFill(Color.WHITE);

        // Button for New Game
        Button newGameButton = createButton("New Game");
        newGameButton.setOnAction(e -> openNewGameScreen());

        // Button for Viewing Last Game Replay
        Button replayButton = createButton("View Last Game Replay");
        replayButton.setOnAction(e -> viewReplay());

        // Create a layout for buttons (HBox) and center them at the bottom of the screen
        HBox buttonLayout = new HBox(30);
        buttonLayout.setAlignment(Pos.BOTTOM_CENTER);
        buttonLayout.getChildren().addAll(newGameButton, replayButton);

        // Create the main layout (VBox) with the title at the top, the credit text directly below it, and buttons at the bottom
        VBox layout = new VBox(20);  // Set the spacing between elements in the VBox
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titleBox, creditText, buttonLayout);

        // Set the background image
        setBackgroundImage(layout);

        // Scene setup
        Scene homeScene = new Scene(layout, 800, 600); // Set initial window size
        primaryStage.setTitle("Nine Men's Morris");
        primaryStage.setScene(homeScene);
        primaryStage.setResizable(true);  // Allow resizing of the window
        primaryStage.show();
    }

    // Helper method to set the background image
    private void setBackgroundImage(Pane layout) {
        File imageFile = new File("x_mens_morris/src/nmmBg.jpg"); // Image located in the root directory
        if (imageFile.exists()) {      Image image = new Image(imageFile.toURI().toString());
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

    // Action for opening the New Game screen
    private void openNewGameScreen() {
        NewGameScreen newGameScreen = new NewGameScreen(primaryStage);
        newGameScreen.showNewGameScreen();
    }

    // Dummy action for viewing replay
    private void viewReplay() {
        System.out.println("Viewing last game replay...");
    }

    // Helper method to create buttons with shadow and hover effect
    private Button createButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: #444444; -fx-text-fill: white; -fx-padding: 10px 20px;");

        // Add shadow effect to buttons
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(2.0);
        shadow.setOffsetY(2.0);
        shadow.setColor(Color.BLACK);
        shadow.setRadius(5.0);
        button.setEffect(shadow);

        // Add hover effect to change background color
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> button.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-background-color: #555555; -fx-text-fill: white; -fx-padding: 10px 20px;"));
        button.addEventHandler(MouseEvent.MOUSE_EXITED, e -> button.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-background-color: #444444; -fx-text-fill: white; -fx-padding: 10px 20px;"));

        return button;
    }

    // Create the wave animation for each letter in the title
    private void createWaveAnimation(HBox titleBox) {
        SequentialTransition sequentialTransition = new SequentialTransition();

        // Create the wave effect for each letter
        for (int i = 0; i < titleBox.getChildren().size(); i++) {
            Text letter = (Text) titleBox.getChildren().get(i);

            // Create a ScaleTransition for each letter
            ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(0.13), letter);
            scaleUp.setToX(1.5);
            scaleUp.setToY(1.5);

            ScaleTransition scaleDown = new ScaleTransition(Duration.seconds(0.13), letter);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);

            // Add the scale animations to the SequentialTransition
            sequentialTransition.getChildren().add(scaleUp);
            sequentialTransition.getChildren().add(scaleDown);
        }

        // Start the animation when the app launches
        sequentialTransition.setCycleCount(Animation.INDEFINITE);
        sequentialTransition.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
