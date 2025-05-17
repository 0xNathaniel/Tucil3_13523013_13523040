package rushhour.lib;
import java.util.*;

// Lihat Class Algorithm untuk melihat class member
public class Fringe extends Algorithm{
    /* Cukup memasukkan initialBoard state yang ingin di-solve
     * Gunakan solve() untuk memberikan solusi berdasarkan algo Fringe */
    public Fringe(Board board) {
        super(board);
    }

    /* String heuristic: metode heuristic yang dipilih */
    @Override
    public List<Move> solve(String heuristic) {
        // Pengecekan metode heuristic yang digunakan
        if (!heuristic.equals("blockingCars") && !heuristic.equals("exitDistance")) {
            System.out.println("Invalid heuristic.");
            return new ArrayList<Move>(); // Empty list
        }

        // Waktu mulai
        long start = System.currentTimeMillis();

        /* Inisialisasi data structure
         * Salah satu karakteristik utama dari Fringe search
         * yaitu terdapat list now dan later*/
        Deque<State> now = new LinkedList<State>();
        Deque<State> later = new LinkedList<State>();

        /* Nilai initial threshold adalah nilai f(n) dari state awal
        *  dengan g(n) = 0 dan h(n) bergantung pada jenis heuristic yang dipilih*/
        int initialHValue = State.calculateHValue(heuristic, initialBoard);
        State initialState = new State(initialBoard, new ArrayList<>(), initialHValue);
        int threshold = initialState.getFValue();
        int increment = 5;

        // Inisialisasi list now
        now.add(initialState);
        
        // Proses Fringe search
        while (!now.isEmpty()) {
            while (!now.isEmpty()) {
                State currentState = now.poll();
                Board currentBoard = currentState.getBoard();
                List<Move> currentMoves = currentState.getMoves(); 

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
            now.addAll(later); // now = later
            later.clear();     // later = []
        }

        // Waktu selesai
        long end = System.currentTimeMillis();
        timeElapsed = end - start;
        return new ArrayList<>();
    }
}
