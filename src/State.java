import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class State implements Comparable<State> { //kelas buat nampung state board skrg
    public Board board;
    public List<Move> moves;
    public int gValue; //cost so far (moves made)
    public int hValue; //heuristic value
    public int fValue; //(g+h for A*)
    
    public State(Board board, List<Move> moves, int hValue) { //buat greedy
        this.board = board;
        this.moves = new ArrayList<>(moves);
        this.gValue = moves.size();
        this.hValue = hValue;
        this.fValue = gValue + hValue;
    }
    
    public State(Board board, List<Move> moves, int gValue, int hValue) { // buat A*
        this.board = board;
        this.moves = new ArrayList<>(moves);
        this.gValue = gValue;
        this.hValue = hValue;
        this.fValue = gValue + hValue;
    }
    
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