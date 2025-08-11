# Advanced Maze Solver - Algorithm Visualizer

A JavaFX-based maze solving application that demonstrates and visualizes various pathfinding algorithms in real-time.

## 🚀 Features

- **Multiple Pathfinding Algorithms**: 
  - A* (A-Star) Algorithm
  - Breadth-First Search (BFS)
  - Depth-First Search (DFS)
  - Dijkstra's Algorithm
- **Interactive Maze Generation**: Create custom mazes or generate random ones
- **Real-time Visualization**: Watch algorithms solve mazes step-by-step
- **Modern JavaFX UI**: Clean, responsive interface with customizable themes
- **Performance Metrics**: Track algorithm performance and execution time

## 🛠️ Technology Stack

- **Java 17+** - Core programming language
- **JavaFX 17** - Modern UI framework
- **Maven** - Build and dependency management
- **CSS** - Custom styling and theming

## 📋 Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven 3.6+ (optional, for building from source)

## 🚀 Getting Started

### Option 1: Run the JAR file (Recommended)

1. Download the latest release JAR file
2. Ensure you have Java 17+ installed
3. Run: `java -jar advanced-maze-solver-1.0.0.jar`

### Option 2: Build from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/advanced-maze-solver.git
   cd advanced-maze-solver
   ```

2. Build the project:
   ```bash
   mvn clean compile
   ```

3. Run the application:
   ```bash
   mvn javafx:run
   ```

## 🎮 How to Use

1. **Generate a Maze**: Click "Generate Maze" to create a new random maze
2. **Choose Algorithm**: Select your preferred pathfinding algorithm from the dropdown
3. **Set Start/End**: Click on cells to set start (green) and end (red) points
4. **Solve**: Click "Solve Maze" to watch the algorithm in action
5. **Customize**: Adjust maze size, generation algorithm, and visualization speed

## 🏗️ Project Structure

```
src/main/java/com/mazesolver/
├── algorithm/          # Pathfinding algorithm implementations
│   ├── AStarAlgorithm.java
│   ├── BreadthFirstSearchAlgorithm.java
│   ├── DepthFirstSearchAlgorithm.java
│   ├── DijkstraAlgorithm.java
│   └── AbstractPathfindingAlgorithm.java
├── model/              # Data models
│   ├── Cell.java       # Individual maze cell
│   └── Maze.java       # Maze representation and generation
├── ui/                 # User interface components
│   ├── MainView.java   # Main application window
│   ├── MainController.java # Application logic controller
│   └── MazeView.java   # Maze visualization component
├── util/               # Utility classes
│   └── AlgorithmManager.java # Algorithm management
└── MazeSolverApp.java  # Main application class
```

## 🔧 Configuration

The application can be customized through:
- **Maze Size**: Adjustable grid dimensions
- **Generation Algorithm**: Different maze generation strategies
- **Visualization Speed**: Control animation timing
- **Theme**: Customizable CSS styling

## 📊 Algorithm Details

### A* Algorithm
- **Best for**: Finding shortest path with heuristic guidance
- **Time Complexity**: O(E log V) where E is edges and V is vertices
- **Space Complexity**: O(V)

### Breadth-First Search
- **Best for**: Finding shortest path in unweighted graphs
- **Time Complexity**: O(V + E)
- **Space Complexity**: O(V)

### Depth-First Search
- **Best for**: Exploring maze structure, not optimal for shortest path
- **Time Complexity**: O(V + E)
- **Space Complexity**: O(V)

### Dijkstra's Algorithm
- **Best for**: Finding shortest path in weighted graphs
- **Time Complexity**: O((V + E) log V)
- **Space Complexity**: O(V)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- JavaFX team for the excellent UI framework
- Maven community for build tools
- Open source contributors and algorithm researchers

## 📞 Support

If you encounter any issues or have questions:
- Open an issue on GitHub
- Check the existing issues for solutions
- Review the code documentation

## 🔮 Future Enhancements

- [ ] Additional pathfinding algorithms (Bellman-Ford, Floyd-Warshall)
- [ ] 3D maze visualization
- [ ] Algorithm comparison mode
- [ ] Export/import maze functionality
- [ ] Mobile application version
- [ ] Web-based version using JavaFX WebView

---

**Happy Maze Solving! 🧩✨**
