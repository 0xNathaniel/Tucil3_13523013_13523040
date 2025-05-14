import java.io.File;

public class TestRushHour {
    public static void main(String[] args) {
        testLoadFromFile();
        testBoardAndCarMovement();
        testGameWinCondition();
    }
    
    private static void testLoadFromFile() {
        System.out.println("=== Testing File Loading ===");
        try {
            File testFile = new File("../test/test_puzzle.txt");
            
            if (!testFile.exists()) {
                System.out.println("Test file not found. Creating a sample test file...");
                createSampleTestFile(testFile);
            }
            
            Board board = Load.loadGame(testFile);
            
            if (board != null) {
                System.out.println("File loaded successfully!");
                System.out.println("Board dimensions: " + board.getWidth() + "x" + board.getHeight());
                System.out.println("Number of cars: " + board.getCars().length);
                System.out.println("Door location: (" + board.getDoorX() + ", " + board.getDoorY() + ")");
                
                System.out.println("\nBoard configuration:");
                board.printBoard();
                System.out.println();
                
                System.out.println("Playable area:");
                board.printPlayableArea();
                System.out.println();
                
                System.out.println("Car details:");
                for (Car car : board.getCars()) {
                    System.out.println("Car " + car.getId() + ": pos=(" + car.getX() + "," + car.getY() + 
                                      "), length=" + car.getLength() + 
                                      ", orientation=" + (car.isHorizontal() ? "horizontal" : "vertical") +
                                      ", isPrimary=" + car.isPrimary());
                    
                    System.out.print("  Positions: ");
                    int[][] positions = car.posisiCar();
                    for (int[] pos : positions) {
                        System.out.print("(" + pos[0] + "," + pos[1] + ") ");
                    }
                    System.out.println();
                }
                
                Car primaryCar = board.getCarById('P');
                if (primaryCar != null) {
                    System.out.println("\nPrimary car found: " + primaryCar.getId() + 
                                      " at position (" + primaryCar.getX() + "," + primaryCar.getY() + ")");
                }
            } else {
                System.out.println("Failed to load board from file.");
            }
        } catch (Exception e) {
            System.out.println("Error during file loading test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testBoardAndCarMovement() {
        System.out.println("\n=== Testing Car Movement ===");
        try {
            Board board = new Board(6, 6);
            board.setDoor(6, 2);
            
            Car primaryCar = new Car('P', 3, 2, 2, true, true);
            Car verticalCar1 = new Car('A', 2, 0, 3, false, false);
            Car horizontalCar1 = new Car('B', 0, 1, 2, true, false);
            
            board.setCars(new Car[]{primaryCar, verticalCar1, horizontalCar1});
            
            System.out.println("Initial board:");
            board.printBoard();
            System.out.println();
            
            System.out.println("Primary car can move left: " + primaryCar.canMoveLeft(board));
            System.out.println("Primary car can move right: " + primaryCar.canMoveRight(board));
            
            if (primaryCar.canMoveRight(board)) {
                primaryCar.moveRight();
                board.updateGrid();
                System.out.println("\nAfter moving primary car right:");
                board.printBoard();
            }
            
            System.out.println("\nVertical car can move up: " + verticalCar1.canMoveUp(board));
            System.out.println("Vertical car can move down: " + verticalCar1.canMoveDown(board));
            
            if (verticalCar1.canMoveDown(board)) {
                verticalCar1.moveDown();
                board.updateGrid();
                System.out.println("\nAfter moving vertical car down:");
                board.printBoard();
            }
            
            System.out.println("\nHorizontal car can move up: " + horizontalCar1.canMoveUp(board) + " (should be false)");
            System.out.println("Horizontal car can move down: " + horizontalCar1.canMoveDown(board) + " (should be false)");
            
            System.out.println("Vertical car can move left: " + verticalCar1.canMoveLeft(board) + " (should be false)");
            System.out.println("Vertical car can move right: " + verticalCar1.canMoveRight(board) + " (should be false)");
        } catch (Exception e) {
            System.out.println("Error during car movement test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testGameWinCondition() {
        System.out.println("\n=== Testing Win Condition ===");
        try {
            Board board = new Board(6, 6);
            board.setDoor(6, 2);
            
            Car primaryCar = new Car('P', 4, 2, 2, true, true);
            
            board.setCars(new Car[]{primaryCar});
            
            System.out.println("Board before win:");
            board.printBoard();
            System.out.println("Is solved? " + board.isSolved());
            
            if (primaryCar.canMoveRight(board)) {
                primaryCar.moveRight();
                board.updateGrid();
                System.out.println("\nAfter moving primary car:");
                board.printBoard();
                System.out.println("Is solved? " + board.isSolved() + " (should be true)");
            } else {
                System.out.println("Primary car can't move right - test failed");
            }
            
            System.out.println("\nTesting vertical primary car:");
            board = new Board(6, 6);
            board.setDoor(2, 6); 
            
            primaryCar = new Car('P', 2, 4, 2, false, true);
            
            board.setCars(new Car[]{primaryCar});
            
            System.out.println("Board before win:");
            board.printBoard();
            System.out.println("Is solved? " + board.isSolved());
            
            // Move to winning position
            if (primaryCar.canMoveDown(board)) {
                primaryCar.moveDown();
                board.updateGrid();
                System.out.println("\nAfter moving primary car down:");
                board.printBoard();
                System.out.println("Is solved? " + board.isSolved() + " (should be true)");
            } else {
                System.out.println("Primary car can't move down - test failed");
            }
        } catch (Exception e) {
            System.out.println("Error during win condition test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createSampleTestFile(File file) {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            
            java.io.PrintWriter writer = new java.io.PrintWriter(file);
            writer.println("6 6");
            writer.println("5");  
            writer.println("AAB..F");
            writer.println("..BCDF");
            writer.println("GPPCDFK");
            writer.println("GH.III");
            writer.println("GHJ...");
            writer.println("LLJMM.");
            writer.close();
            
            System.out.println("Sample test file created at: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Failed to create sample test file: " + e.getMessage());
        }
    }
}

//di directory src aja
//javac TestRushHour.java Board.java Car.java Load.java
//java TestRushHour.java Board.java Car.java Load.java











