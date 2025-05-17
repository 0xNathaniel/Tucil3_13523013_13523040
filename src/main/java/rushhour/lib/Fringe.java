package rushhour.lib;
import java.util.*;

public class Fringe {
    private Board initialBoard;
    private Set<String> visitedStates;
    private int nodesExplored;
    private long timeElapsed;

    public Fringe(Board board) {
        this.initialBoard = board;
        this.visitedStates = new HashSet<>();
        this.nodesExplored = 0;
        this.timeElapsed = 0;
    }

    public List<Move> solve(String heuristic) {
        // Pengecekan metode heuristic yang digunakan
        if (!heuristic.equals("blockingCars") && !heuristic.equals("exitDistance")) {
            System.out.println("Invalid heuristic.");
            return new ArrayList<Move>(); // Empty list
        }

        long start = System.currentTimeMillis();

        /* Salah satu karakteristik utama dari Fringe search
         * yaitu terdapat now dan later*/
        Deque<State> now = new LinkedList<State>();
        Deque<State> later = new LinkedList<State>();

        /* Nilai initial threshold adalah nilai f(n) dari state awal
        *  dengan g(n) = 0 dan h(n) bergantung pada jenis heuristic yang dipilih*/
        int initialHValue;
        if (heuristic.equals("blockingCars")) {
            initialHValue = State.calculateBlockingCarsHeuristic(initialBoard);
        } else { // Exit Distance Heuristic
            initialHValue = State.calculateExitDistanceHeuristic(initialBoard);
        }

        State initialState = new State(initialBoard, new ArrayList<>(), initialHValue);
        int threshold = initialState.getFValue();
        int increment = 5;

        now.add(initialState);
        
        // Proses Fringe search
        while (!now.isEmpty()) {
            while (!now.isEmpty()) {
                State currentState = now.poll();
                Board currentBoard = currentState.getBoard();
                List<Move> currentMoves = currentState.getMoves(); 

                nodesExplored++;

                if (currentBoard.isSolved()) {
                    long end = System.currentTimeMillis();
                    timeElapsed = end - start;
                    return currentMoves;
                }

                String stateString = State.getBoardStateString(currentBoard);
                if (visitedStates.contains(stateString)) {
                    continue;
                } else {
                    visitedStates.add(stateString);
                }

                for (Move move : Move.getPossibleMoves(currentBoard)) {
                    Board nextBoard = currentBoard.copy();
                    move.applyMove(nextBoard, move);

                    String nextStateString = State.getBoardStateString(nextBoard);
                    if (!visitedStates.contains(nextStateString)) {
                        List<Move> nextMoves = new ArrayList<>(currentMoves);
                        nextMoves.add(move);

                        int hValue;
                        if (heuristic.equals("blockingCars")) {
                            hValue = State.calculateBlockingCarsHeuristic(nextBoard);
                        } else { // Exit Distance heuristic
                            hValue = State.calculateExitDistanceHeuristic(nextBoard); // Nanti ganti jadi heuristic ke-2
                        }
                        
                        State newState = new State(nextBoard, nextMoves, hValue);
                        if (newState.getFValue() <= threshold) {
                            now.add(newState);
                        } else {
                            later.add(newState);
                        }
                    }
                }

            }  

            // Tidak ditemukan solusi
            if (later.isEmpty()) break;
            // Increment nilai threshold baru
            threshold += increment;
            // Update now dan later
            now.addAll(later);
            later.clear();
        }

        long end = System.currentTimeMillis();
        timeElapsed = end - start;
        return new ArrayList<>();
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
