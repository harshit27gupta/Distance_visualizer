package com.mazesolver.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.StrokeTransition;
import javafx.animation.ParallelTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import com.mazesolver.model.Cell;
import com.mazesolver.model.Maze;
import java.util.HashMap;
import java.util.Map;

/**
 * Displays the maze grid with interactive cells.
 * Handles user interactions like adding obstacles and visualizing the pathfinding process.
 */
public class MazeView {
    
    private GridPane gridPane;
    private Rectangle[][] cellRectangles;
    private Maze maze;
    private int currentRows;
    private int currentCols;
    private boolean isInteractive;
    private InteractionMode interactionMode;
    
    // Animation and visual enhancement properties
    private Map<String, ParallelTransition> cellAnimations;
    private boolean showExplorationWave = true;
    private int explorationWaveRadius = 2;
    
    public enum InteractionMode {
        OBSTACLE,   // Add/remove obstacles
        SET_START,  // Set start point
        SET_END     // Set end point
    }
    
    public MazeView() {
        // Initialize fields first to prevent null pointer exceptions
        cellAnimations = new HashMap<>();
        interactionMode = InteractionMode.OBSTACLE; // Default mode
        
        // Ensure cellAnimations is not null before proceeding
        if (cellAnimations == null) {
            cellAnimations = new HashMap<>();
        }
        
        initializeGrid();
    }
    
    private void initializeGrid() {
        // Ensure cellAnimations is initialized
        if (cellAnimations == null) {
            cellAnimations = new HashMap<>();
        }
        
        gridPane = new GridPane();
        gridPane.setHgap(1);
        gridPane.setVgap(1);
        gridPane.setPadding(new Insets(10));
        gridPane.getStyleClass().add("maze-grid");
        
        // Set initial size
        setMazeSize(15, 15);
    }
    
    public void setMazeSize(int rows, int cols) {
        this.currentRows = rows;
        this.currentCols = cols;
        
        // Clear existing grid and animations
        gridPane.getChildren().clear();
        getCellAnimations().clear();
        
        // Initialize cell rectangles array
        cellRectangles = new Rectangle[rows][cols];
        
        // Calculate cell size based on available space - make it more responsive
        // Use a larger base size and ensure minimum cell size for visibility
        double baseSize = 1000.0; // Increased base size for better visibility
        double minCellSize = 12.0; // Minimum cell size for visibility
        double maxCellSize = 35.0; // Maximum cell size to prevent too large cells
        
        // Calculate optimal cell size based on maze dimensions
        double cellSize = Math.max(minCellSize, 
            Math.min(maxCellSize, Math.min(baseSize / cols, baseSize / rows)));
        
        // For very large mazes, ensure we can see more of the grid
        if (rows > 20 || cols > 20) {
            cellSize = Math.max(minCellSize, baseSize / Math.max(rows, cols));
        }
        
        // Create cells
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Rectangle cell = new Rectangle(cellSize, cellSize);
                cell.setFill(Color.WHITE);
                cell.setStroke(Color.LIGHTGRAY);
                cell.setStrokeWidth(0.5);
                
                // Add subtle shadow effect
                cell.setEffect(new javafx.scene.effect.DropShadow(2, 0, 1, Color.rgb(0, 0, 0, 0.1)));
                
                // Store reference
                cellRectangles[row][col] = cell;
                
                // Add to grid
                gridPane.add(cell, col, row);
            }
        }
        
        // Update grid pane size with better spacing
        double gridWidth = cols * cellSize + cols + 20;
        double gridHeight = rows * cellSize + rows + 20;
        
        gridPane.setPrefSize(gridWidth, gridHeight);
        gridPane.setMaxSize(gridWidth, gridHeight);
        
        // Make the grid pane resizable
        gridPane.setMinSize(gridWidth, gridHeight);
    }
    
    public void setMaze(Maze maze) {
        this.maze = maze;
        if (maze != null) {
            // Update the maze size to match the actual maze dimensions
            setMazeSize(maze.getRows(), maze.getCols());
        }
        updateDisplay();
    }
    
    public void updateDisplay() {
        if (maze == null) return;
        
        Cell[][] grid = maze.getGrid();
        for (int row = 0; row < currentRows; row++) {
            for (int col = 0; col < currentCols; col++) {
                if (row < grid.length && col < grid[0].length) {
                    updateCellDisplay(row, col, grid[row][col]);
                }
            }
        }
    }
    
    private void updateCellDisplay(int row, int col, Cell cell) {
        if (cell == null || cellRectangles[row][col] == null) return;
        
        Rectangle rect = cellRectangles[row][col];
        
        // Stop any existing animations for this cell
        String cellKey = row + "," + col;
        if (getCellAnimations().containsKey(cellKey)) {
            getCellAnimations().get(cellKey).stop();
            getCellAnimations().remove(cellKey);
        }
        
        // Set color based on cell type and state with enhanced visual hierarchy
        Color targetColor;
        double targetStrokeWidth = 0.5;
        Color targetStrokeColor = Color.LIGHTGRAY;
        
        if (cell.isInPath()) {
            // Enhanced path visualization with golden color and glow effect
            targetColor = Color.rgb(255, 215, 0); // Golden yellow
            targetStrokeWidth = 2.0;
            targetStrokeColor = Color.rgb(255, 165, 0); // Orange
        } else if (cell.isVisited()) {
            // Enhanced visited cell visualization with gradient based on distance
            int distance = cell.getDistance();
            if (distance <= 5) {
                targetColor = Color.rgb(173, 216, 230); // Light blue for recent
            } else if (distance <= 15) {
                targetColor = Color.rgb(135, 206, 235); // Sky blue for medium
            } else {
                targetColor = Color.rgb(100, 149, 237); // Cornflower blue for distant
            }
            targetStrokeWidth = 1.0;
            targetStrokeColor = Color.rgb(70, 130, 180); // Steel blue
        } else {
            switch (cell.getType()) {
                case WALL:
                    targetColor = Color.rgb(47, 79, 79); // Dark slate gray
                    targetStrokeWidth = 1.0;
                    targetStrokeColor = Color.rgb(25, 25, 112); // Midnight blue
                    break;
                case PATH:
                    targetColor = Color.rgb(248, 248, 255); // Ghost white
                    targetStrokeWidth = 0.5;
                    targetStrokeColor = Color.rgb(220, 220, 220); // Gainsboro
                    break;
                case START:
                    targetColor = Color.rgb(34, 139, 34); // Forest green
                    targetStrokeWidth = 3.0;
                    targetStrokeColor = Color.rgb(0, 100, 0); // Dark green
                    break;
                case END:
                    targetColor = Color.rgb(220, 20, 60); // Crimson
                    targetStrokeWidth = 3.0;
                    targetStrokeColor = Color.rgb(139, 0, 0); // Dark red
                    break;
                case OBSTACLE:
                    targetColor = Color.rgb(160, 82, 45); // Saddle brown
                    targetStrokeWidth = 1.0;
                    targetStrokeColor = Color.rgb(101, 67, 33); // Dark brown
                    break;
                default:
                    targetColor = Color.rgb(248, 248, 255); // Ghost white
                    targetStrokeWidth = 0.5;
                    targetStrokeColor = Color.rgb(220, 220, 220); // Gainsboro
                    break;
            }
        }
        
        // Create smooth transition animations
        createCellTransition(rect, targetColor, targetStrokeColor, targetStrokeWidth, cellKey);
    }
    
    private void createCellTransition(Rectangle rect, Color targetColor, Color targetStrokeColor, 
                                    double targetStrokeWidth, String cellKey) {
        // Create fade transition for color change
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(150), rect);
        fadeTransition.setFromValue(0.8);
        fadeTransition.setToValue(1.0);
        
        // Create stroke transition for border
        StrokeTransition strokeTransition = new StrokeTransition(Duration.millis(150), rect);
        Paint currentStroke = rect.getStroke();
        if (currentStroke instanceof Color) {
            strokeTransition.setFromValue((Color) currentStroke);
        } else {
            strokeTransition.setFromValue(Color.BLACK); // Default fallback
        }
        strokeTransition.setToValue(targetStrokeColor);
        
        // Create scale transition for emphasis
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), rect);
        scaleTransition.setFromX(0.95);
        scaleTransition.setFromY(0.95);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        
        // Combine transitions
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, strokeTransition, scaleTransition);
        
        // Set final values immediately for instant visual feedback
        rect.setFill(targetColor);
        rect.setStroke(targetStrokeColor);
        rect.setStrokeWidth(targetStrokeWidth);
        
        // Store animation reference
        getCellAnimations().put(cellKey, parallelTransition);
        
        // Play animation
        parallelTransition.play();
    }
    
    public void setInteractive(boolean interactive) {
        this.isInteractive = interactive;
        
        if (interactive) {
            setupCellInteractions();
        } else {
            removeCellInteractions();
        }
    }
    
    private void setupCellInteractions() {
        for (int row = 0; row < currentRows; row++) {
            for (int col = 0; col < currentCols; col++) {
                final int finalRow = row;
                final int finalCol = col;
                
                Rectangle cell = cellRectangles[row][col];
                cell.setOnMouseClicked(e -> {
                    if (maze != null) {
                        handleCellClick(finalRow, finalCol);
                    }
                });
                
                // Enhanced hover effect with smooth transitions
                cell.setOnMouseEntered(e -> {
                    if (maze != null && maze.getCell(finalRow, finalCol).getType() == Cell.CellType.PATH) {
                        // Create hover animation
                        ScaleTransition hoverScale = new ScaleTransition(Duration.millis(100), cell);
                        hoverScale.setToX(1.05);
                        hoverScale.setToY(1.05);
                        hoverScale.play();
                        
                        // Add glow effect
                        cell.setEffect(new javafx.scene.effect.DropShadow(5, 0, 2, Color.rgb(0, 123, 255, 0.3)));
                    }
                });
                
                cell.setOnMouseExited(e -> {
                    if (maze != null) {
                        // Reset hover effects
                        ScaleTransition resetScale = new ScaleTransition(Duration.millis(100), cell);
                        resetScale.setToX(1.0);
                        resetScale.setToY(1.0);
                        resetScale.play();
                        
                        // Remove glow effect
                        cell.setEffect(new javafx.scene.effect.DropShadow(2, 0, 1, Color.rgb(0, 0, 0, 0.1)));
                        
                        // Update display
                        updateCellDisplay(finalRow, finalCol, maze.getCell(finalRow, finalCol));
                    }
                });
            }
        }
    }
    
    private void removeCellInteractions() {
        for (int row = 0; row < currentRows; row++) {
            for (int col = 0; col < currentCols; col++) {
                Rectangle cell = cellRectangles[row][col];
                cell.setOnMouseClicked(null);
                cell.setOnMouseEntered(null);
                cell.setOnMouseExited(null);
                
                // Reset effects
                cell.setEffect(new javafx.scene.effect.DropShadow(2, 0, 1, Color.rgb(0, 0, 0, 0.1)));
            }
        }
    }
    
    private void handleCellClick(int row, int col) {
        if (maze == null) return;
        
        Cell cell = maze.getCell(row, col);
        if (cell == null) return;
        
        // Handle different interaction modes
        switch (interactionMode) {
            case OBSTACLE:
                if (cell.getType() == Cell.CellType.PATH) {
                    maze.addObstacle(row, col);
                } else if (cell.getType() == Cell.CellType.OBSTACLE) {
                    maze.removeObstacle(row, col);
                }
                break;
                
            case SET_START:
                if (cell.getType() == Cell.CellType.PATH || cell.getType() == Cell.CellType.OBSTACLE) {
                    maze.setStartCell(row, col);
                }
                break;
                
            case SET_END:
                if (cell.getType() == Cell.CellType.PATH || cell.getType() == Cell.CellType.OBSTACLE) {
                    maze.setEndCell(row, col);
                }
                break;
        }
        
        // Update display
        updateDisplay();
    }
    
    public void clearPath() {
        if (maze != null) {
            maze.clearPath();
            updateDisplay();
        }
    }
    
    public void resetMaze() {
        if (maze != null) {
            maze.resetMaze();
            updateDisplay();
        }
    }
    
    public void setCellVisited(int row, int col, boolean visited) {
        if (row >= 0 && row < currentRows && col >= 0 && col < currentCols) {
            if (maze != null) {
                Cell cell = maze.getCell(row, col);
                if (cell != null) {
                    cell.setVisited(visited);
                    updateCellDisplay(row, col, cell);
                    
                    // Add exploration wave effect if enabled
                    if (showExplorationWave && visited) {
                        showExplorationWave(row, col);
                    }
                }
            }
        }
    }
    
    public void setCellInPath(int row, int col, boolean inPath) {
        if (row >= 0 && row < currentRows && col >= 0 && col < currentCols) {
            if (maze != null) {
                Cell cell = maze.getCell(row, col);
                if (cell != null) {
                    cell.setInPath(inPath);
                    updateCellDisplay(row, col, cell);
                    
                    // Add path highlight effect
                    if (inPath) {
                        highlightPathCell(row, col);
                    }
                }
            }
        }
    }
    
    private void showExplorationWave(int centerRow, int centerCol) {
        // Show a subtle wave effect around newly visited cells
        for (int row = Math.max(0, centerRow - explorationWaveRadius); 
             row <= Math.min(currentRows - 1, centerRow + explorationWaveRadius); row++) {
            for (int col = Math.max(0, centerCol - explorationWaveRadius); 
                 col <= Math.min(currentCols - 1, centerCol + explorationWaveRadius); col++) {
                
                if (row == centerRow && col == centerCol) continue;
                
                Rectangle cell = cellRectangles[row][col];
                if (cell != null && maze.getCell(row, col).getType() == Cell.CellType.PATH) {
                    // Create wave effect
                    ScaleTransition waveScale = new ScaleTransition(Duration.millis(300), cell);
                    waveScale.setToX(1.02);
                    waveScale.setToY(1.02);
                    waveScale.setCycleCount(2);
                    waveScale.setAutoReverse(true);
                    waveScale.play();
                }
            }
        }
    }
    
    private void highlightPathCell(int row, int col) {
        Rectangle cell = cellRectangles[row][col];
        if (cell != null) {
            // Create path highlight effect
            ScaleTransition pathScale = new ScaleTransition(Duration.millis(200), cell);
            pathScale.setToX(1.1);
            pathScale.setToY(1.1);
            pathScale.setCycleCount(2);
            pathScale.setAutoReverse(true);
            
            // Add glow effect for path cells
            cell.setEffect(new javafx.scene.effect.DropShadow(8, 0, 3, Color.rgb(255, 215, 0, 0.5)));
            
            pathScale.play();
            
            // Remove glow effect after animation
            pathScale.setOnFinished(e -> {
                cell.setEffect(new javafx.scene.effect.DropShadow(2, 0, 1, Color.rgb(0, 0, 0, 0.1)));
            });
        }
    }
    
    public GridPane getRoot() {
        return gridPane;
    }
    
    public int getCurrentRows() {
        return currentRows;
    }
    
    public int getCurrentCols() {
        return currentCols;
    }
    
    public Maze getMaze() {
        return maze;
    }
    
    public InteractionMode getInteractionMode() {
        return interactionMode;
    }
    
    public void setInteractionMode(InteractionMode mode) {
        this.interactionMode = mode;
    }
    
    // Getters for visual enhancement options
    public boolean isShowExplorationWave() {
        return showExplorationWave;
    }
    
    public void setShowExplorationWave(boolean showExplorationWave) {
        this.showExplorationWave = showExplorationWave;
    }
    
    public int getExplorationWaveRadius() {
        return explorationWaveRadius;
    }
    
    public void setExplorationWaveRadius(int explorationWaveRadius) {
        this.explorationWaveRadius = explorationWaveRadius;
    }
    
    /**
     * Ensures cellAnimations is initialized and returns it.
     * This prevents null pointer exceptions.
     */
    private Map<String, ParallelTransition> getCellAnimations() {
        if (cellAnimations == null) {
            cellAnimations = new HashMap<>();
        }
        return cellAnimations;
    }
}
