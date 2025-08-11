package com.mazesolver.algorithm;

import com.mazesolver.model.Cell;
import com.mazesolver.model.Maze;
import java.util.*;

public class DepthFirstSearchAlgorithm extends AbstractPathfindingAlgorithm {
    
    @Override
    public String getName() {
        return "Depth-First Search";
    }
    
    @Override
    public String getTimeComplexity() {
        return "O(V + E)";
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
        
        // Stack for DFS traversal
        Stack<Cell> stack = new Stack<>();
        Set<Cell> visited = new HashSet<>();
        
        // Initialize start
        start.setDistance(0);
        stack.push(start);
        visited.add(start);
        
        while (!stack.isEmpty()) {
            Cell current = stack.pop();
            
            if (current == end) {
                break;
            }
            
            markVisited(current);
            
            // Animation callback for visited cell
            if (callback != null) {
                callback.onCellVisited(current);
                try {
                    Thread.sleep(callback.onStepDelay(50));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            // Explore all neighbors (in reverse order for better visualization)
            List<Cell> neighbors = getNeighbors(maze, current);
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                Cell neighbor = neighbors.get(i);
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    neighbor.setDistance(current.getDistance() + 1);
                    neighbor.setParent(current);
                    stack.push(neighbor);
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
    
}
