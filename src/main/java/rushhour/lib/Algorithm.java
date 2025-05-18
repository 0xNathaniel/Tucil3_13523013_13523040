package rushhour.lib; 
import java.util.*;

/* Abstract Class Algorithm
 * untuk implementasi seluruh algoritma lainnya:
 * UCS, Greedy Best First, A*, dan Fringe*/
public abstract class Algorithm {
    protected Board initialBoard;
    protected Set<String> visitedStates;
    protected int nodesExplored;
    protected long timeElapsed;

    // Constructor
    public Algorithm(Board board) {
        this.initialBoard = board;
        this.visitedStates = new HashSet<>();
        this.nodesExplored = 0;
        this.timeElapsed = 0;
    }

    // Method solve default untuk uninformed algorithm
    public List<Move> solve() {
        return solve(null);
    }
    
    // Method solve untuk informed algorithm
    public abstract List<Move> solve(String heuristic);

    // Digunakan seluruh algoritma
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

    // Getter: nodes explored
    public int getNodesExplored() {
        return nodesExplored;
    }

    // Getter: time elapsed
    public long getTimeElapsed() {
        return timeElapsed;
    }
}
