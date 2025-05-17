package rushhour;

import javafx.application.Application;
import javafx.animation.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Scale;
import javafx.geometry.Point2D;

import rushhour.lib.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

public class App extends Application {
    private Map<Character, Rectangle> carRectangles = new HashMap<>();
    private Board board;
    private GridPane gridPane;
    private ComboBox<String> algoBox, heuristicBox;
    private Button loadButton, solveButton, saveButton;
    private Label statusLabel;
    private Label nodesExplored;
    private Label timeElapsed;
    private List<Move> moves;
    private Timeline timeline;
    private SequentialTransition sequentialAnimation;
    private Button playButton, pauseButton, stopButton, speedUpButton, slowDownButton;
    private Slider animationSpeedSlider;
    private Label speedLabel;
    private Label moveCountLabel;
    private int currentMoveIndex = 0;
    private char currentlyMovingCar = '.';
    
    private StackPane boardContainer;
    private double scaleFactor = 1.0;
    private double minScale = 0.5;
    private double maxScale = 3.0;
    
    private static final double DEFAULT_SPEED = 1.0;
    private static final double MIN_SPEED = 0.1;
    private static final double MAX_SPEED = 3.0;
    private static final double ZOOM_FACTOR = 1.1;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        BorderPane mainContainer = new BorderPane();
        mainContainer.getStyleClass().add("main-container");
        
        gridPane = new GridPane();
        algoBox = new ComboBox<>();
        heuristicBox = new ComboBox<>();
        loadButton = new Button("Load Puzzle");
        solveButton = new Button("Solve Puzzle");
        saveButton = new Button("Save Solution");
        saveButton.setDisable(true);
        
        statusLabel = new Label("Select a puzzle file to begin!");
        nodesExplored = new Label("0");
        timeElapsed = new Label("0 ms");
        playButton = new Button();
        pauseButton = new Button();
        stopButton = new Button();
        speedUpButton = new Button();
        slowDownButton = new Button();
        animationSpeedSlider = new Slider(MIN_SPEED, MAX_SPEED, DEFAULT_SPEED);
        speedLabel = new Label("Animation Speed: 1.0x");
        moveCountLabel = new Label("Move: 0 / 0");
        
        HBox titleBox = UIBuilder.createTitleArea();
        mainContainer.setTop(titleBox);
        
        boardContainer = new StackPane();
        boardContainer.getChildren().add(gridPane);
        VBox boardArea = UIBuilder.createBoardArea(boardContainer, moveCountLabel);
        
        setupZoomHandlers(boardContainer);
        
        mainContainer.setCenter(boardArea);
        
        HBox zoomControls = createZoomControls();
        
        VBox controlPanel = UIBuilder.createControlPanel(
            algoBox, heuristicBox, loadButton, solveButton, saveButton,
            nodesExplored, timeElapsed, statusLabel, zoomControls,
            e -> handleAlgorithmChange(),
            e -> handleLoad(),
            e -> handleSolve(),
            e -> saveSolutionToFile()
        );
        mainContainer.setBottom(controlPanel);
        
        VBox animationControls = UIBuilder.createAnimationControls(
            playButton, pauseButton, stopButton,
            slowDownButton, speedUpButton, animationSpeedSlider, speedLabel,
            e -> playAnimation(),
            e -> pauseAnimation(),
            e -> stopAnimation(),
            e -> changeSpeed(-0.1),
            e -> changeSpeed(0.1),
            (obs, oldVal, newVal) -> updateSpeed(newVal.doubleValue())
        );
        mainContainer.setRight(animationControls);
        
        Scene scene = new Scene(mainContainer, 900, 900);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        try {
            javafx.scene.image.Image icon = new javafx.scene.image.Image(getClass().getResourceAsStream("/rushhour/RushHourLogo.png"));
            
            if (icon.isError()) {
                icon = new javafx.scene.image.Image(getClass().getResourceAsStream("RushHourLogo.png"));
            }
            
            if (!icon.isError()) {
                stage.getIcons().add(icon);
            } else {
                System.err.println("Could not load application icon.");
            }
        } catch (Exception e) {
            System.err.println("Error loading application icon: " + e.getMessage());
        }
        
        board = new Board(6, 6);
        BoardRenderer.drawBoard(board, gridPane, carRectangles, currentlyMovingCar);
        
        stage.setScene(scene);
        stage.setTitle("Rush Hour Puzzle Solver");
        stage.show();
    }
    
    private void saveSolutionToFile() {
        if (moves == null || moves.isEmpty()) {
            statusLabel.setText("No solution to save.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Solution");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        fileChooser.setInitialFileName("rush_hour_solution.txt");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Rush Hour Puzzle Solution");
                writer.println("-------------------------");
                writer.println("Algorithm used: " + algoBox.getValue() + 
                              (heuristicBox.isDisabled() ? "" : " with " + heuristicBox.getValue() + " heuristic"));
                writer.println("Total moves: " + moves.size());
                writer.println("Nodes explored: " + nodesExplored.getText());
                writer.println("Time elapsed: " + timeElapsed.getText());
                writer.println("-------------------------");
                writer.println();
                
                writer.println("Initial board state:");
                printBoardToWriter(board, writer);
                writer.println();
                
                Board animBoard = board.copy();
                for (int i = 0; i < moves.size(); i++) {
                    Move move = moves.get(i);
                    writer.println("Gerakan ke- " + (i+1) + ": " + formatMove(move));
                    move.applyMove(animBoard, move);
                    printBoardToWriter(animBoard, writer);
                    writer.println();
                }
                
                statusLabel.setText("Solution saved successfully to " + file.getName());
            } catch (FileNotFoundException e) {
                statusLabel.setText("Error saving solution: " + e.getMessage());
            }
        }
    }
    
    private String formatMove(Move move) {
        char carId = move.getCarId();
        String direction = move.getDirection();
        switch (direction) {
            case "up":    direction = "atas"; break;
            case "down":  direction = "bawah"; break;
            case "left":  direction = "kiri"; break;
            case "right": direction = "kanan"; break;
        }
        return "Car " + carId + "-" + direction;
    }
    
    private void printBoardToWriter(Board board, PrintWriter writer) {
        char[][] grid = board.getGrid();
        int height = board.getHeight();
        int width = board.getWidth();
        
        // Print top border
        writer.print("+");
        for (int x = 0; x < width; x++) {
            writer.print("--");
        }
        writer.println("+");
        
        for (int y = 1; y <= height; y++) {
            writer.print("|");
            for (int x = 1; x <= width; x++) {
                writer.print(grid[y][x] + " ");
            }
            writer.println("|");
        }
        
        writer.print("+");
        for (int x = 0; x < width; x++) {
            writer.print("--");
        }
        writer.println("+");
        
        int doorX = board.getDoorX();
        int doorY = board.getDoorY();
        writer.println("Door at position: (" + doorX + ", " + doorY + ")");
    }
    
    private HBox createZoomControls() {
        HBox zoomControls = new HBox(10);
        zoomControls.getStyleClass().add("zoom-controls");
        
        Button zoomInButton = new Button("+");
        zoomInButton.getStyleClass().add("zoom-button");
        zoomInButton.setOnAction(e -> zoom(ZOOM_FACTOR));
        
        Button zoomOutButton = new Button("-");
        zoomOutButton.getStyleClass().add("zoom-button");
        zoomOutButton.setOnAction(e -> zoom(1/ZOOM_FACTOR));
        
        Button resetZoomButton = new Button("Reset");
        resetZoomButton.getStyleClass().add("zoom-button");
        resetZoomButton.setOnAction(e -> resetZoom());
        
        Label zoomLabel = new Label("Zoom:");
        zoomLabel.getStyleClass().add("zoom-label");
        
        zoomControls.getChildren().addAll(zoomLabel, zoomOutButton, resetZoomButton, zoomInButton);
        return zoomControls;
    }
    
    private void setupZoomHandlers(StackPane boardContainer) {
        boardContainer.setOnScroll(this::handleScroll);
        
        Scale scale = new Scale();
        scale.setPivotX(225);
        scale.setPivotY(225);
        scale.setX(scaleFactor);
        scale.setY(scaleFactor);
        gridPane.getTransforms().add(scale);
        
        gridPane.setTranslateX(0);
        gridPane.setTranslateY(0);
        
        boardContainer.setOnMousePressed(event -> {
            boardContainer.setCursor(javafx.scene.Cursor.CLOSED_HAND);
            boardContainer.setUserData(new Point2D(event.getSceneX(), event.getSceneY()));
            event.consume();
        });
        
        boardContainer.setOnMouseDragged(event -> {
            if (boardContainer.getUserData() instanceof Point2D) {
                Point2D dragStart = (Point2D) boardContainer.getUserData();
                double deltaX = event.getSceneX() - dragStart.getX();
                double deltaY = event.getSceneY() - dragStart.getY();
                
                gridPane.setTranslateX(gridPane.getTranslateX() + deltaX);
                gridPane.setTranslateY(gridPane.getTranslateY() + deltaY);
                
                boardContainer.setUserData(new Point2D(event.getSceneX(), event.getSceneY()));
                event.consume();
            }
        });
        
        boardContainer.setOnMouseReleased(event -> {
            boardContainer.setCursor(javafx.scene.Cursor.DEFAULT);
            enforceGridBounds();
            event.consume();
        });
    }

    private void enforceGridBounds() {
        double boardWidth = 450;
        double boardHeight = 450;
        double gridWidth = boardWidth * scaleFactor;
        double gridHeight = boardHeight * scaleFactor;
        
        double maxX = (gridWidth - boardWidth) / 2;
        double maxY = (gridHeight - boardHeight) / 2;
        
        if (scaleFactor > 1.0) {
            gridPane.setTranslateX(Math.max(-maxX, Math.min(maxX, gridPane.getTranslateX())));
            gridPane.setTranslateY(Math.max(-maxY, Math.min(maxY, gridPane.getTranslateY())));
        }
    }

    private void handleScroll(ScrollEvent event) {
        double delta = event.getDeltaY();
        double scaleFactor = (delta > 0) ? ZOOM_FACTOR : 1/ZOOM_FACTOR;
        zoom(scaleFactor);
        event.consume();
    }
    
    private void zoom(double factor) {
        double newScale = scaleFactor * factor;
        
        if (newScale >= minScale && newScale <= maxScale) {
            scaleFactor = newScale;
            
            Scale scale = (Scale) gridPane.getTransforms().get(0);
            scale.setX(scaleFactor);
            scale.setY(scaleFactor);
            
            enforceGridBounds();
            
            statusLabel.setText("Zoom: " + String.format("%.1f", scaleFactor * 100) + "%");
        }
    }
    
    private void resetZoom() {
        scaleFactor = 1.0;
        Scale scale = (Scale) gridPane.getTransforms().get(0);
        scale.setX(scaleFactor);
        scale.setY(scaleFactor);
        
        gridPane.setTranslateX(0);
        gridPane.setTranslateY(0);
        
        statusLabel.setText("Zoom reset to 100%");
    }
    
    private void handleAlgorithmChange() {
        String selected = algoBox.getValue();
        if ("UCS".equals(selected)) {
            heuristicBox.setDisable(true);
            heuristicBox.setValue(null);
        } else {
            heuristicBox.setDisable(false);
        }
    }

    private void handleLoad() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Rush Hour Puzzle File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = chooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                board = Load.loadGame(selectedFile);
                currentlyMovingCar = '.'; 
                BoardRenderer.drawBoard(board, gridPane, carRectangles, currentlyMovingCar);
                statusLabel.setText("Puzzle loaded successfully from " + selectedFile.getName());
                solveButton.setDisable(false);
                
                resetAnimationControls();
                
            } catch (FileNotFoundException ex) {
                statusLabel.setText("File tidak ditemukan: " + ex.getMessage());
            } catch (InputMismatchException ex) {
                statusLabel.setText("Format file tidak valid: " + ex.getMessage());
            } catch (RuntimeException ex) {
                statusLabel.setText("Terjadi kesalahan saat memuat puzzle: " + ex.getMessage());
            }
        }
    }

    private void handleSolve() {
        String algo = algoBox.getValue();
        String heuristic = heuristicBox.getValue();
        
        if (algo == null || (algo.matches("A\\*|Greedy|Fringe") && heuristic == null)) {
            statusLabel.setText("Please select both algorithm and heuristic if required.");
            return;
        }

        statusLabel.setText("Solving puzzle using " + algo + "...");
        solveButton.setDisable(true);
        
        new Thread(() -> {
            try {
                List<Move> result = null;
                int numNodesExplored = 0;
                long timeElapsedMs = 0;
                
                switch (algo) {
                    case "UCS":
                        UCS ucs = new UCS(board);
                        result = ucs.solve();
                        numNodesExplored = ucs.getNodesExplored();
                        timeElapsedMs = ucs.getTimeElapsed();
                        break;
                    case "A*":
                        AStar aStar = new AStar(board);
                        result = aStar.solve(heuristic);
                        numNodesExplored = aStar.getNodesExplored();
                        timeElapsedMs = aStar.getTimeElapsed();
                        break;
                    case "Greedy":
                        Greedy greedy = new Greedy(board);
                        result = greedy.solve(heuristic);
                        numNodesExplored = greedy.getNodesExplored();
                        timeElapsedMs = greedy.getTimeElapsed();
                        break;
                    case "Fringe":
                        Fringe fringe = new Fringe(board);
                        result = fringe.solve(heuristic);
                        numNodesExplored = fringe.getNodesExplored();
                        timeElapsedMs = fringe.getTimeElapsed();
                        break;
                }
                
                final List<Move> finalResult = result;
                final int finalNodes = numNodesExplored;
                final long finalTime = timeElapsedMs;
                
                javafx.application.Platform.runLater(() -> {
                    moves = finalResult;
                    nodesExplored.setText(String.valueOf(finalNodes));
                    timeElapsed.setText(finalTime + " ms");
                    
                    if (moves == null || moves.isEmpty()) {
                        statusLabel.setText("No solution found after exploring " + finalNodes + " nodes.");
                        saveButton.setDisable(true);
                    } else {
                        statusLabel.setText("Solution found with " + moves.size() + " moves!");
                        moveCountLabel.setText("Move: 0 / " + moves.size());
                        
                        playButton.setDisable(false);
                        stopButton.setDisable(false);
                        animationSpeedSlider.setDisable(false);
                        slowDownButton.setDisable(false);
                        speedUpButton.setDisable(false);
                        saveButton.setDisable(false);
                    }
                    
                    solveButton.setDisable(false);
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("Error solving puzzle: " + e.getMessage());
                    solveButton.setDisable(false);
                });
            }
        }).start();
    }

    private void playAnimation() {
        if (moves == null || moves.isEmpty()) return;
        
        cleanupAnimationResources();
        
        if (currentMoveIndex >= moves.size()) {
            stopAnimation();
            return;
        }
        
        Board animBoard = board.copy();
        
        for (int i = 0; i < currentMoveIndex; i++) {
            moves.get(i).applyMove(animBoard, moves.get(i));
        }
        
        currentlyMovingCar = '.';
        BoardRenderer.drawBoard(animBoard, gridPane, carRectangles, currentlyMovingCar);
        
        playButton.setDisable(true);
        pauseButton.setDisable(false);
        
        double speed = animationSpeedSlider.getValue();
        double baseDuration = 500 / speed;
        
        sequentialAnimation = new SequentialTransition();
        
        for (int i = currentMoveIndex; i < moves.size(); i++) {
            final int moveIndex = i;
            Move move = moves.get(moveIndex);
            
            PauseTransition preMovePause = new PauseTransition(Duration.millis(50));
            preMovePause.setOnFinished(e -> {
                currentlyMovingCar = move.getCarId();
                BoardRenderer.drawBoard(animBoard, gridPane, carRectangles, currentlyMovingCar);
            });
            
            Rectangle carRect = carRectangles.get(move.getCarId());
            if (carRect == null) continue;
            
            ParallelTransition moveAnimation = AnimationUtils.createMoveAnimation(move, carRect, baseDuration);
            
            PauseTransition postMovePause = new PauseTransition(Duration.millis(50));
            postMovePause.setOnFinished(e -> {
                move.applyMove(animBoard, move);
                currentlyMovingCar = '.';
                BoardRenderer.drawBoard(animBoard, gridPane, carRectangles, currentlyMovingCar);
                
                currentMoveIndex = moveIndex + 1;
                moveCountLabel.setText("Move: " + currentMoveIndex + " / " + moves.size());
                
                if (moveIndex == moves.size() - 1) {
                    Rectangle mainCar = carRectangles.get('P');
                    ParallelTransition celebration = AnimationUtils.createCelebrationEffect(mainCar);
                    celebration.play();                    
                    playButton.setDisable(true);
                    pauseButton.setDisable(true);
                    statusLabel.setText("🎉 Puzzle solved! The car has escaped! 🎉");
                }
            });
            
            SequentialTransition moveSequence = new SequentialTransition(
                preMovePause,
                moveAnimation,
                postMovePause
            );
            
            sequentialAnimation.getChildren().add(moveSequence);
        }
        
        sequentialAnimation.play();
    }

    private void pauseAnimation() {
        if (sequentialAnimation != null) {
            sequentialAnimation.pause();
            playButton.setDisable(false);
            pauseButton.setDisable(true);
            statusLabel.setText("Animation paused at move " + currentMoveIndex);
        }
    }

    private void stopAnimation() {
        cleanupAnimationResources();
        
        BoardRenderer.drawBoard(board, gridPane, carRectangles, '.');
        currentMoveIndex = 0;
        moveCountLabel.setText("Move: 0 / " + (moves != null ? moves.size() : 0));
        
        playButton.setDisable(false);
        pauseButton.setDisable(true);
        
        statusLabel.setText("Animation reset. Ready to play solution.");
    }
    
    private void changeSpeed(double delta) {
        double currentSpeed = animationSpeedSlider.getValue();
        double newSpeed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, currentSpeed + delta));
        animationSpeedSlider.setValue(newSpeed);
        updateSpeed(newSpeed);
    }
    
    private void updateSpeed(double speed) {
        speedLabel.setText(String.format("Animation Speed: %.1fx", speed));
        
        if (sequentialAnimation != null && sequentialAnimation.getStatus() == Animation.Status.RUNNING) {
            int currentMove = currentMoveIndex;
            stopAnimation();
            currentMoveIndex = currentMove;
            playAnimation();
        }
    }
    
    private void resetAnimationControls() {
        cleanupAnimationResources();
        
        playButton.setDisable(true);
        pauseButton.setDisable(true);
        stopButton.setDisable(true);
        animationSpeedSlider.setDisable(true);
        slowDownButton.setDisable(true);
        speedUpButton.setDisable(true);
        saveButton.setDisable(true);
        
        animationSpeedSlider.setValue(DEFAULT_SPEED);
        updateSpeed(DEFAULT_SPEED);
        
        currentMoveIndex = 0;
        moveCountLabel.setText("Move: 0 / 0");
    }
    
    private void cleanupAnimationResources() {
        if (timeline != null) {
            timeline.stop();
        }
        
        if (sequentialAnimation != null) {
            sequentialAnimation.stop();
        }
        
        for (Rectangle rect : carRectangles.values()) {
            AnimationUtils.resetRectangleAppearance(rect, (Color)rect.getFill());        
        }
    }
}