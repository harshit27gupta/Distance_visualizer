package com.mazesolver.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a maze as a 2D grid of cells.
 * Provides methods for maze generation, pathfinding, and cell manipulation.
 */
public class Maze {
    private Cell[][] grid;
    private int rows;
    private int cols;
    private Cell startCell;
    private Cell endCell;
    private Random random;
    
    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols];
        this.random = new Random();
        initializeGrid();
    }
    
    private void initializeGrid() {
        // Initialize all cells
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid[row][col] = new Cell(row, col);
            }
        }
        
        // Set default start and end cells
        startCell = grid[0][0];
        endCell = grid[rows - 1][cols - 1];
        startCell.setType(Cell.CellType.START);
        endCell.setType(Cell.CellType.END);
    }
    
    public void generateMaze(MazeGenerationAlgorithm algorithm) {
        resetMaze();
        
        switch (algorithm) {
            case RECURSIVE_BACKTRACKING:
                generateRecursiveBacktracking();
                break;
            case PRIMS_ALGORITHM:
                generatePrimsAlgorithm();
                break;
            case KRUSKALS_ALGORITHM:
                generateKruskalsAlgorithm();
                break;
        }
        
        // Ensure start and end are accessible
        ensureStartEndAccessible();
    }
    
    private void generateRecursiveBacktracking() {
        // Start from a random cell
        int startRow = random.nextInt(rows);
        int startCol = random.nextInt(cols);
        recursiveBacktrack(startRow, startCol);
    }
    
    private void recursiveBacktrack(int row, int col) {
        grid[row][col].setType(Cell.CellType.PATH);
        
        // Define directions: up, right, down, left
        int[][] directions = {{-2, 0}, {0, 2}, {2, 0}, {0, -2}};
        shuffleArray(directions);
        
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            if (isValid(newRow, newCol) && grid[newRow][newCol].getType() == Cell.CellType.WALL) {
                // Carve path
                grid[row + dir[0]/2][col + dir[1]/2].setType(Cell.CellType.PATH);
                recursiveBacktrack(newRow, newCol);
            }
        }
    }
    
    private void generatePrimsAlgorithm() {
        // Initialize all cells as walls
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid[row][col].setType(Cell.CellType.WALL);
            }
        }
        
        List<int[]> walls = new ArrayList<>();
        int startRow = random.nextInt(rows);
        int startCol = random.nextInt(cols);
        
        grid[startRow][startCol].setType(Cell.CellType.PATH);
        addWalls(startRow, startCol, walls);
        
        while (!walls.isEmpty()) {
            int wallIndex = random.nextInt(walls.size());
            int[] wall = walls.remove(wallIndex);
            int wallRow = wall[0];
            int wallCol = wall[1];
            
            if (countAdjacentPaths(wallRow, wallCol) == 1) {
                grid[wallRow][wallCol].setType(Cell.CellType.PATH);
                addWalls(wallRow, wallCol, walls);
            }
        }
    }
    
    private void generateKruskalsAlgorithm() {
        // Initialize all cells as walls
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid[row][col].setType(Cell.CellType.WALL);
            }
        }
        
        // Create edges between cells
        List<Edge> edges = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (row < rows - 1) {
                    edges.add(new Edge(row, col, row + 1, col));
                }
                if (col < cols - 1) {
                    edges.add(new Edge(row, col, row, col + 1));
                }
            }
        }
        
        // Shuffle edges
        shuffleEdges(edges);
        
        // Kruskal's algorithm
        UnionFind uf = new UnionFind(rows * cols);
        for (Edge edge : edges) {
            int cell1 = edge.row1 * cols + edge.col1;
            int cell2 = edge.row2 * cols + edge.col2;
            
            if (uf.find(cell1) != uf.find(cell2)) {
                uf.union(cell1, cell2);
                grid[edge.row1][edge.col1].setType(Cell.CellType.PATH);
                grid[edge.row2][edge.col2].setType(Cell.CellType.PATH);
                grid[(edge.row1 + edge.row2) / 2][(edge.col1 + edge.col2) / 2].setType(Cell.CellType.PATH);
            }
        }
    }
    
    private void ensureStartEndAccessible() {
        startCell.setType(Cell.CellType.START);
        endCell.setType(Cell.CellType.END);
        
        // Ensure start and end cells are paths
        startCell.setType(Cell.CellType.PATH);
        endCell.setType(Cell.CellType.PATH);
        
        // Set their types back to START and END
        startCell.setType(Cell.CellType.START);
        endCell.setType(Cell.CellType.END);
    }
    
    private void addWalls(int row, int col, List<int[]> walls) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isValid(newRow, newCol) && grid[newRow][newCol].getType() == Cell.CellType.WALL) {
                walls.add(new int[]{newRow, newCol});
            }
        }
    }
    
    private int countAdjacentPaths(int row, int col) {
        int count = 0;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isValid(newRow, newCol) && grid[newRow][newCol].getType() == Cell.CellType.PATH) {
                count++;
            }
        }
        return count;
    }
    
    private boolean isValid(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
    
    private void shuffleArray(int[][] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
    
    private void shuffleEdges(List<Edge> edges) {
        for (int i = edges.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Edge temp = edges.get(i);
            edges.set(i, edges.get(j));
            edges.set(j, temp);
        }
    }
    
    public void resetMaze() {
        // Reset all cells to walls
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid[row][col].reset();
            }
        }
        
        // Reset start and end cells
        startCell = grid[0][0];
        endCell = grid[rows - 1][cols - 1];
        startCell.setType(Cell.CellType.START);
        endCell.setType(Cell.CellType.END);
    }
    
    public void addObstacle(int row, int col) {
        if (isValid(row, col) && !grid[row][col].isSource() && !grid[row][col].isDestination()) {
            grid[row][col].setType(Cell.CellType.OBSTACLE);
        }
    }
    
    public void removeObstacle(int row, int col) {
        if (isValid(row, col) && grid[row][col].getType() == Cell.CellType.OBSTACLE) {
            grid[row][col].setType(Cell.CellType.PATH);
        }
    }
    
    /**
     * Sets a new start cell at the specified location
     */
    public boolean setStartCell(int row, int col) {
        if (!isValid(row, col) || !grid[row][col].isWalkable()) {
            return false;
        }
        
        // Clear previous start cell
        if (startCell != null) {
            startCell.setType(Cell.CellType.PATH);
        }
        
        // Set new start cell
        startCell = grid[row][col];
        startCell.setType(Cell.CellType.START);
        return true;
    }
    
    /**
     * Sets a new end cell at the specified location
     */
    public boolean setEndCell(int row, int col) {
        if (!isValid(row, col) || !grid[row][col].isWalkable()) {
            return false;
        }
        
        // Clear previous end cell
        if (endCell != null) {
            endCell.setType(Cell.CellType.PATH);
        }
        
        // Set new end cell
        endCell = grid[row][col];
        endCell.setType(Cell.CellType.END);
        return true;
    }
    
    public void clearPath() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid[row][col].setVisited(false);
                grid[row][col].setInPath(false);
                grid[row][col].setDistance(Integer.MAX_VALUE);
                grid[row][col].setParent(null);
            }
        }
    }
    
    // Getters
    public Cell[][] getGrid() { return grid; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public Cell getStartCell() { return startCell; }
    public Cell getEndCell() { return endCell; }
    public Cell getCell(int row, int col) { return isValid(row, col) ? grid[row][col] : null; }
    
    public enum MazeGenerationAlgorithm {
        RECURSIVE_BACKTRACKING,
        PRIMS_ALGORITHM,
        KRUSKALS_ALGORITHM
    }
    
    // Helper classes
    private static class Edge {
        int row1, col1, row2, col2;
        
        Edge(int row1, int col1, int row2, int col2) {
            this.row1 = row1;
            this.col1 = col1;
            this.row2 = row2;
            this.col2 = col2;
        }
    }
    
    private static class UnionFind {
        private int[] parent;
        private int[] rank;
        
        UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
            }
        }
        
        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }
        
        void union(int x, int y) {
            int px = find(x);
            int py = find(y);
            if (px == py) return;
            
            if (rank[px] < rank[py]) {
                parent[px] = py;
            } else if (rank[px] > rank[py]) {
                parent[py] = px;
            } else {
                parent[py] = px;
                rank[px]++;
            }
        }
    }

}
