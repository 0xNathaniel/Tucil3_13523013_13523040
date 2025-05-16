<<<<<<< HEAD:src/TestLoad.java
=======
package rush.hour.lib;
import java.io.File;
import java.util.Scanner;

public class TestLoad {
    public static void main(String[] args) {
        System.out.println("=== RUSH HOUR LOAD TEST ===");
        
        try {
            // Get file path from user
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter puzzle file path: ");
            String path = scanner.nextLine().trim();
            
            File puzzleFile = new File(path);
            if (!puzzleFile.exists()) {
                System.out.println("Error: File not found at " + path);
                return;
            }
            
            // Load the puzzle
            System.out.println("\nLoading puzzle from " + puzzleFile.getPath() + "...");
            Board board = Load.loadGame(puzzleFile);
            
            // Display the loaded board details
            System.out.println("\n=== LOADED BOARD DETAILS ===");
            System.out.println("Board size: " + board.getWidth() + "x" + board.getHeight());
            System.out.println("Door position: (" + board.getDoorX() + "," + board.getDoorY() + ")");
            
            // Print cars
            System.out.println("\nCars found:");
            for (Car car : board.getCars()) {
                System.out.println("Car " + car.getId() + 
                                   ": position=(" + car.getX() + "," + car.getY() + ")" +
                                   ", length=" + car.getLength() + 
                                   ", horizontal=" + car.isHorizontal() + 
                                   ", primary=" + car.isPrimary());
            }
            
            // Print board
            System.out.println("\nBoard representation:");
            board.printBoard();
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
>>>>>>> 343154d6155a2be5674cd51090320ebb740a972c:src/rushhour/src/main/java/rush/hour/lib/TestLoad.java
