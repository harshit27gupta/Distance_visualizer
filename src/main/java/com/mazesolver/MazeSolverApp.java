package com.mazesolver;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import com.mazesolver.ui.MainController;
import com.mazesolver.ui.MainView;

/**
 * Main application class for the Advanced Maze Solver.
 * Sets up the JavaFX application and initializes the main UI.
 */
public class MazeSolverApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Create the main view and controller
            MainView mainView = new MainView();
            MainController controller = new MainController(mainView);
            
            // Set up the main scene
            Scene scene = new Scene(mainView.getRoot(), 1400, 900); // Increased from 1200x800 to 1400x900
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            
            // Configure the primary stage
            primaryStage.setTitle("Advanced Maze Solver - Algorithm Visualizer");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200); // Increased from 1000 to 1200
            primaryStage.setMinHeight(800); // Increased from 700 to 800
            primaryStage.show();
            
            // Initialize the controller
            controller.initialize();
            
        } catch (Exception e) {
            // Show error dialog if something goes wrong
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to start application");
            alert.setContentText("An error occurred while starting the application: " + e.getMessage());
            alert.showAndWait();
            
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
