import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;

public class TestMain {
    public static void main(String[] args) {
        System.out.println("=== RUSH HOUR PUZZLE SOLVER ===");
        Scanner scanner = new Scanner(System.in);
        Board board = null;
        
        while (true) {
            System.out.println("\nMAIN MENU:");
            System.out.println("1. Load puzzle file");
            System.out.println("2. Solve with UCS (Uniform Cost Search)");
            System.out.println("3. Solve with Greedy Best-First Search");
            System.out.println("4. Solve with A* Search");
            System.out.println("5. Compare all algorithms");
            System.out.println("0. Exit");
            
            System.out.print("\nEnter choice: ");
            int choice;
            
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } catch (Exception e) {
                scanner.nextLine(); // clear bad input
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }
            
            if (choice == 0) {
                System.out.println("Exiting. Goodbye!");
                break;
            }
            
            if (choice == 1) {
                board = loadPuzzle();
                continue;
            }
            
            if (board == null && choice > 1) {
                System.out.println("Please load a puzzle first (option 1).");
                continue;
            }
            
            switch (choice) {
                case 2:
                    solveWithUCS(board);
                    break;
                case 3:
                    solveWithGreedy(board, scanner);
                    break;
                case 4:
                    solveWithAStar(board, scanner);
                    break;
                case 5:
                    compareAlgorithms(board);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        scanner.close();
    }
    
    private static Board loadPuzzle() {
        System.out.println("\n=== LOAD PUZZLE ===");
        
        try {
            // Load puzzle file
            File puzzleFile = null;
            
            System.out.println("1. Use file chooser dialog");
            System.out.println("2. Enter file path manually");
            System.out.print("Enter choice (1/2): ");
            
            Scanner scanner = new Scanner(System.in);
            int loadChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            if (loadChoice == 1) {
                puzzleFile = Load.chooseFile();
            } else {
                System.out.print("Enter file path: ");
                String filePath = scanner.nextLine();
                puzzleFile = new File(filePath);
            }
            
            if (puzzleFile == null || !puzzleFile.exists()) {
                System.out.println("File not found or not selected.");
                return null;
            }
            
            Board board = Load.loadGame(puzzleFile);
            System.out.println("\nPuzzle loaded successfully!");
            
            // Display the initial board
            System.out.println("\nInitial Board State:");
            board.printBoard();
            
            return board;
            
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input format - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    private static void solveWithUCS(Board board) {
        System.out.println("\n=== SOLVING WITH UCS ===");
        
        try {
            long startTime = System.currentTimeMillis();
            UCS ucs = new UCS(board);
            List<Move> solution = ucs.solve();
            long endTime = System.currentTimeMillis();
            
            displaySolutionStats(solution, ucs.getNodesExplored(), (endTime - startTime));
            
            // Ask if user wants to see the solution steps
            if (solution != null && !solution.isEmpty()) {
                System.out.print("Display solution steps? (y/n): ");
                Scanner scanner = new Scanner(System.in);
                String response = scanner.nextLine().trim().toLowerCase();
                
                if (response.startsWith("y")) {
                    ucs.displaySolution(solution);
                }
            }
        } catch (Exception e) {
            System.out.println("Error during UCS: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void solveWithGreedy(Board board, Scanner scanner) {
        System.out.println("\n=== SOLVING WITH GREEDY BEST-FIRST SEARCH ===");
        
        try {
            System.out.println("Choose heuristic:");
            System.out.println("1. Blocking Cars Heuristic");
            System.out.println("2. Other Heuristic");
            System.out.print("Enter choice (1/2): ");
            
            int hChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            String heuristic = (hChoice == 1) ? "blockingCars" : "otherHeuristic";
            
            long startTime = System.currentTimeMillis();
            Greedy greedy = new Greedy(board);
            List<Move> solution = greedy.solve(heuristic);
            long endTime = System.currentTimeMillis();
            
            displaySolutionStats(solution, greedy.getNodesExplored(), (endTime - startTime));
            
            // Ask if user wants to see the solution steps
            if (solution != null && !solution.isEmpty()) {
                System.out.print("Display solution steps? (y/n): ");
                String response = scanner.nextLine().trim().toLowerCase();
                
                if (response.startsWith("y")) {
                    greedy.displaySolution(solution);
                }
            }
        } catch (Exception e) {
            System.out.println("Error during Greedy search: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void solveWithAStar(Board board, Scanner scanner) {
        System.out.println("\n=== SOLVING WITH A* SEARCH ===");
        
        try {
            System.out.println("Choose heuristic:");
            System.out.println("1. Blocking Cars Heuristic");
            System.out.println("2. Other Heuristic");
            System.out.print("Enter choice (1/2): ");
            
            int hChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            String heuristic = (hChoice == 1) ? "blockingCars" : "otherHeuristic";
            
            long startTime = System.currentTimeMillis();
            AStar astar = new AStar(board);
            List<Move> solution = astar.solve(heuristic);
            long endTime = System.currentTimeMillis();
            
            displaySolutionStats(solution, astar.getNodesExplored(), (endTime - startTime));
            
            // Ask if user wants to see the solution steps
            if (solution != null && !solution.isEmpty()) {
                System.out.print("Display solution steps? (y/n): ");
                String response = scanner.nextLine().trim().toLowerCase();
                
                if (response.startsWith("y")) {
                    astar.displaySolution(solution);
                }
            }
        } catch (Exception e) {
            System.out.println("Error during A* search: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void compareAlgorithms(Board board) {
        System.out.println("\n=== ALGORITHM COMPARISON ===");
        
        try {
            // UCS
            System.out.println("Running UCS...");
            long startTime = System.currentTimeMillis();
            UCS ucs = new UCS(board);
            List<Move> ucsSolution = ucs.solve();
            long ucsTime = System.currentTimeMillis() - startTime;
            int ucsNodes = ucs.getNodesExplored();
            int ucsMoves = (ucsSolution != null) ? ucsSolution.size() : 0;
            
            // Greedy
            System.out.println("Running Greedy Best-First Search...");
            startTime = System.currentTimeMillis();
            Greedy greedy = new Greedy(board);
            List<Move> greedySolution = greedy.solve("blockingCars");
            long greedyTime = System.currentTimeMillis() - startTime;
            int greedyNodes = greedy.getNodesExplored();
            int greedyMoves = (greedySolution != null) ? greedySolution.size() : 0;
            
            // A*
            System.out.println("Running A* Search...");
            startTime = System.currentTimeMillis();
            AStar astar = new AStar(board);
            List<Move> astarSolution = astar.solve("blockingCars");
            long astarTime = System.currentTimeMillis() - startTime;
            int astarNodes = astar.getNodesExplored();
            int astarMoves = (astarSolution != null) ? astarSolution.size() : 0;
            
            // Print comparison results
            System.out.println("\n=== RESULTS ===");
            System.out.println("Algorithm       | Time (ms) | Nodes Explored | Solution Length");
            System.out.println("----------------|-----------|----------------|----------------");
            System.out.printf("%-15s | %-9d | %-14d | %-15d\n", 
                             "UCS", ucsTime, ucsNodes, ucsMoves);
            System.out.printf("%-15s | %-9d | %-14d | %-15d\n", 
                             "Greedy", greedyTime, greedyNodes, greedyMoves);
            System.out.printf("%-15s | %-9d | %-14d | %-15d\n", 
                             "A*", astarTime, astarNodes, astarMoves);
            
        } catch (Exception e) {
            System.out.println("Error during algorithm comparison: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void displaySolutionStats(List<Move> solution, int nodesExplored, long timeMs) {
        System.out.println("\n=== SOLUTION STATISTICS ===");
        System.out.println("Time taken: " + timeMs + " ms");
        System.out.println("Nodes explored: " + nodesExplored);
        
        if (solution == null || solution.isEmpty()) {
            System.out.println("Status: No solution found!");
        } else {
            System.out.println("Status: Solution found!");
            System.out.println("Solution length: " + solution.size() + " moves");
        }
    }
}