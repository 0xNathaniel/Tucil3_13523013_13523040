package rush.hour;

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
import rush.hour.lib.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.List;

public class App extends Application {
    private Board board;
    private GridPane gridPane;
    private ComboBox<String> algoBox, heuristicBox;
    private Button loadButton, solveButton;
    private Label statusLabel;
    private List<Move> moves;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        HBox optionsBox = new HBox(10);
        optionsBox.setPadding(new Insets(10));

        loadButton = new Button("Load File");
        solveButton = new Button("Solve Puzzle");

        Label topLabel = new Label("Rush Hour Puzzle Solver");

        algoBox = new ComboBox<>();
        algoBox.getItems().addAll("UCS", "Greedy", "A*", "Fringe");
        algoBox.setPromptText("Select Algorithm");

        heuristicBox = new ComboBox<>();
        heuristicBox.getItems().addAll("blockingCars", "exitDistance");
        heuristicBox.setPromptText("Select Heuristic");

        gridPane = new GridPane();
        drawBoard(new Board(6, 6));

        statusLabel = new Label("Select a puzzle file to begin!");

        algoBox.setOnAction(e -> {
            String selected = algoBox.getValue();
            if ("UCS".equals(selected)) {
                heuristicBox.setDisable(true);
                heuristicBox.setValue(null);
            } else {
                heuristicBox.setDisable(false);
            }
        });
        loadButton.setOnAction(e -> handleLoad());
        solveButton.setOnAction(e -> handleSolve());

        optionsBox.getChildren().addAll(loadButton, algoBox, heuristicBox, solveButton);
        root.getChildren().addAll(topLabel, gridPane, statusLabel, optionsBox);

        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.setTitle("Rush Hour Puzzle Solver");
        stage.show();
    }

    private void drawBoard(Board boardObj) {
        gridPane.getChildren().clear();
        int width = boardObj.getWidth();
        int height = boardObj.getHeight();
        char[][] gridData = boardObj.getGrid();

        for (int y = 1; y < height + 1; y++) {
            for (int x = 1; x < width + 1; x++) {
                char cellChar = gridData[y][x];
                StackPane cell = new StackPane();
                Rectangle rect = new Rectangle(40, 40);
                rect.setStroke(Color.BLACK); // border

                Label label = new Label(String.valueOf(cellChar));
                label.setStyle("-fx-font-weight: bold");

                if (cellChar == '.') {
                    rect.setFill(Color.WHITE);
                } else if (cellChar == 'P') {
                    rect.setFill(Color.RED); // primary car
                } else {
                    double hue = ((cellChar - 'A') * 30) % 360;
                    if (Math.abs(hue - 0) < 20 || Math.abs(hue - 360) < 20) {
                        hue += 40; 
                    }
                    Color pieceColor = Color.hsb(hue, 0.5, 0.85);
                    rect.setFill(pieceColor);
                }

                cell.getChildren().addAll(rect, label);
                gridPane.add(cell, x, y);
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
                statusLabel.setText("Puzzle loaded successfully.");
                solveButton.setDisable(false);
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

        switch (algo) {
            case "UCS":
                moves = new UCS(board).solve();
                break;
            case "A*":
                moves = new AStar(board).solve(heuristic);
                break;
            case "Greedy":
                moves = new Greedy(board).solve(heuristic);
                break;
            case "Fringe":
                moves = new Fringe(board).solve(heuristic);
                break;
        }

        if (moves == null || moves.isEmpty()) {
            statusLabel.setText("No solution found.");
        } else {
            statusLabel.setText("Solution found! Animating...");
            animateMoves();
        }
    }

    private void animateMoves() {
        Timeline timeline = new Timeline();
        Board animBoard = board.copy();

        for (int i = 0; i < moves.size(); i++) {
            final int index = i;
            KeyFrame kf = new KeyFrame(Duration.seconds(i), e -> {
                moves.get(index).applyMove(animBoard, moves.get(index));
                drawBoard(animBoard);
            });
            timeline.getKeyFrames().add(kf);
        }

        timeline.play();
    }

}