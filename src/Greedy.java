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
        int initialHValue = State.calculateBlockingCarsHeuristic(initialBoard);
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
            for (Move move : Move.getPossibleMoves(currentBoard)) { //iterasi semua kemungkinan gerakan
                Board nextBoard = currentBoard.copy(); //buat copy, biar bisa itung heuristic
                move.applyMove(nextBoard, move);
                
                String nextStateString = getBoardStateString(nextBoard);
                if (!visitedStates.contains(nextStateString)) {
                    List<Move> nextMoves = new ArrayList<>(currentMoves);
                    nextMoves.add(move);
                    
                    int hValue = State.calculateBlockingCarsHeuristic(nextBoard);
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