package rushhour;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import rushhour.lib.*;

import java.util.Map;

public class BoardRenderer {
    public static void drawBoard(Board boardObj, GridPane gridPane, Map<Character, Rectangle> carRectangles, char currentlyMovingCar) {
        gridPane.getChildren().clear();
        carRectangles.clear();
        
        gridPane.setHgap(0);
        gridPane.setVgap(0);
        gridPane.setPadding(new Insets(2));
        
        int width = boardObj.getWidth();
        int height = boardObj.getHeight();
        char[][] gridData = boardObj.getGrid();

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

                boolean isMovingCar = cellChar == currentlyMovingCar;

                if (cellChar == '.') {
                    rect.setFill(Color.rgb(50, 50, 50, 0.2));
                    label.setText("");
                } else if (cellChar == 'P') {
                    Color carColor = isMovingCar ? 
                        Color.rgb(255, 60, 60, 0.9) : 
                        Color.rgb(220, 50, 50, 0.8);
                    rect.setFill(carColor);
                    label.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 14px;");
                    carRectangles.put(cellChar, rect);
                    
                    if (isMovingCar) {
                        AnimationUtils.addHighlightEffect(rect);
                    }
                } else if (cellChar == 'K') {
                    Color carColor = isMovingCar ? 
                        Color.rgb(60, 255, 60, 0.9) : 
                        Color.rgb(50, 200, 50, 0.8);
                    rect.setFill(carColor);
                    label.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 14px;");
                    carRectangles.put(cellChar, rect);
                    
                    if (isMovingCar) {
                        AnimationUtils.addHighlightEffect(rect);
                    }
                } else if (cellChar != '.') {
                    double hue = ((cellChar - 'A') * 30) % 360;
                    if (Math.abs(hue - 0) < 20 || Math.abs(hue - 360) < 20) {
                        hue += 40;
                    }
                    
                    double saturation = isMovingCar ? 0.9 : 0.7;
                    double brightness = isMovingCar ? 0.95 : 0.85;
                    double opacity = isMovingCar ? 0.95 : 0.8;
                    
                    Color pieceColor = Color.hsb(hue, saturation, brightness, opacity);
                    rect.setFill(pieceColor);
                    
                    if (isMovingCar) {
                        AnimationUtils.addHighlightEffect(rect);
                    }
                    
                    if (!carRectangles.containsKey(cellChar)) {
                        carRectangles.put(cellChar, rect);
                    }
                }

                cell.getChildren().addAll(rect, label);
                cell.getStyleClass().add("board-cell");
                
                if (!isMovingCar) {
                    rect.setEffect(new DropShadow(4, 1, 1, Color.rgb(0, 0, 0, 0.3)));
                }
                
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
}