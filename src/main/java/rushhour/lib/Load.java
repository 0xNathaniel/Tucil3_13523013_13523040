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
                throw new InputMismatchException("Input harus integer untuk width (baris pertama)");
            }
            
            int width = scanner.nextInt();
            
            if (!scanner.hasNextInt()) {
                throw new InputMismatchException("Input harus integer untuk height (baris kedua)");
            }

            int height = scanner.nextInt();
            if (width <= 0 || height <= 0) {
                throw new InputMismatchException("Input harus lebih dari 0 untuk width dan height");
            }
            scanner.nextLine();
            
            Board board = new Board(width, height);
            
            if (!scanner.hasNextInt()) {
                throw new InputMismatchException("Input harus integer untuk jumlah mobil (baris ketiga)");
            }

            int declaredPieceCount = scanner.nextInt();
            if (declaredPieceCount <= 0) {
                throw new InputMismatchException("Input harus lebih dari 0 untuk jumlah mobil");
            }
            scanner.nextLine();
            
            List<String> gridLines = new ArrayList<>();
            for (int i = 0; i < height; i++) {
                if (!scanner.hasNextLine()) {
                    throw new InputMismatchException("Board kekurangan baris, dibutuhkan " + height + " baris");
                }
                String line = scanner.nextLine().trim();
                gridLines.add(line);
            }
            
            Map<Character, List<int[]>> carPositions = new HashMap<>();
            int doorX = -1;
            int doorY = -1;
            boolean doorFound = false;
            
            for (int y = 0; y < height; y++) {
                String line = gridLines.get(y);
                
                for (int x = 0; x < line.length(); x++) {
                    char cell = line.charAt(x);
                    
                    if (cell == '.') continue;
                    
                    if (cell == 'K') {
                        doorX = x;
                        doorY = y;
                        doorFound = true;
                        System.out.println("Door found inside grid at (" + x + "," + y + ")");
                    } else {
                        carPositions.putIfAbsent(cell, new ArrayList<>());
                        carPositions.get(cell).add(new int[]{x, y});
                    }
                }
            }
            
            for (int y = 0; y < height; y++) {
                String line = gridLines.get(y);
                if (line.length() > width && line.charAt(width) == 'K') {
                    doorX = width;
                    doorY = y;
                    doorFound = true;
                    System.out.println("Door found at right edge at position (" + doorX + "," + doorY + ")");
                    break;
                }
            }
            
            if (!doorFound && scanner.hasNextLine()) {
                String extraLine = scanner.nextLine();
                for (int x = 0; x < Math.min(extraLine.length(), width); x++) {
                    if (extraLine.charAt(x) == 'K') {
                        doorX = x;
                        doorY = height; 
                        doorFound = true;
                        System.out.println("Door found at bottom edge at position (" + doorX + "," + doorY + ")");
                        break;
                    }
                }
            }
            
            if (!doorFound) {
                if (carPositions.containsKey('P')) {
                    List<int[]> primaryPositions = carPositions.get('P');
                    
                    primaryPositions.sort((a, b) -> {
                        if (a[1] != b[1]) return Integer.compare(a[1], b[1]);
                        return Integer.compare(a[0], b[0]); 
                    });
                    
                    boolean isHorizontal = false;
                    if (primaryPositions.size() > 1) {
                        isHorizontal = primaryPositions.get(0)[1] == primaryPositions.get(1)[1];
                    }
                    
                    if (isHorizontal) {
                        doorX = width;
                        doorY = primaryPositions.get(0)[1];
                        System.out.println("Door inferred at right edge for horizontal primary car: (" + doorX + "," + doorY + ")");
                    } else {
                        doorX = primaryPositions.get(0)[0];
                        doorY = height;
                        System.out.println("Door inferred at bottom edge for vertical primary car: (" + doorX + "," + doorY + ")");
                    }
                    doorFound = true;
                } else {
                    throw new InputMismatchException("Pintu keluar (K) tidak ditemukan dan mobil utama (P) tidak ditemukan");
                }
            }
            
            board.setDoor(doorX, doorY);
            
            List<Car> cars = new ArrayList<>();
            for (Map.Entry<Character, List<int[]>> entry : carPositions.entrySet()) {
                char id = entry.getKey();
                List<int[]> positions = entry.getValue();
                
                boolean isPrimary = (id == 'P');
                
                positions.sort((a, b) -> {
                    if (a[1] != b[1]) return Integer.compare(a[1], b[1]);
                    return Integer.compare(a[0], b[0]); 
                });
                
                int x = positions.get(0)[0];
                int y = positions.get(0)[1];
                int length = positions.size();
                
                boolean isHorizontal;
                if (positions.size() > 1) {
                    int sameY = 0;
                    for (int i = 1; i < positions.size(); i++) {
                        if (positions.get(i)[1] == positions.get(0)[1]) sameY++;
                    }
                    isHorizontal = (sameY == positions.size() - 1);
                } else {
                    isHorizontal = true;
                }
                
                Car car = new Car(id, x, y, length, isHorizontal, isPrimary);
                cars.add(car);
                System.out.println("Added car " + id + " at (" + x + "," + y + "), length=" + length + ", horizontal=" + isHorizontal + ", primary=" + isPrimary);
            }
            
            boolean hasPrimary = cars.stream().anyMatch(Car::isPrimary);
            if (!hasPrimary) {
                throw new InputMismatchException("Tidak ada mobil utama (P) ditemukan");
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