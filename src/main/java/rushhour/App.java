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

import rushhour.lib.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

public class App extends Application {
    private Map<Character, Rectangle> carRectangles = new HashMap<>();
    private Board board;
    private GridPane gridPane;
    private ComboBox<String> algoBox, heuristicBox;
    private Button loadButton, solveButton;
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
    
    private static final double DEFAULT_SPEED = 1.0;
    private static final double MIN_SPEED = 0.1;
    private static final double MAX_SPEED = 3.0;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        BorderPane mainContainer = new BorderPane();
        mainContainer.getStyleClass().add("main-container");
        
        // Initialize controls
        gridPane = new GridPane();
        algoBox = new ComboBox<>();
        heuristicBox = new ComboBox<>();
        loadButton = new Button("Load Puzzle");
        solveButton = new Button("Solve Puzzle");
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
        
        // Komponen-komponen utama UI
        HBox titleBox = UIBuilder.createTitleArea();
        mainContainer.setTop(titleBox);
        
        VBox boardArea = UIBuilder.createBoardArea(gridPane, moveCountLabel);
        mainContainer.setCenter(boardArea);
        
        VBox controlPanel = UIBuilder.createControlPanel(
            algoBox, heuristicBox, loadButton, solveButton, 
            nodesExplored, timeElapsed, statusLabel,
            e -> handleAlgorithmChange(),
            e -> handleLoad(),
            e -> handleSolve()
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
        
        Scene scene = new Scene(mainContainer, 900, 750);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        try {
            // Try loading from different possible paths
            javafx.scene.image.Image icon = new javafx.scene.image.Image(getClass().getResourceAsStream("/rushhour/RushHourLogo.png"));
            
            if (icon.isError()) {
                // Fallback to other possible paths
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
        
        // Inisialisasi board
        board = new Board(6, 6);
        BoardRenderer.drawBoard(board, gridPane, carRectangles, currentlyMovingCar);
        
        stage.setScene(scene);
        stage.setTitle("Rush Hour Puzzle Solver");
        stage.show();
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
                    } else {
                        statusLabel.setText("Solution found with " + moves.size() + " moves!");
                        moveCountLabel.setText("Move: 0 / " + moves.size());
                        
                        playButton.setDisable(false);
                        stopButton.setDisable(false);
                        animationSpeedSlider.setDisable(false);
                        slowDownButton.setDisable(false);
                        speedUpButton.setDisable(false);
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
                    celebration.play();                    playButton.setDisable(true);
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
            AnimationUtils.resetRectangleAppearance(rect, (Color)rect.getFill());        }
    }
}