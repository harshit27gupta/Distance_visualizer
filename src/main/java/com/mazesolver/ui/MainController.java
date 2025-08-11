package com.mazesolver.ui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import com.mazesolver.algorithm.PathfindingAlgorithm;
import com.mazesolver.model.Cell;
import com.mazesolver.model.Maze;
import com.mazesolver.util.AlgorithmManager;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.scene.control.TextArea;

public class MainController implements PathfindingAlgorithm.AnimationCallback {
    private MainView view;
    private Maze maze;
    private PathfindingAlgorithm currentAlgorithm;
    private ExecutorService executor;
    private boolean isPathfinding;
    private List<Cell> currentPath;

    public MainController(MainView view) {
        this.view = view;
        this.executor = Executors.newSingleThreadExecutor();
        this.isPathfinding = false;
    }

    public void initialize() {
        setupEventHandlers();
        
        // Set initial algorithm info text with more prominent display
        String welcomeMessage = 
            "Welcome to Maze Solver!\n\n" +
            "Select a pathfinding algorithm from the dropdown above to see:\n" +
            "• Time Complexity\n" +
            "• Space Complexity\n" +
            "• Algorithm Description\n\n" +
            "Generate a maze and find the optimal path!";
        
        // Update both panels with the welcome message
        view.getAlgorithmInfoTextArea().setText(welcomeMessage);
        view.getAlgorithmInfoTextArea().setWrapText(true);
        
        // Also show in the right panel
        TextArea rightPanelInfo = view.getRightPanelAlgorithmInfo();
        if (rightPanelInfo != null) {
            rightPanelInfo.setText(welcomeMessage);
            rightPanelInfo.setWrapText(true);
        }
        
        // Update algorithm info immediately to show the selected algorithm's complexity
        updateAlgorithmInfo();
        updateMazeInfo();
        
        // Force a layout update to ensure visibility
        Platform.runLater(() -> {
            if (view.getRoot() != null) {
                view.getRoot().requestLayout();
            }
        });
    }

    private void setupEventHandlers() {
        // Generate maze button
        view.getGenerateMazeButton().setOnAction(e -> generateMaze());
        
        // Find path button
        view.getFindPathButton().setOnAction(e -> findPath());
        
        // Clear path button
        view.getClearPathButton().setOnAction(e -> clearPath());
        
        // Reset maze button
        view.getResetMazeButton().setOnAction(e -> resetMaze());
        
        // Clear obstacles button
        view.getClearObstaclesButton().setOnAction(e -> clearObstacles());
        
        // Maze size combo box
        view.getMazeSizeComboBox().setOnAction(e -> updateMazeSize());
        
        // Generation algorithm combo box
        view.getGenerationAlgorithmComboBox().setOnAction(e -> updateMazeInfo());
        
        // Pathfinding algorithm combo box
        view.getPathfindingAlgorithmComboBox().setOnAction(e -> updateAlgorithmInfo());
        
        // Animation speed slider
        view.getAnimationSpeedSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            // Speed slider value changed - this will be used in animation callbacks
        });
    }

    private void generateMaze() {
        try {
            String sizeText = view.getMazeSizeComboBox().getValue();
            String[] dimensions = sizeText.split("x");
            int rows = Integer.parseInt(dimensions[0]);
            int cols = Integer.parseInt(dimensions[1]);
            
            String generationAlgorithm = view.getGenerationAlgorithmComboBox().getValue();
            Maze.MazeGenerationAlgorithm algorithm = getGenerationAlgorithm(generationAlgorithm);
            
            maze = new Maze(rows, cols);
            maze.generateMaze(algorithm);
            
            view.getMazeView().setMaze(maze);
            view.getMazeView().setInteractive(true);
            
            updateMazeInfo();
            setControlsEnabled(true);
            
        } catch (Exception e) {
            showAlert("Error generating maze: " + e.getMessage());
        }
    }

    private Maze.MazeGenerationAlgorithm getGenerationAlgorithm(String name) {
        switch (name) {
            case "Recursive Backtracking": return Maze.MazeGenerationAlgorithm.RECURSIVE_BACKTRACKING;
            case "Prim's Algorithm": return Maze.MazeGenerationAlgorithm.PRIMS_ALGORITHM;
            case "Kruskal's Algorithm": return Maze.MazeGenerationAlgorithm.KRUSKALS_ALGORITHM;
            default: return Maze.MazeGenerationAlgorithm.RECURSIVE_BACKTRACKING;
        }
    }

    private void findPath() {
        if (maze == null || isPathfinding) {
            return;
        }

        String algorithmName = view.getPathfindingAlgorithmComboBox().getValue();
        currentAlgorithm = AlgorithmManager.getAlgorithm(algorithmName);
        
        if (currentAlgorithm == null) {
            showAlert("Please select a valid algorithm");
            return;
        }

        isPathfinding = true;
        setControlsEnabled(false);
        
        // Show progress indicator
        view.getExplorationProgressIndicator().setVisible(true);
        view.getProgressLabel().setText("Exploring maze...");
        
        // Clear previous path
        view.getMazeView().clearPath();
        
        Task<List<Cell>> pathfindingTask = new Task<>() {
            @Override
            protected List<Cell> call() throws Exception {
                return currentAlgorithm.findPathWithAnimation(maze, maze.getStartCell(), maze.getEndCell(), MainController.this);
            }
        };

        pathfindingTask.setOnSucceeded(e -> {
            List<Cell> path = pathfindingTask.getValue();
            handlePathfindingResult(path);
            isPathfinding = false;
            setControlsEnabled(true);
            
            // Hide progress indicator
            view.getExplorationProgressIndicator().setVisible(false);
        });

        pathfindingTask.setOnFailed(e -> {
            showAlert("Pathfinding failed: " + pathfindingTask.getException().getMessage());
            isPathfinding = false;
            setControlsEnabled(true);
            
            // Hide progress indicator
            view.getExplorationProgressIndicator().setVisible(false);
        });

        executor.submit(pathfindingTask);
    }

    // Animation callback implementations
    @Override
    public void onCellVisited(Cell cell) {
        Platform.runLater(() -> {
            view.getMazeView().setCellVisited(cell.getRow(), cell.getCol(), true);
            
            // Update progress based on visited cells vs total cells
            if (maze != null) {
                int totalCells = maze.getRows() * maze.getCols();
                int visitedCells = countVisitedCells();
                double progress = (double) visitedCells / totalCells;
                view.getExplorationProgressIndicator().setProgress(progress);
                view.getProgressLabel().setText(String.format("Exploring... %d/%d cells", visitedCells, totalCells));
            }
        });
    }
    
    private int countVisitedCells() {
        if (maze == null) return 0;
        
        int count = 0;
        Cell[][] grid = maze.getGrid();
        for (int row = 0; row < maze.getRows(); row++) {
            for (int col = 0; col < maze.getCols(); col++) {
                if (grid[row][col].isVisited()) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void onPathUpdated(List<Cell> currentPath) {
        this.currentPath = currentPath;
        Platform.runLater(() -> {
            // Clear previous path display
            view.getMazeView().clearPath();
            // Show current path
            for (Cell pathCell : currentPath) {
                view.getMazeView().setCellInPath(pathCell.getRow(), pathCell.getCol(), true);
            }
        });
    }

    @Override
    public void onAlgorithmComplete(List<Cell> finalPath) {
        this.currentPath = finalPath;
        Platform.runLater(() -> {
            // Show final path with enhanced visual feedback
            for (Cell pathCell : finalPath) {
                view.getMazeView().setCellInPath(pathCell.getRow(), pathCell.getCol(), true);
            }
            
            // Update progress to show completion
            view.getExplorationProgressIndicator().setProgress(1.0);
            
            // Check if path is valid (should have at least 2 cells: start and end)
            if (finalPath.isEmpty() || finalPath.size() < 2) {
                view.getProgressLabel().setText("No path found!");
                view.getProgressLabel().setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                view.getProgressLabel().getStyleClass().add("progress-label");
                view.getProgressLabel().getStyleClass().add("error");
                
                // Clear any previous path display
                view.getMazeView().clearPath();
            } else {
                // More concise path length message
                String pathMessage = String.format("Path found! (%d cells)", finalPath.size());
                view.getProgressLabel().setText(pathMessage);
                view.getProgressLabel().setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                view.getProgressLabel().getStyleClass().add("progress-label");
                view.getProgressLabel().getStyleClass().add("success");
                
                // Also update the performance info label with path length
                if (currentAlgorithm != null) {
                    view.getPerformanceInfoLabel().setText(
                        "Path Length: " + finalPath.size() + " cells\n" +
                        "Visited Cells: " + currentAlgorithm.getVisitedCellsCount() + "\n" +
                        "Execution Time: " + currentAlgorithm.getExecutionTime() + "ms"
                    );
                }
            }
        });
    }

    @Override
    public long onStepDelay(long milliseconds) {
        // Use the animation speed slider to control delay
        double speedMultiplier = view.getAnimationSpeedSlider().getValue() / 100.0;
        return (long) (milliseconds / speedMultiplier);
    }

    private void handlePathfindingResult(List<Cell> path) {
        if (path.isEmpty() || path.size() < 2) {
            showAlert("No path found!");
        } else {
            updatePerformanceInfo();
            showAlert("Path found! Length: " + path.size() + " cells");
        }
    }

    private void clearPath() {
        if (maze != null) {
            view.getMazeView().clearPath();
            currentPath = null;
        }
    }

    private void resetMaze() {
        if (maze != null) {
            maze.resetMaze();
            view.getMazeView().updateDisplay();
            currentPath = null;
        }
    }

    private void clearObstacles() {
        if (maze != null) {
            maze.clearPath();
            view.getMazeView().updateDisplay();
            currentPath = null;
        }
    }
    
    private void updateMazeSize() {
        // This will be handled when generating a new maze
    }

    private void updateAlgorithmInfo() {
        String algorithmName = view.getPathfindingAlgorithmComboBox().getValue();
        if (algorithmName == null) {
            algorithmName = "Dijkstra's Algorithm"; // Default fallback
        }
        
        PathfindingAlgorithm algorithm = AlgorithmManager.getAlgorithm(algorithmName);
        
        if (algorithm != null) {
            String algorithmInfo = 
                "Algorithm: " + algorithm.getName() + "\n\n" +
                "Time Complexity: " + algorithm.getTimeComplexity() + "\n" +
                "Space Complexity: " + algorithm.getSpaceComplexity() + "\n\n" +
                "Description: " + getAlgorithmDescription(algorithmName);
            
            // Update both the bottom info panel and the right panel
            view.getAlgorithmInfoTextArea().setText(algorithmInfo);
            view.getAlgorithmInfoTextArea().setWrapText(true);
            
            // Also update the right panel algorithm info for better visibility
            TextArea rightPanelInfo = view.getRightPanelAlgorithmInfo();
            if (rightPanelInfo != null) {
                rightPanelInfo.setText(algorithmInfo);
                rightPanelInfo.setWrapText(true);
            }
            
            // Force layout update to ensure visibility
            Platform.runLater(() -> {
                if (view.getRoot() != null) {
                    view.getRoot().requestLayout();
                }
                // Also force the text areas to update their display
                view.getAlgorithmInfoTextArea().requestLayout();
                if (rightPanelInfo != null) {
                    rightPanelInfo.requestLayout();
                }
            });
        } else {
            // Fallback message if algorithm not found
            String fallbackInfo = 
                "Algorithm: " + algorithmName + "\n\n" +
                "Time Complexity: O(V + E)\n" +
                "Space Complexity: O(V)\n\n" +
                "Description: Standard pathfinding algorithm for maze navigation.";
            
            view.getAlgorithmInfoTextArea().setText(fallbackInfo);
            view.getAlgorithmInfoTextArea().setWrapText(true);
            
            TextArea rightPanelInfo = view.getRightPanelAlgorithmInfo();
            if (rightPanelInfo != null) {
                rightPanelInfo.setText(fallbackInfo);
                rightPanelInfo.setWrapText(true);
            }
            
            // Force layout update to ensure visibility
            Platform.runLater(() -> {
                if (view.getRoot() != null) {
                    view.getRoot().requestLayout();
                }
                // Also force the text areas to update their display
                view.getAlgorithmInfoTextArea().requestLayout();
                if (rightPanelInfo != null) {
                    rightPanelInfo.requestLayout();
                }
            });
        }
    }

    private String getAlgorithmDescription(String algorithmName) {
        switch (algorithmName) {
            case "Dijkstra's Algorithm":
                return "Finds shortest path using distance-based exploration. Guarantees optimal solution.";
            case "A* Algorithm":
                return "Heuristic-based search combining Dijkstra's approach with goal-directed exploration.";
            case "Breadth-First Search":
                return "Level-by-level exploration ensuring shortest path in unweighted graphs.";
            case "Depth-First Search":
                return "Deep exploration strategy, may not find shortest path but uses less memory.";
            default:
                return "Pathfinding algorithm for maze navigation.";
        }
    }

    private void updatePerformanceInfo() {
        if (currentAlgorithm != null) {
            view.getPerformanceInfoLabel().setText(
                "Visited Cells: " + currentAlgorithm.getVisitedCellsCount() + "\n" +
                "Execution Time: " + currentAlgorithm.getExecutionTime() + "ms"
            );
        }
    }

    private void updateMazeInfo() {
        if (maze != null) {
            view.getMazeInfoLabel().setText(
                "Maze Size: " + maze.getRows() + "x" + maze.getCols() + "\n" +
                "Generation: " + view.getGenerationAlgorithmComboBox().getValue()
            );
        }
    }

    private void setControlsEnabled(boolean enabled) {
        view.getGenerateMazeButton().setDisable(!enabled);
        view.getFindPathButton().setDisable(!enabled);
        view.getClearPathButton().setDisable(!enabled);
        view.getResetMazeButton().setDisable(!enabled);
        view.getClearObstaclesButton().setDisable(!enabled);
        view.getMazeSizeComboBox().setDisable(!enabled);
        view.getGenerationAlgorithmComboBox().setDisable(!enabled);
        view.getPathfindingAlgorithmComboBox().setDisable(!enabled);
        view.getAnimationSpeedSlider().setDisable(!enabled);
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Maze Solver");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
