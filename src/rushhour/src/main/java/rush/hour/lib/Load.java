package rush.hour.lib;
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
            scanner.nextLine();
            
            Board board = new Board(width, height);
            
            if (!scanner.hasNextInt()) {
                throw new InputMismatchException("Input harus integer untuk jumlah mobil (baris ketiga)");
            }
            int declaredPieceCount = scanner.nextInt();
            scanner.nextLine();
            
            char[][] gameGrid = new char[height][width];
            
            for (int i = 0; i < height; i++) {
                if (!scanner.hasNextLine()) {
                    throw new InputMismatchException("Board kekurangan baris");
                }
                
                String line = scanner.nextLine();
                if (line.length() < width) {
                    throw new InputMismatchException("Board kekurangan kolom di row " + (i + 1));
                }
                
                for (int j = 0; j < width; j++) {
                    gameGrid[i][j] = line.charAt(j);
                }
            }
            
            Map<Character, List<int[]>> carPositions = new HashMap<>();
            int doorX = -1;
            int doorY = -1;
            boolean doorFound = false;
            
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    char cell = gameGrid[y][x];
                    if (cell == '.') {
                        continue;
                    } else if (cell == 'K') {
                        doorX = x;
                        doorY = y;
                        doorFound = true;
                    } else {
                        carPositions.putIfAbsent(cell, new ArrayList<>());
                        carPositions.get(cell).add(new int[]{x, y});
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
                    } else {
                        doorX = primaryPositions.get(0)[0];
                        doorY = height;
                    }
                    doorFound = true;
                }
            }
            
            if (!doorFound) {
                throw new InputMismatchException("Pintu keluar tidak ditemukan (K)");
            }
            
            board.setDoor(doorX, doorY);
            
            int actualPieceCount = carPositions.size();  
            
            if (declaredPieceCount != actualPieceCount) {
                throw new InputMismatchException("Declared piece count (" + declaredPieceCount + 
                                                ") does not match actual count (" + actualPieceCount + ")");
            }
            
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
                    int sameY = 0, sameX = 0;
                    for (int i = 1; i < positions.size(); i++) {
                        if (positions.get(i)[1] == positions.get(0)[1]) sameY++;
                        if (positions.get(i)[0] == positions.get(0)[0]) sameX++;
                    }
                    isHorizontal = (sameY == positions.size() - 1);
                    
                    if (!isHorizontal && sameX != positions.size() - 1) {
                        throw new InputMismatchException("Car " + id + 
                                                        " is not properly aligned (must be horizontal or vertical)");
                    }
                } else {
                    isHorizontal = true;
                }
                
                Car car = new Car(id, x, y, length, isHorizontal, isPrimary);
                cars.add(car);
            }
            
            long primaryCount = cars.stream().filter(Car::isPrimary).count();
            if (primaryCount != 1) {
                throw new InputMismatchException("There must be exactly one primary car (P), found: " + primaryCount);
            }
            
            board.setCars(cars.toArray(new Car[0]));
            
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