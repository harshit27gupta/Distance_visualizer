package com.mazesolver.algorithm;

import com.mazesolver.model.Cell;
import com.mazesolver.model.Maze;
import java.util.*;

public class DijkstraAlgorithm extends AbstractPathfindingAlgorithm {
    
    @Override
    public String getName() {
        return "Dijkstra's Algorithm";
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
        
        // Initialize distances and visited set
        Map<Cell, Integer> distances = new HashMap<>();
        Set<Cell> visited = new HashSet<>();
        PriorityQueue<Cell> pq = new PriorityQueue<>((a, b) -> 
            Integer.compare(distances.getOrDefault(a, Integer.MAX_VALUE), 
                          distances.getOrDefault(b, Integer.MAX_VALUE)));
        
        // Set start distance to 0
        distances.put(start, 0);
        start.setDistance(0);
        pq.offer(start);
        
        while (!pq.isEmpty()) {
            Cell current = pq.poll();
            
            if (current == end) {
                break;
            }
            
            if (visited.contains(current)) {
                continue;
            }
            
            visited.add(current);
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
            
            // Explore neighbors
            for (Cell neighbor : getNeighbors(maze, current)) {
                if (!visited.contains(neighbor)) {
                    int newDistance = distances.get(current) + 1;
                    
                    if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                        distances.put(neighbor, newDistance);
                        neighbor.setDistance(newDistance);
                        neighbor.setParent(current);
                        pq.offer(neighbor);
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
}

