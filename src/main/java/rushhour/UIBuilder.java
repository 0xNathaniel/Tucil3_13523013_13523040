package rushhour;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class UIBuilder {
    public static HBox createTitleArea() {
        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(15));
        titleBox.getStyleClass().add("title-box");
        
        Label titleLabel = new Label("Rush Hour Puzzle Solver");
        titleLabel.getStyleClass().add("title-label");
        
        titleBox.getChildren().add(titleLabel);
        return titleBox;
    }
    
    public static VBox createBoardArea(StackPane boardContainer, Label moveCountLabel) {
        VBox boardArea = new VBox(15);
        boardArea.setAlignment(Pos.CENTER);
        boardArea.setPadding(new Insets(10));
        boardArea.getStyleClass().add("board-area");
        
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(450, 450);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        boardContainer.setClip(clip);
        
        boardContainer.setMinSize(450, 450);
        boardContainer.setMaxSize(450, 450);
        boardContainer.setPrefSize(450, 450);
        
        boardContainer.getStyleClass().add("board-container");
        
        boardContainer.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #999; -fx-border-width: 2px; -fx-border-radius: 5px;");
        
        moveCountLabel.getStyleClass().add("status-label");
        
        boardArea.getChildren().addAll(boardContainer, moveCountLabel);
        
        return boardArea;
    }
    
    public static VBox createControlPanel(
            ComboBox<String> algoBox, 
            ComboBox<String> heuristicBox,
            Button loadButton,
            Button solveButton,
            Button saveButton,
            Label nodesExplored,
            Label timeElapsed,
            Label statusLabel,
            HBox zoomControls,
            EventHandler<ActionEvent> onAlgoChanged,
            EventHandler<ActionEvent> onLoadClicked,
            EventHandler<ActionEvent> onSolveClicked,
            EventHandler<ActionEvent> onSaveClicked) {
            
        VBox controlPanel = new VBox(10);
        controlPanel.setPadding(new Insets(15));
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.getStyleClass().add("control-panel");
        
        HBox algoHBox = new HBox(10);
        algoHBox.setAlignment(Pos.CENTER);
        
        Label algoLabel = new Label("Algorithm:");
        algoLabel.getStyleClass().add("control-label");
        
        algoBox.getItems().addAll("UCS", "Greedy", "A*", "Fringe");
        algoBox.setPromptText("Select Algorithm");
        algoBox.getStyleClass().add("combo-box");
        
        Label heuristicLabel = new Label("Heuristic:");
        heuristicLabel.getStyleClass().add("control-label");
        
        heuristicBox.getItems().addAll("blockingCars", "exitDistance");
        heuristicBox.setPromptText("Select Heuristic");
        heuristicBox.getStyleClass().add("combo-box");
        
        algoHBox.getChildren().addAll(algoLabel, algoBox, heuristicLabel, heuristicBox);
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        loadButton.getStyleClass().add("action-button");
        loadButton.setGraphic(createIcon("📂", 16));
        
        solveButton.getStyleClass().add("action-button");
        solveButton.setGraphic(createIcon("🔍", 16));
        solveButton.setDisable(true);
        
        saveButton.getStyleClass().add("action-button");
        saveButton.setGraphic(createIcon("💾", 16));
        saveButton.setDisable(true);
        
        buttonBox.getChildren().addAll(loadButton, solveButton, saveButton);
        
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        
        VBox nodesBox = new VBox(5);
        nodesBox.setAlignment(Pos.CENTER);
        Label nodesLabel = new Label("Nodes Explored:");
        nodesLabel.getStyleClass().add("stats-label");
        nodesExplored.getStyleClass().add("stats-value");
        nodesBox.getChildren().addAll(nodesLabel, nodesExplored);
        
        VBox timeBox = new VBox(5);
        timeBox.setAlignment(Pos.CENTER);
        Label timeLabel = new Label("Time Elapsed:");
        timeLabel.getStyleClass().add("stats-label");
        timeElapsed.getStyleClass().add("stats-value");
        timeBox.getChildren().addAll(timeLabel, timeElapsed);
        
        statsBox.getChildren().addAll(nodesBox, timeBox);
        
        statusLabel.getStyleClass().add("status-label");
        
        HBox controlRow = new HBox(20);
        controlRow.setAlignment(Pos.CENTER);
        controlRow.getChildren().addAll(zoomControls);
        
        algoBox.setOnAction(onAlgoChanged);
        loadButton.setOnAction(onLoadClicked);
        solveButton.setOnAction(onSolveClicked);
        saveButton.setOnAction(onSaveClicked);
        
        controlPanel.getChildren().addAll(algoHBox, buttonBox, statsBox, controlRow, statusLabel);
        return controlPanel;
    }
    
    public static VBox createAnimationControls(
            Button playButton,
            Button pauseButton, 
            Button stopButton,
            Button slowDownButton,
            Button speedUpButton,
            Slider animationSpeedSlider,
            Label speedLabel,
            EventHandler<ActionEvent> onPlayClicked,
            EventHandler<ActionEvent> onPauseClicked,
            EventHandler<ActionEvent> onStopClicked,
            EventHandler<ActionEvent> onSlowDownClicked,
            EventHandler<ActionEvent> onSpeedUpClicked,
            ChangeListener<Number> onSpeedChanged) {
            
        VBox animationPanel = new VBox(15);
        animationPanel.setPadding(new Insets(15));
        animationPanel.setAlignment(Pos.CENTER);
        animationPanel.getStyleClass().add("animation-panel");
        
        Label controlLabel = new Label("Animation Controls");
        controlLabel.getStyleClass().add("section-label");
        
        HBox transportControls = new HBox(10);
        transportControls.setAlignment(Pos.CENTER);
        
        playButton.setGraphic(createIcon("▶", 16));
        playButton.getStyleClass().add("transport-button");
        playButton.setDisable(true);
        
        pauseButton.setGraphic(createIcon("⏸", 16));
        pauseButton.getStyleClass().add("transport-button");
        pauseButton.setDisable(true);
        
        stopButton.setGraphic(createIcon("⏹", 16));
        stopButton.getStyleClass().add("transport-button");
        stopButton.setDisable(true);
        
        transportControls.getChildren().addAll(playButton, pauseButton, stopButton);
        
        VBox speedControls = new VBox(10);
        speedControls.setAlignment(Pos.CENTER);
        
        speedLabel.getStyleClass().add("control-label");
        
        HBox speedButtons = new HBox(10);
        speedButtons.setAlignment(Pos.CENTER);
        
        slowDownButton.setGraphic(createIcon("🐢", 16));
        slowDownButton.getStyleClass().add("speed-button");
        slowDownButton.setDisable(true);
        
        speedUpButton.setGraphic(createIcon("🐇", 16));
        speedUpButton.getStyleClass().add("speed-button");
        speedUpButton.setDisable(true);
        
        speedButtons.getChildren().addAll(slowDownButton, speedUpButton);
        
        animationSpeedSlider.setShowTickMarks(true);
        animationSpeedSlider.setShowTickLabels(true);
        animationSpeedSlider.setMajorTickUnit(0.5);
        animationSpeedSlider.setBlockIncrement(0.1);
        animationSpeedSlider.setDisable(true);
        
        speedControls.getChildren().addAll(speedLabel, speedButtons, animationSpeedSlider);
        
        playButton.setOnAction(onPlayClicked);
        pauseButton.setOnAction(onPauseClicked);
        stopButton.setOnAction(onStopClicked);
        slowDownButton.setOnAction(onSlowDownClicked);
        speedUpButton.setOnAction(onSpeedUpClicked);
        animationSpeedSlider.valueProperty().addListener(onSpeedChanged);
        
        animationPanel.getChildren().addAll(controlLabel, transportControls, speedControls);
        return animationPanel;
    }
    
    public static Label createIcon(String text, int size) {
        Label icon = new Label(text);
        icon.setStyle("-fx-font-size: " + size + "px;");
        return icon;
    }
}