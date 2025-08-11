package com.mazesolver.algorithm;

import com.mazesolver.model.Cell;
import com.mazesolver.model.Maze;
import java.util.*;

/**
 * Abstract base class for pathfinding algorithms.
 * Provides common functionality like performance tracking and utility methods.
 */
public abstract class AbstractPathfindingAlgorithm implements PathfindingAlgorithm {
    
    protected int visitedCellsCount;
    protected long executionTime;
    protected long startTime;
    
    public AbstractPathfindingAlgorithm() {
        reset();
    }
    
    @Override
    public List<Cell> findPath(Maze maze, Cell start, Cell end) {
        startTime = System.currentTimeMillis();
        List<Cell> path = findPathImplementation(maze, start, end);
        executionTime = System.currentTimeMillis() - startTime;
        return path;
    }

    @Override
    public List<Cell> findPathWithAnimation(Maze maze, Cell start, Cell end, AnimationCallback callback) {
        startTime = System.currentTimeMillis();
        List<Cell> path = findPathImplementationWithAnimation(maze, start, end, callback);
        executionTime = System.currentTimeMillis() - startTime;
        return path;
    }

    /**
     * Implementation of the pathfinding algorithm.
     * Subclasses must implement this method.
     */
    protected abstract List<Cell> findPathImplementation(Maze maze, Cell start, Cell end);
    
    // Default implementation that can be overridden for better animation
    protected List<Cell> findPathImplementationWithAnimation(Maze maze, Cell start, Cell end, AnimationCallback callback) {
        // Default implementation just calls the regular method
        return findPathImplementation(maze, start, end);
    }
    
    @Override
    public int getVisitedCellsCount() {
        return visitedCellsCount;
    }
    
    @Override
    public long getExecutionTime() {
        return executionTime;
    }
    
    @Override
    public void reset() {
        visitedCellsCount = 0;
        executionTime = 0;
        startTime = 0;
    }
    
    /**
     * Marks a cell as visited and increments the counter.
     */
    protected void markVisited(Cell cell) {
        if (cell != null && !cell.isVisited()) {
            cell.setVisited(true);
            visitedCellsCount++;
        }
    }
    
    /**
     * Gets all valid neighbors of a cell.
     */
    protected List<Cell> getNeighbors(Maze maze, Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int row = cell.getRow();
        int col = cell.getCol();
        
        // Check all 4 directions: up, right, down, left
        int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            if (isValidPosition(maze, newRow, newCol)) {
                Cell neighbor = maze.getCell(newRow, newCol);
                if (neighbor != null && neighbor.isWalkable()) {
                    neighbors.add(neighbor);
                }
            }
        }
        
        return neighbors;
    }
    
    /**
     * Checks if a position is valid within the maze bounds.
     */
    protected boolean isValidPosition(Maze maze, int row, int col) {
        return row >= 0 && row < maze.getRows() && col >= 0 && col < maze.getCols();
    }
    
    /**
     * Reconstructs the path from end to start using parent pointers.
     */
    protected List<Cell> reconstructPath(Cell end) {
        List<Cell> path = new ArrayList<>();
        Cell current = end;
        
        while (current != null) {
            path.add(0, current); // Add to front to maintain order
            current = current.getParent();
        }
        
        return path;
    }
    
    /**
     * Calculates Manhattan distance between two cells.
     */
    protected int manhattanDistance(Cell a, Cell b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }
    
    /**
     * Calculates Euclidean distance between two cells.
     */
    protected double euclideanDistance(Cell a, Cell b) {
        int dr = a.getRow() - b.getRow();
        int dc = a.getCol() - b.getCol();
        return Math.sqrt(dr * dr + dc * dc);
    }
}
