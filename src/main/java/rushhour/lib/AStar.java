package rushhour.lib; 
import java.util.*;

// Lihat Class Algorithm untuk melihat class member
public class AStar extends Algorithm {
    /* Cukup memasukkan initialBoard state yang ingin di-solve
     * Gunakan solve() untuk memberikan solusi berdasarkan algo A* */
    public AStar(Board board) {
        super(board);
    }

    /* String heuristic: metode heuristic yang dipilih */
    @Override
    public List<Move> solve(String heuristic) {
        // Validasi input heuristic
        if (!heuristic.equals("blockingCars") && !heuristic.equals("exitDistance")) {
            System.out.println("Invalid heuristic.");
            return new ArrayList<Move>(); // Empty list
        }

        // Waktu mulai
        long start = System.currentTimeMillis();

        // Inisialisasi data structure
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.getFValue()));
        List<Move> initialMoves = new ArrayList<>();
        int initialHValue = State.calculateHValue(heuristic, initialBoard);
        queue.add(new State(initialBoard.copy(), initialMoves, initialHValue));
        
        // Proses A* search
        while (!queue.isEmpty()) {
            State statenow = queue.poll();          
            Board currentBoard = statenow.getBoard();
            List<Move> currentMoves = statenow.getMoves();

            nodesExplored++;
            // Goal state
            if (currentBoard.isSolved()) {
                // Waktu selesai
                long end = System.currentTimeMillis();
                timeElapsed = end - start;

                return currentMoves;
            }
            
            /* Validasi duplikat
             * Untuk menghindari cycle atau repeated states */
            String stateString = State.getBoardStateString(currentBoard);
            if (visitedStates.contains(stateString)) {
                continue;
            } else {
                visitedStates.add(stateString);
            }

            // Iterasi seluruh kemungkinan move pada state terkini
            for (Move move : Move.getPossibleMoves(currentBoard)) { 
                Board nextBoard = currentBoard.copy(); 
                move.applyMove(nextBoard, move);
                
                String nextStateString = State.getBoardStateString(nextBoard);
                if (!visitedStates.contains(nextStateString)) {
                    List<Move> nextMoves = new ArrayList<>(currentMoves);
                    nextMoves.add(move);
                    int hValue = State.calculateHValue(heuristic, nextBoard);

                    queue.add(new State(nextBoard, nextMoves, hValue));
                }
            }
        }
        // Waktu selesai
        long end = System.currentTimeMillis();
        timeElapsed = end - start;
        
        return null;
    }
}