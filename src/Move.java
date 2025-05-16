import java.util.ArrayList;
import java.util.List;

public class Move {
    private char carId;
    private String direction;
    
    public Move(char carId, String direction) {
        this.carId = carId;
        this.direction = direction;
    }
    
    public char getCarId() {
        return carId;
    }
    
    public String getDirection() {
        return direction;
    }
    
    @Override
    public String toString() {
        return carId + "-" + direction;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Move other = (Move) obj;
        return carId == other.carId && direction.equals(other.direction);
    }

    public void applyMove(Board board, Move move) {
        Car car = board.getCarById(move.carId);
        
        if (move.direction.equals("up")) {
            car.moveUp();
        } else if (move.direction.equals("down")) {
            car.moveDown();
        } else if (move.direction.equals("left")) {
            car.moveLeft();
        } else if (move.direction.equals("right")) {
            car.moveRight();
        }
        
        board.updateGrid();
    }

    public static List<Move> getPossibleMoves(Board board) {
        List<Move> possibleMoves = new ArrayList<>();
        
        for (Car car : board.getCars()) {
            if (car.canMoveUp(board)) {
                possibleMoves.add(new Move(car.getId(), "up"));
            }
            if (car.canMoveDown(board)) {
                possibleMoves.add(new Move(car.getId(), "down"));
            }
            if (car.canMoveLeft(board)) {
                possibleMoves.add(new Move(car.getId(), "left"));
            }
            if (car.canMoveRight(board)) {
                possibleMoves.add(new Move(car.getId(), "right"));
            }
        }
        
        return possibleMoves;
    }
}