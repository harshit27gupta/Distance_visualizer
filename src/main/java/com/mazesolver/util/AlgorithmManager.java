package com.mazesolver.util;

import com.mazesolver.algorithm.*;
import java.util.*;

/**
 * Manages all available pathfinding algorithms.
 * Provides easy access to algorithms and their metadata.
 */
public class AlgorithmManager {
    
    private static final Map<String, PathfindingAlgorithm> algorithms = new LinkedHashMap<>();
    
    static {
        // Initialize all available algorithms
        algorithms.put("Dijkstra's Algorithm", new DijkstraAlgorithm());
        algorithms.put("A* Algorithm", new AStarAlgorithm());
        algorithms.put("Breadth-First Search", new BreadthFirstSearchAlgorithm());
        algorithms.put("Depth-First Search", new DepthFirstSearchAlgorithm());
    }
    
    /**
     * Gets all available algorithm names.
     * @return List of algorithm names
     */
    public static List<String> getAlgorithmNames() {
        return new ArrayList<>(algorithms.keySet());
    }
    
    /**
     * Gets an algorithm by name.
     * @param name The name of the algorithm
     * @return The algorithm instance, or null if not found
     */
    public static PathfindingAlgorithm getAlgorithm(String name) {
        return algorithms.get(name);
    }
    
    /**
     * Gets all available algorithms.
     * @return Map of algorithm names to algorithm instances
     */
    public static Map<String, PathfindingAlgorithm> getAllAlgorithms() {
        return new LinkedHashMap<>(algorithms);
    }
    
    /**
     * Gets the default algorithm (Dijkstra's).
     * @return Default algorithm instance
     */
    public static PathfindingAlgorithm getDefaultAlgorithm() {
        return algorithms.get("Dijkstra's Algorithm");
    }
    
    /**
     * Resets all algorithms.
     */
    public static void resetAllAlgorithms() {
        algorithms.values().forEach(PathfindingAlgorithm::reset);
    }
}
