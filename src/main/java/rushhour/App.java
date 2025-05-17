package rushhour;

import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.*;
import javafx.animation.*;
import javafx.util.Duration;
import rushhour.lib.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.List;

import javafx.scene.effect.Glow;
import javafx.scene.effect.DropShadow;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

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
    private Button playButton, pauseButton, stopButton, speedUpButton, slowDownButton;
    private Slider animationSpeedSlider;
    private Label speedLabel;
    private Label moveCountLabel;
    private int currentMoveIndex = 0;
    
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
        
        HBox titleBox = createTitleArea();
        mainContainer.setTop(titleBox);
        
        VBox boardArea = createBoardArea();
        mainContainer.setCenter(boardArea);
        
        VBox controlPanel = createControlPanel();
        mainContainer.setBottom(controlPanel);
        
        VBox animationControls = createAnimationControls();
        mainContainer.setRight(animationControls);
        
        Scene scene = new Scene(mainContainer, 750, 600);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        stage.setScene(scene);
        stage.setTitle("Rush Hour Puzzle Solver");
        stage.show();
    }
    
    private HBox createTitleArea() {
        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(15));
        titleBox.getStyleClass().add("title-box");
        
        Label titleLabel = new Label("Rush Hour Puzzle Solver");
        titleLabel.getStyleClass().add("title-label");
        
        titleBox.getChildren().add(titleLabel);
        return titleBox;
    }
    
    private VBox createBoardArea() {
        VBox boardArea = new VBox(15);
        boardArea.setAlignment(Pos.CENTER);
        boardArea.setPadding(new Insets(10));
        boardArea.getStyleClass().add("board-area");
        
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.getStyleClass().add("grid-pane");
        
        moveCountLabel = new Label("Move: 0 / 0");
        moveCountLabel.getStyleClass().add("status-label");
        
        boardArea.getChildren().addAll(gridPane, moveCountLabel);
        
        drawBoard(new Board(6, 6));
        
        return boardArea;
    }
    
    private VBox createControlPanel() {
        VBox controlPanel = new VBox(10);
        controlPanel.setPadding(new Insets(15));
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.getStyleClass().add("control-panel");
        
        HBox algoBox = new HBox(10);
        algoBox.setAlignment(Pos.CENTER);
        
        Label algoLabel = new Label("Algorithm:");
        algoLabel.getStyleClass().add("control-label");
        
        this.algoBox = new ComboBox<>();
        this.algoBox.getItems().addAll("UCS", "Greedy", "A*", "Fringe");
        this.algoBox.setPromptText("Select Algorithm");
        this.algoBox.getStyleClass().add("combo-box");
        
        Label heuristicLabel = new Label("Heuristic:");
        heuristicLabel.getStyleClass().add("control-label");
        
        heuristicBox = new ComboBox<>();
        heuristicBox.getItems().addAll("blockingCars", "exitDistance");
        heuristicBox.setPromptText("Select Heuristic");
        heuristicBox.getStyleClass().add("combo-box");
        
        algoBox.getChildren().addAll(algoLabel, this.algoBox, heuristicLabel, heuristicBox);
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        loadButton = new Button("Load Puzzle");
        loadButton.getStyleClass().add("action-button");
        loadButton.setGraphic(createIcon("📂", 16));
        
        solveButton = new Button("Solve Puzzle");
        solveButton.getStyleClass().add("action-button");
        solveButton.setGraphic(createIcon("🔍", 16));
        solveButton.setDisable(true);
        
        buttonBox.getChildren().addAll(loadButton, solveButton);
        
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        
        VBox nodesBox = new VBox(5);
        nodesBox.setAlignment(Pos.CENTER);
        Label nodesLabel = new Label("Nodes Explored:");
        nodesLabel.getStyleClass().add("stats-label");
        nodesExplored = new Label("0");
        nodesExplored.getStyleClass().add("stats-value");
        nodesBox.getChildren().addAll(nodesLabel, nodesExplored);
        
        VBox timeBox = new VBox(5);
        timeBox.setAlignment(Pos.CENTER);
        Label timeLabel = new Label("Time Elapsed:");
        timeLabel.getStyleClass().add("stats-label");
        timeElapsed = new Label("0 ms");
        timeElapsed.getStyleClass().add("stats-value");
        timeBox.getChildren().addAll(timeLabel, timeElapsed);
        
        statsBox.getChildren().addAll(nodesBox, timeBox);
        
        statusLabel = new Label("Select a puzzle file to begin!");
        statusLabel.getStyleClass().add("status-label");
        
        this.algoBox.setOnAction(e -> {
            String selected = this.algoBox.getValue();
            if ("UCS".equals(selected)) {
                heuristicBox.setDisable(true);
                heuristicBox.setValue(null);
            } else {
                heuristicBox.setDisable(false);
            }
        });
        
        loadButton.setOnAction(e -> handleLoad());
        solveButton.setOnAction(e -> handleSolve());
        
        controlPanel.getChildren().addAll(algoBox, buttonBox, statsBox, statusLabel);
        return controlPanel;
    }
    
    private VBox createAnimationControls() {
        VBox animationPanel = new VBox(15);
        animationPanel.setPadding(new Insets(15));
        animationPanel.setAlignment(Pos.CENTER);
        animationPanel.getStyleClass().add("animation-panel");
        
        Label controlLabel = new Label("Animation Controls");
        controlLabel.getStyleClass().add("section-label");
        
        HBox transportControls = new HBox(10);
        transportControls.setAlignment(Pos.CENTER);
        
        playButton = new Button();
        playButton.setGraphic(createIcon("▶", 16));
        playButton.getStyleClass().add("transport-button");
        playButton.setDisable(true);
        
        pauseButton = new Button();
        pauseButton.setGraphic(createIcon("⏸", 16));
        pauseButton.getStyleClass().add("transport-button");
        pauseButton.setDisable(true);
        
        stopButton = new Button();
        stopButton.setGraphic(createIcon("⏹", 16));
        stopButton.getStyleClass().add("transport-button");
        stopButton.setDisable(true);
        
        transportControls.getChildren().addAll(playButton, pauseButton, stopButton);
        
        VBox speedControls = new VBox(10);
        speedControls.setAlignment(Pos.CENTER);
        
        speedLabel = new Label("Animation Speed: 1.0x");
        speedLabel.getStyleClass().add("control-label");
        
        HBox speedButtons = new HBox(10);
        speedButtons.setAlignment(Pos.CENTER);
        
        slowDownButton = new Button();
        slowDownButton.setGraphic(createIcon("🐢", 16));
        slowDownButton.getStyleClass().add("speed-button");
        slowDownButton.setDisable(true);
        
        speedUpButton = new Button();
        speedUpButton.setGraphic(createIcon("🐇", 16));
        speedUpButton.getStyleClass().add("speed-button");
        speedUpButton.setDisable(true);
        
        speedButtons.getChildren().addAll(slowDownButton, speedUpButton);
        
        animationSpeedSlider = new Slider(MIN_SPEED, MAX_SPEED, DEFAULT_SPEED);
        animationSpeedSlider.setShowTickMarks(true);
        animationSpeedSlider.setShowTickLabels(true);
        animationSpeedSlider.setMajorTickUnit(0.5);
        animationSpeedSlider.setBlockIncrement(0.1);
        animationSpeedSlider.setDisable(true);
        
        speedControls.getChildren().addAll(speedLabel, speedButtons, animationSpeedSlider);
        
        playButton.setOnAction(e -> playAnimation());
        pauseButton.setOnAction(e -> pauseAnimation());
        stopButton.setOnAction(e -> stopAnimation());
        slowDownButton.setOnAction(e -> changeSpeed(-0.1));
        speedUpButton.setOnAction(e -> changeSpeed(0.1));
        
        animationSpeedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSpeed(newVal.doubleValue());
        });
        
        animationPanel.getChildren().addAll(controlLabel, transportControls, speedControls);
        return animationPanel;
    }
    
    private Label createIcon(String text, int size) {
        Label icon = new Label(text);
        icon.setStyle("-fx-font-size: " + size + "px;");
        return icon;
    }

    private void drawBoard(Board boardObj) {
    gridPane.getChildren().clear();
    carRectangles.clear();
    
    gridPane.setHgap(0);
    gridPane.setVgap(0);
    gridPane.setPadding(new Insets(2));
    
    int width = boardObj.getWidth();
    int height = boardObj.getHeight();
    char[][] gridData = boardObj.getGrid();

    StackPane boardContainer = new StackPane();
    boardContainer.getStyleClass().add("board-container");
    
    // Cell content
    for (int y = 1; y < height + 1; y++) {
        for (int x = 1; x < width + 1; x++) {
            char cellChar = gridData[y][x];
            StackPane cell = new StackPane();
            Rectangle rect = new Rectangle(40, 40);
            rect.setStroke(Color.DARKGRAY);
            rect.setStrokeWidth(1);
            rect.setArcHeight(8);
            rect.setArcWidth(8);

            Label label = new Label(String.valueOf(cellChar));
            label.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

            if (cellChar == '.') {
                rect.setFill(Color.rgb(50, 50, 50, 0.2));
                label.setText("");
            } else if (cellChar == 'P') {
                rect.setFill(Color.rgb(220, 50, 50, 0.8));
                label.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 14px;");
                carRectangles.put(cellChar, rect); // Store main car reference
            } else if (cellChar == 'K') {
                rect.setFill(Color.rgb(50, 200, 50, 0.8));
                label.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 14px;");
                carRectangles.put(cellChar, rect);
            } else if (cellChar != '.') {
                double hue = ((cellChar - 'A') * 30) % 360;
                if (Math.abs(hue - 0) < 20 || Math.abs(hue - 360) < 20) {
                    hue += 40;
                }
                Color pieceColor = Color.hsb(hue, 0.7, 0.85, 0.8);
                rect.setFill(pieceColor);
                
                if (!carRectangles.containsKey(cellChar)) {
                    carRectangles.put(cellChar, rect);
                }
            }

            cell.getChildren().addAll(rect, label);
            cell.getStyleClass().add("board-cell");
            
            rect.setEffect(new javafx.scene.effect.DropShadow(4, 1, 1, Color.rgb(0, 0, 0, 0.3)));
            
            cell.setUserData(String.valueOf(cellChar));
            
            gridPane.add(cell, x, y);
        }
    }

    int doorX = boardObj.getDoorX();
    int doorY = boardObj.getDoorY();
    
    if (doorX >= 0 && doorX <= width && doorY >= 0 && doorY <= height) {
        Rectangle doorMarker = new Rectangle(12, 12);
        doorMarker.setFill(Color.LIGHTGREEN);
        doorMarker.setStroke(Color.GREEN);
        doorMarker.setStrokeWidth(2);
        doorMarker.setArcHeight(6);
        doorMarker.setArcWidth(6);
        StackPane doorCell = new StackPane(doorMarker);
        doorCell.getStyleClass().add("door-marker");
        
        if (doorX == width) { 
            gridPane.add(doorCell, doorX, doorY + 1);
        } else if (doorY == height) {
            gridPane.add(doorCell, doorX + 1, doorY);
        }
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
                drawBoard(board);
                statusLabel.setText("Puzzle loaded successfully from " + selectedFile.getName());
                solveButton.setDisable(false);
                
                // Reset animation controls
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
                        
                        // Enable animation controls
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
    
    if (timeline != null) {
        timeline.stop();
    }
    
    if (currentMoveIndex >= moves.size()) {
        stopAnimation();
        return;
    }
    
    Board animBoard = board.copy();
    
    for (int i = 0; i < currentMoveIndex; i++) {
        moves.get(i).applyMove(animBoard, moves.get(i));
    }
    
    drawBoard(animBoard);
    
    playButton.setDisable(true);
    pauseButton.setDisable(false);
    
    double speed = animationSpeedSlider.getValue();
    double baseDuration = 500 / speed; // ms
    
    SequentialTransition sequence = new SequentialTransition();
    
    for (int i = currentMoveIndex; i < moves.size(); i++) {
        final int moveIndex = i;
        Move move = moves.get(moveIndex);
        
        Rectangle carRect = carRectangles.get(move.getCarId());
        if (carRect == null) continue;
        
        ParallelTransition moveAnimation = createMoveAnimation(move, carRect, baseDuration);
        
        moveAnimation.setOnFinished(e -> {
            move.applyMove(animBoard, move);
            drawBoard(animBoard);
            
            currentMoveIndex = moveIndex + 1;
            moveCountLabel.setText("Move: " + currentMoveIndex + " / " + moves.size());
            
            if (moveIndex == moves.size() - 1) {
                addCelebrationEffect(animBoard);
                playButton.setDisable(true);
                pauseButton.setDisable(true);
                statusLabel.setText("Animation complete - Solution required " + moves.size() + " moves!");
            }
        });
        
        sequence.getChildren().add(moveAnimation);
    }
    
    timeline = new Timeline(new KeyFrame(Duration.millis(1), e -> sequence.play()));
    timeline.play();
}

private ParallelTransition createMoveAnimation(Move move, Rectangle carRect, double duration) {
    ParallelTransition animations = new ParallelTransition();
    
    Color originalColor = (Color) carRect.getFill();
    
    ScaleTransition scaleUp = new ScaleTransition(Duration.millis(duration * 0.2), carRect);
    scaleUp.setToX(1.15);
    scaleUp.setToY(1.15);
    
    Timeline colorCycle = new Timeline();
    colorCycle.setCycleCount(1);
    
    DoubleProperty colorShift = new SimpleDoubleProperty(0);
    
    colorShift.addListener((obs, oldVal, newVal) -> {
        double progress = newVal.doubleValue();
        
        double baseHue = originalColor.getHue();
        double shiftedHue = (baseHue + progress * 360) % 360;
        
        Color cycledColor = Color.hsb(
            shiftedHue,
            Math.min(1.0, originalColor.getSaturation() * 1.5), 
            Math.min(1.0, originalColor.getBrightness() * 1.2),
            originalColor.getOpacity()
        );
        
        carRect.setFill(cycledColor);
    });
    
    colorCycle.getKeyFrames().addAll(
        new KeyFrame(Duration.ZERO, new KeyValue(colorShift, 0.0)),
        new KeyFrame(Duration.millis(duration * 0.1), new KeyValue(colorShift, 0.1)),
        new KeyFrame(Duration.millis(duration * 0.2), new KeyValue(colorShift, 0.2)),
        new KeyFrame(Duration.millis(duration * 0.3), new KeyValue(colorShift, 0.3)),
        new KeyFrame(Duration.millis(duration * 0.4), new KeyValue(colorShift, 0.4)),
        new KeyFrame(Duration.millis(duration * 0.5), new KeyValue(colorShift, 0.5)),
        new KeyFrame(Duration.millis(duration * 0.6), new KeyValue(colorShift, 0.6)),
        new KeyFrame(Duration.millis(duration * 0.7), new KeyValue(colorShift, 0.7)),
        new KeyFrame(Duration.millis(duration * 0.8), new KeyValue(colorShift, 0.8))
    );
    
    Bloom bloom = new Bloom(0.4);
    ColorAdjust colorAdjust = new ColorAdjust();
    colorAdjust.setBrightness(0.2);
    colorAdjust.setContrast(0.2);
    colorAdjust.setSaturation(0.5);
    bloom.setInput(colorAdjust);
    
    carRect.setEffect(bloom);
    
    ScaleTransition scaleDown = new ScaleTransition(Duration.millis(duration * 0.2), carRect);
    scaleDown.setDelay(Duration.millis(duration * 0.8));
    scaleDown.setToX(1.0);
    scaleDown.setToY(1.0);
    
    Timeline resetAppearance = new Timeline(
        new KeyFrame(Duration.millis(duration), e -> {
            carRect.setFill(originalColor);
            carRect.setEffect(new DropShadow(4, 1, 1, Color.rgb(0, 0, 0, 0.3)));
        })
    );
    
    animations.getChildren().addAll(
        scaleUp,
        colorCycle,
        scaleDown,
        resetAppearance
    );
    
    return animations;
}

private void addCelebrationEffect(Board animBoard) {
    Rectangle mainCar = carRectangles.get('P');
    if (mainCar == null) return;
    
    Color originalColor = (Color) mainCar.getFill();
    
    ParallelTransition celebration = new ParallelTransition();
    
    Timeline rainbow = new Timeline();
    rainbow.setCycleCount(6);
    
    DoubleProperty hueShift = new SimpleDoubleProperty(0);
    
    hueShift.addListener((obs, oldVal, newVal) -> {
        double hue = (newVal.doubleValue() * 360) % 360;
        Color newColor = Color.hsb(hue, 0.9, 0.9);
        mainCar.setFill(newColor);
    });
    
    rainbow.getKeyFrames().addAll(
        new KeyFrame(Duration.ZERO, new KeyValue(hueShift, 0.0)),
        new KeyFrame(Duration.millis(500), new KeyValue(hueShift, 1.0))
    );
    
    ScaleTransition pulse = new ScaleTransition(Duration.millis(250), mainCar);
    pulse.setFromX(1.0);
    pulse.setFromY(1.0);
    pulse.setToX(1.2);
    pulse.setToY(1.2);
    pulse.setCycleCount(12);
    pulse.setAutoReverse(true);
    
    Bloom bloom = new Bloom(0.7);
    DropShadow gold = new DropShadow(20, Color.GOLD);
    gold.setInput(bloom);
    mainCar.setEffect(gold);
    
    celebration.getChildren().addAll(rainbow, pulse);
    
    celebration.setOnFinished(e -> {
        mainCar.setFill(originalColor);
        mainCar.setEffect(new DropShadow(4, 1, 1, Color.rgb(0, 0, 0, 0.3)));
        mainCar.setScaleX(1.0);
        mainCar.setScaleY(1.0);
    });
    
    celebration.play();
    
    statusLabel.setText("🎉 Puzzle solved! The car has escaped! 🎉");
}
    private void pauseAnimation() {
    if (timeline != null) {
        timeline.pause();
        playButton.setDisable(false);
        pauseButton.setDisable(true);
        statusLabel.setText("Animation paused at move " + currentMoveIndex);
    }
}

private void stopAnimation() {
    if (timeline != null) {
        timeline.stop();
    }
    
    drawBoard(board);
    currentMoveIndex = 0;
    moveCountLabel.setText("Move: 0 / " + (moves != null ? moves.size() : 0));
    
    playButton.setDisable(false);
    pauseButton.setDisable(true);
    
    statusLabel.setText("Animation reset. Ready to play solution.");
    
    for (Rectangle rect : carRectangles.values()) {
        rect.setEffect(new DropShadow(4, 1, 1, Color.rgb(0, 0, 0, 0.3)));
        rect.setScaleX(1.0);
        rect.setScaleY(1.0);
    }
}
    
    private void changeSpeed(double delta) {
        double currentSpeed = animationSpeedSlider.getValue();
        double newSpeed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, currentSpeed + delta));
        animationSpeedSlider.setValue(newSpeed);
        updateSpeed(newSpeed);
    }
    
    private void updateSpeed(double speed) {
        speedLabel.setText(String.format("Animation Speed: %.1fx", speed));
        
        if (timeline != null && timeline.getStatus() == Animation.Status.RUNNING) {
            int currentMove = currentMoveIndex;
            stopAnimation();
            currentMoveIndex = currentMove;
            playAnimation();
        }
    }
    
    private void resetAnimationControls() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
        
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
}