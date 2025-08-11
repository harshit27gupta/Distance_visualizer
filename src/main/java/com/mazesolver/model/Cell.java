package com.mazesolver.model;

import javafx.scene.paint.Color;

/**
 * Represents a single cell in the maze grid.
 * Each cell can be in different states: wall, path, start, end, visited, etc.
 */
public class Cell {
    private int row;
    private int col;
    private CellType type;
    private boolean visited;
    private boolean inPath;
    private int distance;
    private Cell parent;
    
    public enum CellType {
        WALL(Color.BLACK),
        PATH(Color.WHITE),
        START(Color.GREEN),
        END(Color.RED),
        VISITED(Color.LIGHTBLUE),
        CURRENT(Color.YELLOW),
        OBSTACLE(Color.BROWN);
        
        private final Color color;
        
        CellType(Color color) {
            this.color = color;
        }
        
        public Color getColor() {
            return color;
        }
        
        public boolean isSource() {
            return this == START;
        }
        
        public boolean isDestination() {
            return this == END;
        }
    }
    
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.type = CellType.PATH;
        this.visited = false;
        this.inPath = false;
        this.distance = Integer.MAX_VALUE;
        this.parent = null;
    }
    
    // Getters and Setters
    public int getRow() { return row; }
    public int getCol() { return col; }
    public CellType getType() { return type; }
    public void setType(CellType type) { this.type = type; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }
    public boolean isInPath() { return inPath; }
    public void setInPath(boolean inPath) { this.inPath = inPath; }
    public int getDistance() { return distance; }
    public void setDistance(int distance) { this.distance = distance; }
    public Cell getParent() { return parent; }
    public void setParent(Cell parent) { this.parent = parent; }
    
    public void reset() {
        this.visited = false;
        this.inPath = false;
        this.distance = Integer.MAX_VALUE;
        this.parent = null;
        
        // Don't reset the type - keep walls, start, end, etc.
        if (this.type != CellType.WALL && this.type != CellType.START && this.type != CellType.END) {
            this.type = CellType.PATH;
        }
    }
    
    public boolean isWalkable() {
        return this.type == CellType.PATH || this.type == CellType.START || this.type == CellType.END;
    }
    
    public boolean isSource() {
        return this.type.isSource();
    }
    
    public boolean isDestination() {
        return this.type.isDestination();
    }
    
    @Override
    public String toString() {
        return "Cell[" + row + "," + col + "](" + type + ")";
    }
}
