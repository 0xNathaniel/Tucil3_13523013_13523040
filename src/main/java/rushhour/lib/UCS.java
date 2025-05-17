package rushhour.lib; 
import java.util.*;

public class UCS {
     private Board initialBoard;
     private Set<String> visitedStates;
     private int nodesExplored;
     private long timeElapsed;

    /* Cukup memasukkan initialBoard state yang ingin di-solve
     * Gunakan solve() untuk memberikan solusi berdasarkan algo UCS */
    public UCS(Board board) {
        this.initialBoard = board;
        this.visitedStates = new HashSet<>();
        this.nodesExplored = 0;
        this.timeElapsed = 0;
    }

    public List<Move> solve() {
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.getGValue()));
        List<Move> initialMoves = new ArrayList<>();
        queue.add(new State(initialBoard.copy(), initialMoves));

        long start = System.currentTimeMillis();

        while (!queue.isEmpty()) {
            State stateNow = queue.poll();          
            Board currentBoard = stateNow.getBoard();
            List<Move> currentMoves = stateNow.getMoves();

            nodesExplored++;
            if (currentBoard.isSolved()) { // Goal state
                long end = System.currentTimeMillis();
                timeElapsed = end - start;
                return currentMoves;
            }

            // Hindari duplicate dengna visitedStates
            String stateString = State.getBoardStateString(currentBoard);
            if (visitedStates.contains(stateString)) {
                continue;
            } else {
                visitedStates.add(stateString);
            }

            for (Move move : Move.getPossibleMoves(currentBoard)) { //iterasi semua kemungkinan gerakan
                Board nextBoard = currentBoard.copy(); //buat copy, biar bisa itung heuristic
                move.applyMove(nextBoard, move);
                
                String nextStateString = State.getBoardStateString(nextBoard);
                if (!visitedStates.contains(nextStateString)) {
                    List<Move> nextMoves = new ArrayList<>(currentMoves);
                    nextMoves.add(move);

                    queue.add(new State(nextBoard, nextMoves));
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
