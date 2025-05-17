package rushhour.lib; 
import java.util.*;

public class Greedy {
    private Board initialBoard;
    private Set<String> visitedStates;
    private int nodesExplored;
    private long timeElapsed;
    
    public Greedy(Board board) {
        this.initialBoard = board;
        this.visitedStates = new HashSet<>();
        this.nodesExplored = 0;
        this.timeElapsed = 0;
    }

    /* String heuristic: metode heuristic yang dipilih */
    public List<Move> solve(String heuristic) {
        if (!heuristic.equals("blockingCars") && !heuristic.equals("exitDistance")) {
            System.out.println("Invalid heuristic.");
            return new ArrayList<Move>();
        }
        
        long start = System.currentTimeMillis();

        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.getHValue()));
        
        List<Move> initialMoves = new ArrayList<>();

        int initialHValue;
        if (heuristic.equals("blockingCars")) {
            initialHValue = State.calculateBlockingCarsHeuristic(initialBoard);
        } else {
            initialHValue = State.calculateExitDistanceHeuristic(initialBoard);
        }

        queue.add(new State(initialBoard.copy(), initialMoves, initialHValue));
        
        
        while (!queue.isEmpty()) {
            State statenow = queue.poll();          
            Board currentBoard = statenow.getBoard();
            List<Move> currentMoves = statenow.getMoves();

            nodesExplored++;
            if (currentBoard.isSolved()) {
                long end = System.currentTimeMillis();
                timeElapsed = end - start;
                return currentMoves;
            }
            String stateString = State.getBoardStateString(currentBoard);
            if (visitedStates.contains(stateString)) { //klo udah pernah dikunjungi
                continue;
            } else {
                visitedStates.add(stateString); //tambahin ke visited
            }
            for (Move move : Move.getPossibleMoves(currentBoard)) { //iterasi semua kemungkinan gerakan
                Board nextBoard = currentBoard.copy(); //buat copy, biar bisa itung heuristic
                move.applyMove(nextBoard, move);
                
                String nextStateString = State.getBoardStateString(nextBoard);
                if (!visitedStates.contains(nextStateString)) {
                    List<Move> nextMoves = new ArrayList<>(currentMoves);
                    nextMoves.add(move);
                    
                    int hValue;
                    if (heuristic.equals("blockingCars")) {
                        hValue = State.calculateBlockingCarsHeuristic(nextBoard);
                    } else {
                        hValue = State.calculateExitDistanceHeuristic(nextBoard); // Nanti ganti jadi heuristic ke-2
                    }

                    queue.add(new State(nextBoard, nextMoves, hValue));
                }
            }
        }
        long end = System.currentTimeMillis();
        timeElapsed = end - start;
        
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

    public long getTimeElapsed() {
        return timeElapsed;
    }
}