package com.mazesolver.algorithm;

import com.mazesolver.model.Cell;
import com.mazesolver.model.Maze;
import java.util.List;

/**
 * Base interface for all pathfinding algorithms.
 * Provides methods for finding paths and getting performance metrics.
 */
public interface PathfindingAlgorithm {
    
    /**
     * Finds the shortest path from start to end in the given maze.
     * @param maze The maze to search in
     * @param start The starting cell
     * @param end The destination cell
     * @return List of cells representing the path, or empty list if no path found
     */
    List<Cell> findPath(Maze maze, Cell start, Cell end);
    
    // Animation callback interface
    interface AnimationCallback {
        void onCellVisited(Cell cell);
        void onPathUpdated(List<Cell> currentPath);
        void onAlgorithmComplete(List<Cell> finalPath);
        long onStepDelay(long milliseconds);
    }
    
    // Method to find path with animation
    List<Cell> findPathWithAnimation(Maze maze, Cell start, Cell end, AnimationCallback callback);
    
    /**
     * Gets the name of the algorithm.
     * @return Algorithm name
     */
    String getName();
    
    /**
     * Gets the time complexity of the algorithm.
     * @return Time complexity as a string (e.g., "O(V + E)")
     */
    String getTimeComplexity();
    
    /**
     * Gets the space complexity of the algorithm.
     * @return Space complexity as a string (e.g., "O(V)")
     */
    String getSpaceComplexity();
    
    /**
     * Gets the number of cells visited during the last pathfinding operation.
     * @return Number of visited cells
     */
    int getVisitedCellsCount();
    
    /**
     * Gets the execution time of the last pathfinding operation in milliseconds.
     * @return Execution time in milliseconds
     */
    long getExecutionTime();
    
    /**
     * Resets the algorithm's internal state and metrics.
     */
    void reset();
}
