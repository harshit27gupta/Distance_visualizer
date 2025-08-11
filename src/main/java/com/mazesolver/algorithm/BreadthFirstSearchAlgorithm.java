package com.mazesolver.algorithm;

import com.mazesolver.model.Cell;
import com.mazesolver.model.Maze;
import java.util.*;

public class BreadthFirstSearchAlgorithm extends AbstractPathfindingAlgorithm {
    
    @Override
    public String getName() {
        return "Breadth-First Search";
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
        
        // Queue for BFS traversal
        Queue<Cell> queue = new LinkedList<>();
        Set<Cell> visited = new HashSet<>();
        
        // Initialize start
        start.setDistance(0);
        queue.offer(start);
        visited.add(start);
        
        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            
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
            
            // Explore all neighbors
            for (Cell neighbor : getNeighbors(maze, current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    neighbor.setDistance(current.getDistance() + 1);
                    neighbor.setParent(current);
                    queue.offer(neighbor);
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
