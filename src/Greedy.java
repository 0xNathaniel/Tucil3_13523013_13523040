import java.util.*;

public class Greedy {
    private Board initialBoard;
    private Set<String> visitedStates;
    private int nodesExplored;
    
    public Greedy(Board board) {
        this.initialBoard = board;
        this.visitedStates = new HashSet<>();
        this.nodesExplored = 0;
    }
    
    private int calculateHeuristic(Board board) {
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
        
        return distance + blockingCars.size() * 2;
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
            
            return distance + blockingCars * 2;
        }
    }
    
    private List<Move> getPossibleMoves(Board board) {
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
    
    private String getBoardStateString(Board board) {
        StringBuilder sb = new StringBuilder();
        for (Car car : board.getCars()) {
            sb.append(car.getId()).append(car.getX()).append(car.getY());
        }
        return sb.toString();
    }
    
    public List<Move> solve() {
        PriorityQueue<State> queue = new PriorityQueue<>();
        
        List<Move> initialMoves = new ArrayList<>();
        int initialHValue = calculateHeuristic(initialBoard);
        queue.add(new State(initialBoard.copy(), initialMoves, initialHValue));
        
        
        while (!queue.isEmpty()) {
            State statenow = queue.poll();          
            Board currentBoard = statenow.getBoard();
            List<Move> currentMoves = statenow.getMoves();

            nodesExplored++;
            if (currentBoard.isSolved()) {
                return currentMoves;
            }
            String stateString = getBoardStateString(currentBoard);
            if (visitedStates.contains(stateString)) { //klo udah pernah dikunjungi
                continue;
            } else {
                visitedStates.add(stateString); //tambahin ke visited
            }
            for (Move move : getPossibleMoves(currentBoard)) { //iterasi semua kemungkinan gerakan
                Board nextBoard = currentBoard.copy(); //buat copy, biar bisa itung heuristic
                move.applyMove(nextBoard, move);
                
                String nextStateString = getBoardStateString(nextBoard);
                if (!visitedStates.contains(nextStateString)) {
                    List<Move> nextMoves = new ArrayList<>(currentMoves);
                    nextMoves.add(move);
                    
                    int hValue = calculateHeuristic(nextBoard);
                    queue.add(new State(nextBoard, nextMoves, hValue));
                }
            }
        }
        
        return null;
    }
     public void displaySolution(List<Move> solution) {
        if (solution == null) {
            System.out.println("No solution found.");
            return;
        }
        
        Board board = initialBoard.copy();
        System.out.println("Papan Awal");
        board.printPlayableArea();
        
        for (int i = 0; i < solution.size(); i++) {
            Move move = solution.get(i);
            move.applyMove(board, move);
            
            System.out.println("Gerakan " + (i + 1) + ": " + move);
            board.printPlayableArea();
        }
    }


    public int getNodesExplored() {
        return nodesExplored;
    }
}