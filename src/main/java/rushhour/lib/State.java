package rushhour.lib; 
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class State implements Comparable<State> { //kelas buat nampung state board skrg
    public Board board;
    public List<Move> moves; // History dari moves yang sudah dilakukan
    public int gValue; // Cost so far: untuk UCS dan A*
    public int hValue; // Heuristic value: untuk A* dan Greedy
    public int fValue; // Untuk A*: f(n) = g(n) + h(n)
    
    // Constructor untuk UCS (tidak butuh hValue)
    public State(Board board, List<Move> moves) {
        this.board = board;
        this.moves = new ArrayList<>(moves);
        calculateGValue();
    }

    /* Constructor untuk Greedy dan A*  
     * Perhitungan hValue dijadikan parameter karena terdapat
     * beberapa metode perhitungn hValue */
    public State(Board board, List<Move> moves, int hValue) { 
        this.board = board;
        this.moves = new ArrayList<>(moves);
        calculateGValue();
        this.hValue = hValue;
        calculateFValue();
    }
    
    // Getter
    public Board getBoard() {
        return board;
    }
    
    public List<Move> getMoves() {
        return moves;
    }
    
    public int getGValue() {
        return gValue;
    }
    
    public int getHValue() {
        return hValue;
    }
    
    public int getFValue() {
        return fValue;
    }

    /*Convert state jadi String untuk visitedStates
    * untuk menghindarkan duplicates dan cycles */
    public static String getBoardStateString(Board board) {
        StringBuilder sb = new StringBuilder();
        for (Car car : board.getCars()) {
            sb.append(car.getId()).append(car.getX()).append(car.getY());
        }
        return sb.toString();
    }

    // Calculation functions
    public void calculateGValue() {
        this.gValue = this.moves.size();
    }

    // f(n) = g(n) + h(n)
    public void calculateFValue() {
        this.fValue = getGValue() + getHValue(); 
    }

    public static int calculateHValue(String heuristic, Board board) {
        if (heuristic.equals("blockingCars")) {
            return State.calculateBlockingCarsHeuristic(board);
        } else if (heuristic.equals("exitDistance")) {
            return State.calculateExitDistanceHeuristic(board); 
        } else {
            return State.calculateBlockingCarsAndExitDistanceHeuristic(board);
        }
    }

    public static int calculateBlockingCarsHeuristic(Board board) {
        Car primaryCar = null;

        // Cari mobil utama (primary car)
        for (Car car : board.getCars()) {
            if (car.isPrimary()) {
                primaryCar = car;
                break;
            }
        }

        if (primaryCar == null) return Integer.MAX_VALUE;

        int doorX = board.getDoorX();
        int doorY = board.getDoorY();

        // Horizontal case
        if (primaryCar.isHorizontal()) {
            Set<Car> blockingCars = new HashSet<>();

            // Periksa semua sel antara ujung mobil utama sampai ke pintu keluar
            for (int x = primaryCar.getX() + primaryCar.getLength(); x < doorX; x++) {
                if (!board.isCellEmpty(x, primaryCar.getY())) {
                    Car carAtPosition = board.getCarsAt(x, primaryCar.getY());
                    if (carAtPosition != null && carAtPosition != primaryCar) {
                        blockingCars.add(carAtPosition);
                    }
                }
            }

            return blockingCars.size();
        }

        // Vertical case
        else {
            Set<Car> blockingCars = new HashSet<>();

            if (doorY > primaryCar.getY()) {
                for (int y = primaryCar.getY() + primaryCar.getLength(); y < doorY; y++) {
                    if (!board.isCellEmpty(primaryCar.getX(), y)) {
                        Car carAtPosition = board.getCarsAt(primaryCar.getX(), y);
                        if (carAtPosition != null && carAtPosition != primaryCar) {
                            blockingCars.add(carAtPosition);
                        }
                    }
                }
            } else {
                for (int y = 0; y < primaryCar.getY(); y++) {
                    if (!board.isCellEmpty(primaryCar.getX(), y)) {
                        Car carAtPosition = board.getCarsAt(primaryCar.getX(), y);
                        if (carAtPosition != null && carAtPosition != primaryCar) {
                            blockingCars.add(carAtPosition);
                        }
                    }
                }
            }

            return blockingCars.size();
        }
    }


    public static int calculateExitDistanceHeuristic(Board board) {
        Car primaryCar = null;
        for (Car car : board.getCars()) {
            if (car.isPrimary()) {
                primaryCar = car;
                break;
            }
        }
        
        if (primaryCar == null) return 9999999;
        
        int doorX = board.getDoorX();
        int doorY = board.getDoorY();
        
        int distance = 0;
        
        if (primaryCar.isHorizontal()) {
            distance = doorX - (primaryCar.getX() + primaryCar.getLength());
            return distance;
        } else {
            distance = doorY - (primaryCar.getY() + primaryCar.getLength());
            if (doorY == 0) { 
                distance = primaryCar.getY();
            }
            return distance;
        }
    }

    public static int calculateBlockingCarsAndExitDistanceHeuristic(Board board) {
        Car primaryCar = null;
        for (Car car : board.getCars()) {
            if (car.isPrimary()) {
                primaryCar = car;
                break;
            }
        }
        
        if (primaryCar == null) return 9999999;
        
        int doorX = board.getDoorX();
        int doorY = board.getDoorY();
        
        int distance = 0;
        
        if (primaryCar.isHorizontal()) {
            distance = doorX - (primaryCar.getX() + primaryCar.getLength());
            
            Set<Car> blockingCars = new HashSet<>();
            for (int x = primaryCar.getX() + primaryCar.getLength(); x < doorX; x++) {
                if (!board.isCellEmpty(x, primaryCar.getY())) {
                    Car carAtPosition = board.getCarsAt(x, primaryCar.getY());
                    if (carAtPosition != null) {
                        blockingCars.add(carAtPosition);
                    }
                }
            }
            
            return distance + blockingCars.size();
        }  else {
                distance = doorY - (primaryCar.getY() + primaryCar.getLength());
                if (doorY == 0) { 
                    distance = primaryCar.getY();
                }
                
                int blockingCars = 0;
                if (doorY > primaryCar.getY()) {
                    for (int y = primaryCar.getY() + primaryCar.getLength(); y < doorY; y++) {
                        if (!board.isCellEmpty(primaryCar.getX(), y)) {
                            blockingCars++;
                        }
                    }
                } else {
                    for (int y = 0; y < primaryCar.getY(); y++) {
                        if (!board.isCellEmpty(primaryCar.getX(), y)) {
                            blockingCars++;
                        }
                    }
                }
            
            return distance + blockingCars;
        }
    }
    
    @Override
    public int compareTo(State other) {
        return Integer.compare(this.fValue, other.fValue);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { 
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        State state = (State) o;
        return Objects.equals(board, state.board);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(board);
    }
    
    public State addMove(Move move, Board newBoard, int newHValue) {
        List<Move> newMoves = new ArrayList<>(moves);
        newMoves.add(move);
        return new State(newBoard, newMoves, newHValue);
    }
}