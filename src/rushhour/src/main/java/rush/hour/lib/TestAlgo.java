package rush.hour.lib;
import java.io.File;
import java.util.List;

public class TestAlgo {
    public static void main(String[] args) {
        // testGreedyWithExamplePuzzle();
        testGreedyWithLoadedPuzzle();
        // testGreedyPerformance();
    }
    
    // private static void testGreedyWithExamplePuzzle() {
    //     System.out.println("========== TESTING GREEDY WITH EXAMPLE PUZZLE ==========");
    //     try {
    //         // Create a simple test board
    //         Board board = new Board(6, 6);
    //         board.setDoor(5, 2); // Door at right edge
            
    //         Car primaryCar = new Car('P', 2, 2, 2, true, true); // Primary car at middle
    //         Car blockingCar1 = new Car('A', 4, 2, 2, false, false); // Vertical car blocking path
    //         Car blockingCar2 = new Car('B', 0, 0, 3, false, false);
    //         Car blockingCar3 = new Car('C', 0, 3, 2, true, false);
            
    //         board.setCars(new Car[]{primaryCar, blockingCar1, blockingCar2, blockingCar3});
    //         board.updateGrid();
            
    //         System.out.println("Initial board:");
    //         board.printPlayableArea();
            
    //         Greedy greedy = new Greedy(board);
    //         List<Move> solution = greedy.solve();
            
    //         if (solution != null) {
    //             System.out.println("\nSolution found with " + solution.size() + " moves!");
    //             greedy.displaySolution(solution);
    //         } else {
    //             System.out.println("\nNo solution found.");
    //         }
    //     } catch (Exception e) {
    //         System.out.println("Error: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    // }
    
    private static void testGreedyWithLoadedPuzzle() {
        System.out.println("\n========== TESTING GREEDY WITH LOADED PUZZLE ==========");
        try {
            File testFile = new File("../test/test_puzzle.txt");
            
            // if (!testFile.exists()) {
            //     System.out.println("Creating sample test file...");
            //     createSampleTestFile(testFile);
            // }
            
            Board board = Load.loadGame(testFile);
            if (board != null) {
                System.out.println("Puzzle loaded successfully!");
                System.out.println("Board dimensions: " + board.getWidth() + "x" + board.getHeight());
                System.out.println("Initial board:");
                board.printPlayableArea();
                
                //Greedy greedy = new Greedy(board);
                // List<Move> solution = greedy.solve("blockingCars");
                //List<Move> solution = greedy.solve("exitDistance");
                //UCS ucs = new UCS(board);
                //List<Move> solution = ucs.solve();
                AStar aStar = new AStar(board);
                //List<Move> solution = aStar.solve("blockingCars");
                List<Move> solution = aStar.solve("exitDistance");

                if (solution != null) {
                    System.out.println("\nSolution found with " + solution.size() + " moves!");
                    //greedy.displaySolution(solution);
                    //ucs.displaySolution(solution);
                    aStar.displaySolution(solution);
                } else {
                    System.out.println("\nNo solution found.");
                }
            } else {
                System.out.println("Failed to load puzzle from file.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // private static void testGreedyPerformance() {
    //     System.out.println("\n========== TESTING GREEDY PERFORMANCE ==========");
    //     try {
    //         // Create a series of puzzles with increasing complexity
    //         for (int complexity = 1; complexity <= 3; complexity++) {
    //             System.out.println("\nTesting puzzle with complexity level " + complexity);
                
    //             Board board = createComplexityTestPuzzle(complexity);
    //             System.out.println("Board dimensions: " + board.getWidth() + "x" + board.getHeight());
    //             System.out.println("Initial board:");
    //             board.printPlayableArea();
                
    //             long startTime = System.currentTimeMillis();
    //             Greedy greedy = new Greedy(board);
    //             List<Move> solution = greedy.solve();
    //             long endTime = System.currentTimeMillis();
                
    //             System.out.println("Time taken: " + (endTime - startTime) + " ms");
    //             System.out.println("Nodes explored: " + greedy.getNodesExplored());
                
    //             if (solution != null) {
    //                 System.out.println("Solution length: " + solution.size() + " moves");
    //             } else {
    //                 System.out.println("No solution found.");
    //             }
    //         }
    //     } catch (Exception e) {
    //         System.out.println("Error: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    // }
    
    // private static Board createComplexityTestPuzzle(int complexity) {
    //     Board board = new Board(6, 6);
    //     board.setDoor(5, 2); // Door at right edge
        
    //     // Primary car is always needed
    //     Car primaryCar = new Car('P', 1, 2, 2, true, true);
        
    //     // Add cars based on complexity
    //     Car[] cars;
    //     switch (complexity) {
    //         case 1: // Easiest - Only one blocking car
    //             cars = new Car[]{
    //                 primaryCar,
    //                 new Car('A', 3, 1, 2, false, false) // One vertical car
    //             };
    //             break;
                
    //         case 2: // Medium - Several blocking cars
    //             cars = new Car[]{
    //                 primaryCar,
    //                 new Car('A', 3, 1, 3, false, false), // Vertical car blocking primary
    //                 new Car('B', 4, 0, 2, true, false),
    //                 new Car('C', 0, 0, 3, false, false),
    //                 new Car('D', 0, 3, 2, true, false)
    //             };
    //             break;
                
    //         case 3: // Hard - Many cars creating complex blocking patterns
    //             cars = new Car[]{
    //                 primaryCar,
    //                 new Car('A', 3, 1, 3, false, false),
    //                 new Car('B', 4, 0, 2, true, false),
    //                 new Car('C', 0, 0, 3, false, false),
    //                 new Car('D', 0, 3, 2, true, false),
    //                 new Car('E', 4, 4, 2, true, false),
    //                 new Car('F', 3, 4, 2, false, false),
    //                 new Car('G', 5, 3, 2, false, false)
    //             };
    //             break;
                
    //         default:
    //             cars = new Car[]{primaryCar};
    //     }
        
    //     board.setCars(cars);
    //     board.updateGrid();
    //     return board;
    // }
    
    // private static void createSampleTestFile(File file) {
    //     try {
    //         if (!file.getParentFile().exists()) {
    //             file.getParentFile().mkdirs();
    //         }
            
    //         java.io.PrintWriter writer = new java.io.PrintWriter(file);
    //         writer.println("6 6");
    //         writer.println("5");
    //         writer.println("AAB..F");
    //         writer.println("..BCDF");
    //         writer.println("GPPCDFK");
    //         writer.println("GH.III");
    //         writer.println("GHJ...");
    //         writer.println("LLJMM.");
    //         writer.close();
            
    //         System.out.println("Sample test file created at: " + file.getAbsolutePath());
    //     } catch (Exception e) {
    //         System.out.println("Failed to create sample test file: " + e.getMessage());
    //     }
    // }
}