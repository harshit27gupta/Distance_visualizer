package com.mazesolver.algorithm;

import com.mazesolver.model.Cell;
import com.mazesolver.model.Maze;
import java.util.*;

public class AStarAlgorithm extends AbstractPathfindingAlgorithm {
    
    @Override
    public String getName() {
        return "A* Algorithm";
    }
    
    @Override
    public String getTimeComplexity() {
        return "O((V + E) log V)";
    }
    
    @Override
    public String getSpaceComplexity() {
        return "O(V)";
    }

    @Override
    protected List<Cell> findPathImplementation(Maze maze, Cell start, Cell end) {
        return findPathImplementationWithAnimation(maze, start, end, null);
    }

    @Override
    protected List<Cell> findPathImplementationWithAnimation(Maze maze, Cell start, Cell end, AnimationCallback callback) {
        reset();
        
        // Priority queue for open set (cells to explore)
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>((a, b) -> 
            Double.compare(a.fScore, b.fScore));
        
        // Maps to track g-scores and f-scores
        Map<Cell, Double> gScore = new HashMap<>();
        Map<Cell, Double> fScore = new HashMap<>();
        
        // Initialize start node
        gScore.put(start, 0.0);
        fScore.put(start, (double) manhattanDistance(start, end));
        start.setDistance(0);
        openSet.offer(new AStarNode(start, fScore.get(start)));
        
        Set<Cell> closedSet = new HashSet<>();
        
        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            Cell currentCell = current.cell;
            
            if (currentCell == end) {
                break;
            }
            
            closedSet.add(currentCell);
            markVisited(currentCell);
            
            // Animation callback for visited cell
            if (callback != null) {
                callback.onCellVisited(currentCell);
                try {
                    Thread.sleep(callback.onStepDelay(50));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            // Explore neighbors
            for (Cell neighbor : getNeighbors(maze, currentCell)) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                
                double tentativeGScore = gScore.get(currentCell) + 1;
                
                if (!gScore.containsKey(neighbor) || tentativeGScore < gScore.get(neighbor)) {
                    // This path is better, record it
                    neighbor.setParent(currentCell);
                    neighbor.setDistance((int) tentativeGScore);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + (double) manhattanDistance(neighbor, end));
                    
                    // Add to open set if not already there
                    if (!openSet.stream().anyMatch(node -> node.cell == neighbor)) {
                        openSet.offer(new AStarNode(neighbor, fScore.get(neighbor)));
                    }
                }
            }
        }
        
        // Reconstruct path
        List<Cell> path = reconstructPath(end);
        
        // Animation callback for final path
        if (callback != null) {
            callback.onAlgorithmComplete(path);
        }
        
        return path;
    }
    
    private static class AStarNode {
        Cell cell;
        double fScore;
        
        AStarNode(Cell cell, double fScore) {
            this.cell = cell;
            this.fScore = fScore;
        }
    }
    

}

