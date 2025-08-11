package com.mazesolver.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.mazesolver.model.Maze;
import com.mazesolver.util.AlgorithmManager;

/**
 * Main view class that builds the complete JavaFX UI.
 * Contains all the controls, maze display, and algorithm selection panels.
 */
public class MainView {
    
    private BorderPane root;
    private MazeView mazeView;
    private VBox controlPanel;
    private VBox algorithmPanel;
    private VBox infoPanel;
    
    // Control elements
    private ComboBox<String> mazeSizeComboBox;
    private ComboBox<String> generationAlgorithmComboBox;
    private Button generateMazeButton;
    private Button clearPathButton;
    private Button resetMazeButton;
    
    // Interaction mode controls
    private ToggleGroup interactionModeGroup;
    private RadioButton obstacleModeButton;
    private RadioButton setStartModeButton;
    private RadioButton setEndModeButton;
    
    // Visual enhancement controls
    private CheckBox explorationWaveCheckBox;
    private Slider waveRadiusSlider;
    private Label waveRadiusLabel;
    private Label waveRadiusValueLabel;
    
    // Progress tracking
    private ProgressIndicator explorationProgressIndicator;
    private Label progressLabel;
    
    // Algorithm elements
    private ComboBox<String> pathfindingAlgorithmComboBox;
    private Button findPathButton;
    private Button clearObstaclesButton;
    private Slider animationSpeedSlider;
    
    // Info elements
    private TextArea algorithmInfoTextArea;
    private Label performanceInfoLabel;
    private Label mazeInfoLabel;
    
    // Right panel algorithm info
    private TextArea rightPanelAlgorithmInfo;
    
    public MainView() {
        initializeComponents();
        buildLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        // Initialize maze view
        mazeView = new MazeView();
        
        // Initialize control elements
        mazeSizeComboBox = new ComboBox<>();
        mazeSizeComboBox.getItems().addAll("10x10", "15x15", "20x20", "25x25", "30x30");
        mazeSizeComboBox.setValue("15x15");
        
        generationAlgorithmComboBox = new ComboBox<>();
        generationAlgorithmComboBox.getItems().addAll("Recursive Backtracking", "Prim's Algorithm", "Kruskal's Algorithm");
        generationAlgorithmComboBox.setValue("Recursive Backtracking");
        
        generateMazeButton = new Button("Generate Maze");
        generateMazeButton.setMaxWidth(Double.MAX_VALUE);
        generateMazeButton.getStyleClass().add("primary-button");
        
        clearPathButton = new Button("Clear Path");
        clearPathButton.setMaxWidth(Double.MAX_VALUE);
        clearPathButton.getStyleClass().add("secondary-button");
        
        resetMazeButton = new Button("Reset Maze");
        resetMazeButton.setMaxWidth(Double.MAX_VALUE);
        resetMazeButton.getStyleClass().add("secondary-button");
        
        // Initialize interaction mode controls
        interactionModeGroup = new ToggleGroup();
        
        obstacleModeButton = new RadioButton("Add/Remove Obstacles");
        obstacleModeButton.setToggleGroup(interactionModeGroup);
        obstacleModeButton.setSelected(true);
        obstacleModeButton.setMaxWidth(Double.MAX_VALUE);
        
        setStartModeButton = new RadioButton("Set Start Point");
        setStartModeButton.setToggleGroup(interactionModeGroup);
        setStartModeButton.setMaxWidth(Double.MAX_VALUE);
        
        setEndModeButton = new RadioButton("Set End Point");
        setEndModeButton.setToggleGroup(interactionModeGroup);
        setEndModeButton.setMaxWidth(Double.MAX_VALUE);
        
        // Initialize visual enhancement controls
        explorationWaveCheckBox = new CheckBox("Enable Exploration Wave");
        explorationWaveCheckBox.getStyleClass().add("visual-enhancement");
        
        waveRadiusSlider = new Slider(1, 5, 2);
        waveRadiusSlider.setShowTickLabels(true);
        waveRadiusSlider.setShowTickMarks(true);
        waveRadiusSlider.setMajorTickUnit(1);
        waveRadiusSlider.setMinorTickCount(0);
        waveRadiusSlider.getStyleClass().add("slider");
        
        waveRadiusLabel = new Label("Wave Radius:");
        waveRadiusValueLabel = new Label("2");
        
        // Initialize progress tracking
        explorationProgressIndicator = new ProgressIndicator();
        explorationProgressIndicator.setMinWidth(100);
        explorationProgressIndicator.setMinHeight(10);
        explorationProgressIndicator.setMaxWidth(100);
        explorationProgressIndicator.setMaxHeight(10);
        explorationProgressIndicator.setVisible(false); // Hidden by default
        
        progressLabel = new Label("Exploring...");
        progressLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        progressLabel.setTextFill(Color.BLACK);
        progressLabel.setMinWidth(150); // Ensure enough width for path length message
        progressLabel.setWrapText(true); // Allow text wrapping if needed
        
        // Initialize algorithm elements
        pathfindingAlgorithmComboBox = new ComboBox<>();
        pathfindingAlgorithmComboBox.getItems().addAll("Dijkstra's Algorithm", "A* Algorithm", "Breadth-First Search", "Depth-First Search");
        pathfindingAlgorithmComboBox.setValue("Dijkstra's Algorithm");
        
        findPathButton = new Button("Find Path");
        findPathButton.setMaxWidth(Double.MAX_VALUE);
        findPathButton.getStyleClass().add("primary-button");
        
        clearObstaclesButton = new Button("Clear Obstacles");
        clearObstaclesButton.setMaxWidth(Double.MAX_VALUE);
        clearObstaclesButton.getStyleClass().add("secondary-button");
        
        animationSpeedSlider = new Slider(10, 200, 50);
        animationSpeedSlider.setShowTickLabels(true);
        animationSpeedSlider.setShowTickMarks(true);
        animationSpeedSlider.setMajorTickUnit(50);
        animationSpeedSlider.setMinorTickCount(4);
        
        // Initialize info elements
        algorithmInfoTextArea = new TextArea();
        algorithmInfoTextArea.setEditable(false);
        algorithmInfoTextArea.setPrefRowCount(10); // Increased from 8 to accommodate more info
        algorithmInfoTextArea.setWrapText(true);
        algorithmInfoTextArea.getStyleClass().add("info-text");
        algorithmInfoTextArea.setPromptText("Select an algorithm to see complexity information...");
        
        performanceInfoLabel = new Label("Performance: Ready");
        performanceInfoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        performanceInfoLabel.setMinWidth(200); // Ensure enough width for performance info
        performanceInfoLabel.setWrapText(true); // Allow text wrapping if needed
        
        mazeInfoLabel = new Label("Maze: Not generated");
        mazeInfoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    }
    
    private void buildLayout() {
        root = new BorderPane();
        
        // Build control panel (left)
        controlPanel = buildControlPanel();
        
        // Build algorithm panel (right)
        algorithmPanel = buildAlgorithmPanel();
        
        // Build info panel (bottom)
        infoPanel = buildInfoPanel();
        
        // Set up the main layout
        root.setLeft(controlPanel);
        
        // Wrap maze view in a scroll pane for large mazes
        ScrollPane mazeScrollPane = new ScrollPane(mazeView.getRoot());
        mazeScrollPane.setFitToWidth(true);
        mazeScrollPane.setFitToHeight(true);
        mazeScrollPane.setPannable(true); // Allow panning for large mazes
        
        root.setCenter(mazeScrollPane);
        root.setRight(algorithmPanel);
        root.setBottom(infoPanel);
        
        // Set padding and spacing
        root.setPadding(new Insets(10));
        BorderPane.setMargin(controlPanel, new Insets(0, 10, 0, 0));
        BorderPane.setMargin(algorithmPanel, new Insets(0, 0, 0, 10));
        BorderPane.setMargin(infoPanel, new Insets(10, 0, 0, 0));
    }
    
    private VBox buildControlPanel() {
        VBox panel = new VBox(8); // Reduced spacing to 8 for better fit
        panel.setPrefWidth(240); // Increased from 220 to 240 for better element fit
        panel.setPadding(new Insets(15));
        panel.getStyleClass().add("control-panel");
        
        // Maze generation section
        Label generationLabel = new Label("Maze Generation");
        generationLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        generationLabel.setTextFill(Color.DARKBLUE);
        
        Label sizeLabel = new Label("Maze Size:");
        sizeLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        Label algorithmLabel = new Label("Generation Algorithm:");
        algorithmLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        // Animation speed section
        Label speedLabel = new Label("Animation Speed:");
        speedLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        Label speedValueLabel = new Label("100%");
        speedValueLabel.setFont(Font.font("System", FontWeight.NORMAL, 10));
        speedValueLabel.setTextFill(Color.GRAY);
        
        // Bind speed label to slider
        animationSpeedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            speedValueLabel.setText(String.format("%.0f%%", newVal.doubleValue()));
        });
        
        // Interaction mode section
        Label interactionLabel = new Label("Interaction Mode:");
        interactionLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        // Visual enhancements section
        Label visualLabel = new Label("Visual Enhancements:");
        visualLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        // Create a scrollable container for the control panel content
        VBox contentBox = new VBox(8);
        contentBox.getChildren().addAll(
            generationLabel,
            sizeLabel,
            mazeSizeComboBox,
            algorithmLabel,
            generationAlgorithmComboBox,
            generateMazeButton,
            new Separator(),
            speedLabel,
            animationSpeedSlider,
            speedValueLabel,
            new Separator(),
            interactionLabel,
            obstacleModeButton,
            setStartModeButton,
            setEndModeButton,
            new Separator(),
            visualLabel,
            explorationWaveCheckBox,
            new HBox(10, waveRadiusLabel, waveRadiusSlider, waveRadiusValueLabel),
            new Separator(),
            clearPathButton,
            resetMazeButton
        );
        
        // Wrap content in ScrollPane to handle overflow
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPadding(new Insets(0));
        
        panel.getChildren().add(scrollPane);
        
        // Set proper sizing constraints
        panel.setMinHeight(650); // Increased minimum height
        panel.setPrefHeight(650); // Set preferred height to match minimum
        
        return panel;
    }
    
    private VBox buildAlgorithmPanel() {
        VBox panel = new VBox(10); // Reduced spacing to 10 for better fit
        panel.setPrefWidth(300); // Increased from 280 to 300 for better element fit
        panel.setPadding(new Insets(15));
        panel.getStyleClass().add("algorithm-panel");
        
        // Algorithm selection section
        Label algorithmLabel = new Label("Pathfinding Algorithm");
        algorithmLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        algorithmLabel.setTextFill(Color.DARKGREEN);
        
        Label selectLabel = new Label("Select Algorithm:");
        selectLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        // Instructions
        TextArea instructions = new TextArea(
            "1. Generate a maze\n" +
            "2. Select interaction mode:\n" +
            "   • Obstacles: Add/remove obstacles\n" +
            "   • Start: Click to set start point\n" +
            "   • End: Click to set end point\n" +
            "3. Select an algorithm\n" +
            "4. Click 'Find Path'\n" +
            "5. Compare algorithm performance"
        );
        instructions.setPrefRowCount(7);
        instructions.setEditable(false);
        instructions.getStyleClass().add("instructions-text");
        
        // Algorithm Information Area - Show complexity info prominently
        Label algoInfoTitle = new Label("Algorithm Information:");
        algoInfoTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        algoInfoTitle.setTextFill(Color.DARKGREEN);
        
        rightPanelAlgorithmInfo = new TextArea();
        rightPanelAlgorithmInfo.setEditable(false);
        rightPanelAlgorithmInfo.setPrefRowCount(12); // Increased from 10 to 12 for better visibility
        rightPanelAlgorithmInfo.setWrapText(true);
        rightPanelAlgorithmInfo.getStyleClass().add("info-text");
        rightPanelAlgorithmInfo.setPromptText("Select an algorithm to see time and space complexity...");
        rightPanelAlgorithmInfo.setMinHeight(200); // Increased minimum height to ensure visibility
        rightPanelAlgorithmInfo.setPrefHeight(200); // Set preferred height to match minimum
        
        // Progress indicator
        HBox progressBox = new HBox(10);
        progressBox.setAlignment(Pos.CENTER_LEFT);
        progressBox.getChildren().addAll(explorationProgressIndicator, progressLabel);
        
        // Create a scrollable container for the algorithm panel content
        VBox contentBox = new VBox(10);
        contentBox.getChildren().addAll(
            algorithmLabel,
            selectLabel,
            pathfindingAlgorithmComboBox,
            findPathButton,
            new Separator(),
            clearObstaclesButton,
            new Separator(),
            progressBox,
            new Separator(),
            algoInfoTitle,
            rightPanelAlgorithmInfo,
            new Separator(),
            instructions
        );
        
        // Wrap content in ScrollPane to handle overflow
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPadding(new Insets(0));
        
        panel.getChildren().add(scrollPane);
        
        // Ensure proper spacing and prevent elements from being cut off
        panel.setMinHeight(750); // Increased minimum height
        panel.setPrefHeight(750); // Set preferred height to match minimum
        
        return panel;
    }
    
    private VBox buildInfoPanel() {
        VBox panel = new VBox(10);
        panel.setPrefHeight(180); // Increased from 150 to 180 for better visibility
        panel.setPadding(new Insets(15));
        panel.getStyleClass().add("info-panel");
        
        // Algorithm information
        Label algoInfoTitle = new Label("Algorithm Information:");
        algoInfoTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        algoInfoTitle.setTextFill(Color.DARKGREEN);
        
        // Performance metrics
        Label perfTitle = new Label("Performance Metrics:");
        perfTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        perfTitle.setTextFill(Color.DARKBLUE);
        
        // Maze information
        Label mazeInfoTitle = new Label("Maze Information:");
        mazeInfoTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        mazeInfoTitle.setTextFill(Color.DARKRED);
        
        // Ensure algorithm info text area has proper sizing
        algorithmInfoTextArea.setPrefRowCount(10); // Increased from 8 to 10 for better visibility
        algorithmInfoTextArea.setWrapText(true);
        algorithmInfoTextArea.setMinHeight(100); // Increased minimum height
        algorithmInfoTextArea.setPrefHeight(100); // Set preferred height to match minimum
        
        HBox infoBox = new HBox(20);
        VBox algoBox = new VBox(5, algoInfoTitle, algorithmInfoTextArea);
        VBox perfBox = new VBox(5, perfTitle, performanceInfoLabel);
        VBox mazeBox = new VBox(5, mazeInfoTitle, mazeInfoLabel);
        
        infoBox.getChildren().addAll(algoBox, perfBox, mazeBox);
        HBox.setHgrow(algoBox, Priority.ALWAYS);
        HBox.setHgrow(perfBox, Priority.ALWAYS);
        HBox.setHgrow(mazeBox, Priority.ALWAYS);
        
        panel.getChildren().add(infoBox);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Algorithm selection change - this will be handled by the controller
        // pathfindingAlgorithmComboBox.setOnAction(e -> updateAlgorithmInfo());
        
        // Interaction mode change handlers
        obstacleModeButton.setOnAction(e -> {
            mazeView.setInteractionMode(MazeView.InteractionMode.OBSTACLE);
        });
        
        setStartModeButton.setOnAction(e -> {
            mazeView.setInteractionMode(MazeView.InteractionMode.SET_START);
        });
        
        setEndModeButton.setOnAction(e -> {
            mazeView.setInteractionMode(MazeView.InteractionMode.SET_END);
        });
        
        // Wave radius slider change handler
        waveRadiusSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mazeView != null) {
                mazeView.setExplorationWaveRadius(newVal.intValue());
            }
        });
        
        // Visual enhancement control handlers
        explorationWaveCheckBox.setOnAction(e -> {
            if (mazeView != null) {
                mazeView.setShowExplorationWave(explorationWaveCheckBox.isSelected());
            }
        });
        
        // Initial algorithm info will be handled by the controller
        // updateAlgorithmInfo();
    }
    
    // Remove this duplicate method - let the controller handle it
    // private void updateAlgorithmInfo() {
    //     String selectedAlgorithm = pathfindingAlgorithmComboBox.getValue();
    //     if (selectedAlgorithm != null) {
    //         // This will be updated by the controller when algorithms are available
    //         algorithmInfoTextArea.setText("Selected: " + selectedAlgorithm);
    //     }
    // }
    
    // Getters for controller access
    public BorderPane getRoot() { return root; }
    public MazeView getMazeView() { return mazeView; }
    public ComboBox<String> getMazeSizeComboBox() { return mazeSizeComboBox; }
    public ComboBox<String> getGenerationAlgorithmComboBox() { return generationAlgorithmComboBox; }
    public Button getGenerateMazeButton() { return generateMazeButton; }
    public Button getClearPathButton() { return clearPathButton; }
    public Button getResetMazeButton() { return resetMazeButton; }
    public ComboBox<String> getPathfindingAlgorithmComboBox() { return pathfindingAlgorithmComboBox; }
    public Button getFindPathButton() { return findPathButton; }
    public Button getClearObstaclesButton() { return clearObstaclesButton; }
    public Slider getAnimationSpeedSlider() { return animationSpeedSlider; }
    public TextArea getAlgorithmInfoTextArea() { return algorithmInfoTextArea; }
    public Label getPerformanceInfoLabel() { return performanceInfoLabel; }
    public Label getMazeInfoLabel() { return mazeInfoLabel; }
    
    // Interaction mode control getters
    public ToggleGroup getInteractionModeGroup() { return interactionModeGroup; }
    public RadioButton getObstacleModeButton() { return obstacleModeButton; }
    public RadioButton getSetStartModeButton() { return setStartModeButton; }
    public RadioButton getSetEndModeButton() { return setEndModeButton; }
    
    // Visual enhancement control getters
    public CheckBox getExplorationWaveCheckBox() { return explorationWaveCheckBox; }
    public Slider getWaveRadiusSlider() { return waveRadiusSlider; }
    public ProgressIndicator getExplorationProgressIndicator() { return explorationProgressIndicator; }
    public Label getProgressLabel() { return progressLabel; }
    
    // Getter for the algorithm info text area in the right panel
    public TextArea getRightPanelAlgorithmInfo() { 
        return rightPanelAlgorithmInfo;
    }
}
