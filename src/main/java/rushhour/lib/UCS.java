package rushhour.lib; 
import java.util.*;

// Lihat Class Algorithm untuk melihat class member
public class UCS extends Algorithm {
    /* Cukup memasukkan initialBoard state yang ingin di-solve
     * Gunakan solve() untuk memberikan solusi berdasarkan algo UCS */
    public UCS(Board board) {
        super(board);
    }

    @Override
    public List<Move> solve(String heuristic) {
        // Inisialisasi data structure
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.getGValue()));
        List<Move> initialMoves = new ArrayList<>();
        queue.add(new State(initialBoard.copy(), initialMoves));

        // Waktu mulai
        long start = System.currentTimeMillis();

        // Proses UCS
        while (!queue.isEmpty()) {
            State stateNow = queue.poll();          
            Board currentBoard = stateNow.getBoard();
            List<Move> currentMoves = stateNow.getMoves();

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

                    queue.add(new State(nextBoard, nextMoves));
                }
            }
        }
        // Waktu selesai
        long end = System.currentTimeMillis();
        timeElapsed = end - start;
        
        return null;
    }
}   
