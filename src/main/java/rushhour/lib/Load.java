package rushhour.lib; 
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Load {
    public static Board loadGame(File file) throws InputMismatchException, FileNotFoundException {
    try (Scanner scanner = new Scanner(file)) {
        if (!scanner.hasNextInt()) {
            System.out.println("Input harus integer untuk width (baris pertama)");
            return null;
        }
        
        int height = scanner.nextInt();
        
        if (!scanner.hasNextInt()) {
            System.out.println("Input harus integer untuk height (baris kedua)");
            return null;
        }

        int width = scanner.nextInt();
        if (width <= 0 || height <= 0) {
            System.out.println("Input harus lebih dari 0 untuk width dan height");
            return null;
        }
        scanner.nextLine();
        
        Board board = new Board(width, height);
        
        if (!scanner.hasNextInt()) {
            System.out.println("Input harus integer untuk jumlah mobil (baris ketiga)");
            return null;
        }

        int declaredPieceCount = scanner.nextInt();
        if (declaredPieceCount <= 0) {
            System.out.println("Input tidak boleh negatif untuk jumlah mobil");
            return null;
        }
        scanner.nextLine();

        Map<Character, List<int[]>> carPositions = new HashMap<>();
        int doorX = -1;
        int doorY = -1;
        boolean doorFound = false;
        
        List<String> allLines = new ArrayList<>();
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println("Read line: [" + line + "]");
            allLines.add(line);
        }
        
        System.out.println("Total lines read: " + allLines.size());
        
        if (!allLines.isEmpty()) {
            String firstLine = allLines.get(0);
            if (firstLine.trim().equals("K") || firstLine.matches("\\s*K\\s*")) {
                if (doorFound) {
                    System.out.println("Error: Multiple doors (K) found - only one door is allowed");
                    return null;
                }
                
                int leadingSpaces = firstLine.length() - StringExtensions.lTrim(firstLine).length();
                doorX = leadingSpaces;
                doorY = -1;
                doorFound = true;
                System.out.println("Door found at top edge at position (" + doorX + "," + doorY + ")");
                allLines.remove(0);
            }
        }
        
        List<String> gridLines = new ArrayList<>();
        for (int i = 0; i < Math.min(height, allLines.size()); i++) {
            String line = allLines.get(i);
            if (!line.trim().isEmpty()) {
                gridLines.add(line);
            }
        }

        System.out.println("Expected height: " + height);
        System.out.println("Grid lines count: " + gridLines.size());
        
        for (int i = 0; i < gridLines.size(); i++) {
            System.out.println("Grid line " + i + ": [" + gridLines.get(i) + "]");
        }

        if (gridLines.size() < height) {
            System.out.println("Error: Not enough rows in the puzzle. Expected " + height + 
                              " rows but found only " + gridLines.size());
            return null;
        }
        
        for (int y = 0; y < gridLines.size(); y++) {
            String line = gridLines.get(y);
            
            if (line.length() > 0 && line.charAt(0) == 'K') {
                if (doorFound) {
                    System.out.println("Error: Multiple doors (K) found - only one door is allowed");
                    return null;
                }
                
                doorX = -1; 
                doorY = y;
                doorFound = true;
                System.out.println("Door found at left edge at position (" + doorX + "," + doorY + ")");
                line = line.substring(1);
            }
            
            if (line.length() > width && line.charAt(width) == 'K') {
                if (doorFound) {
                    System.out.println("Error: Multiple doors (K) found - only one door is allowed");
                    return null;
                }
                
                doorX = width;
                doorY = y;
                doorFound = true;
                System.out.println("Door found at right edge at position (" + doorX + "," + doorY + ")");
            }
            
            for (int x = 0; x < Math.min(line.length(), width); x++) {
                char cell = line.charAt(x);
                
                if (cell == '.') continue;
                
                if (cell == 'K') {
                    if (doorFound) {
                        System.out.println("Error: Multiple doors (K) found - only one door is allowed");
                        return null;
                    }
                    
                    doorX = x;
                    doorY = y;
                    doorFound = true;
                    System.out.println("Door found inside grid at (" + doorX + "," + doorY + ")");
                } else {
                    if (!Character.isLetter(cell)) {
                        System.out.println("Error: Invalid car ID '" + cell + "'. Car IDs must be letters A-Z or a-z.");
                        return null;
                    }
                    
                    carPositions.putIfAbsent(cell, new ArrayList<>());
                    carPositions.get(cell).add(new int[]{x, y});
                }
            }
        }
        
        int remainingLinesStart = gridLines.size();
        if (remainingLinesStart < allLines.size()) {
            String bottomLine = allLines.get(remainingLinesStart);
            if (bottomLine.trim().equals("K") || bottomLine.matches("\\s*K\\s*")) {
                if (doorFound) {
                    System.out.println("Error: Multiple doors (K) found - only one door is allowed");
                    return null;
                }
                
                int leadingSpaces = bottomLine.length() - StringExtensions.lTrim(bottomLine).length();
                doorX = leadingSpaces;
                doorY = height;
                doorFound = true;
                System.out.println("Door found at bottom edge at position (" + doorX + "," + doorY + ")");
            }
        }
        
        if (!doorFound) {
            System.out.println("Error: Tidak ada pintu keluar (K) ditemukan");
            return null;
        }
        
        board.setDoor(doorX, doorY);
        
        if (!carPositions.containsKey('P')) {
            System.out.println("Error: Tidak ada mobil utama (P) ditemukan");
            return null;
        }
        
        for (Map.Entry<Character, List<int[]>> entry : carPositions.entrySet()) {
            char id = entry.getKey();
            List<int[]> positions = entry.getValue();
            
            positions.sort((a, b) -> {
                if (a[1] != b[1]) return Integer.compare(a[1], b[1]);
                return Integer.compare(a[0], b[0]); 
            });
            
            boolean isHorizontal = true;
            boolean isVertical = true;
            
            int firstY = positions.get(0)[1];
            for (int[] pos : positions) {
                if (pos[1] != firstY) {
                    isHorizontal = false;
                    break;
                }
            }
            
            int firstX = positions.get(0)[0];
            for (int[] pos : positions) {
                if (pos[0] != firstX) {
                    isVertical = false;
                    break;
                }
            }
            
            if (!isHorizontal && !isVertical) {
                System.out.println("Error: Car '" + id + "' has an invalid shape. Cars must be straight lines (either horizontal or vertical).");
                return null;
            }
            
            if (isHorizontal) {
                for (int i = 0; i < positions.size() - 1; i++) {
                    int currentX = positions.get(i)[0];
                    int nextX = positions.get(i + 1)[0];
                    if (nextX - currentX != 1) {
                        System.out.println("Error: Car '" + id + "' has non-contiguous positions. Cars must occupy adjacent cells.");
                        return null;
                    }
                }
            }
            
            if (isVertical) {
                for (int i = 0; i < positions.size() - 1; i++) {
                    int currentY = positions.get(i)[1];
                    int nextY = positions.get(i + 1)[1];
                    if (nextY - currentY != 1) {
                        System.out.println("Error: Car '" + id + "' has non-contiguous positions. Cars must occupy adjacent cells.");
                        return null;
                    }
                }
            }
        }
        
        List<Car> cars = new ArrayList<>();
        for (Map.Entry<Character, List<int[]>> entry : carPositions.entrySet()) {
            char id = entry.getKey();
            List<int[]> positions = entry.getValue();
            
            boolean isPrimary = (id == 'P');
            
            int x = positions.get(0)[0];
            int y = positions.get(0)[1];
            int length = positions.size();
            
            boolean isHorizontal = true;
            int firstY = positions.get(0)[1];
            for (int[] pos : positions) {
                if (pos[1] != firstY) {
                    isHorizontal = false;
                    break;
                }
            }
            
            Car car = new Car(id, x, y, length, isHorizontal, isPrimary);
            cars.add(car);
            System.out.println("Added car " + id + " at (" + x + "," + y + "), length=" + length + 
                              ", horizontal=" + isHorizontal + ", primary=" + isPrimary);
        }
        
        int actualCarCount = cars.size() - 1;
        if (actualCarCount < declaredPieceCount) {
            System.out.println("Error: Jumlah mobil dalam file (" + actualCarCount + 
                    ") kurang dari yang dideklarasikan (" + declaredPieceCount + ")");
            return null;
        } else if (actualCarCount > declaredPieceCount) {
            System.out.println("Error: Jumlah mobil dalam file (" + actualCarCount + 
                    ") lebih dari yang dideklarasikan (" + declaredPieceCount + ")");
            return null;
        }
        
        Car primaryCar = null;
        for (Car car : cars) {
            if (car.isPrimary()) {
                primaryCar = car;
                break;
            }
        }
        
        if (primaryCar != null) {
            boolean doorReachable = false;
            
            if (primaryCar.isHorizontal()) {
                if (doorY == primaryCar.getY()) {
                    if (doorX == -1 || doorX >= primaryCar.getX() + primaryCar.getLength()) {
                        doorReachable = true;
                    }
                }
            } else {
                if (doorX == primaryCar.getX()) {
                    if ((doorY == -1) || (doorY == height) || 
                        (doorY >= primaryCar.getY() + primaryCar.getLength()) || 
                        (doorY < primaryCar.getY())) {
                        doorReachable = true;
                    }
                }
            }
            
            if (!doorReachable) {
                System.out.println("Warning: Pintu keluar tidak dapat dijangkau oleh mobil utama berdasarkan orientasinya");
                System.out.println("Door at (" + doorX + "," + doorY + "), Primary car: " + 
                                  (primaryCar.isHorizontal() ? "horizontal" : "vertical") + 
                                  " at (" + primaryCar.getX() + "," + primaryCar.getY() + 
                                  ") with length " + primaryCar.getLength());
                return null;
            }
        }
        
        board.setCars(cars.toArray(new Car[0]));
        board.updateGrid();
        
        return board;
    } catch (FileNotFoundException e) {
        throw e;
    } catch (InputMismatchException e) {
        throw e;
    } catch (Exception e) {
        throw new RuntimeException("Error loading game: " + e.getMessage(), e);
    }
}
    
    public static File chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Rush Hour Puzzle File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
}

class StringExtensions {
    public static String lTrim(String str) {
        int i = 0;
        while (i < str.length() && Character.isWhitespace(str.charAt(i))) {
            i++;
        }
        return str.substring(i);
    }
}