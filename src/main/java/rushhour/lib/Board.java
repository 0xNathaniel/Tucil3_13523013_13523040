package rushhour.lib; 
public class Board {
    private int width;
    private int height;
    private char[][] grid;
    private Car[] cars;
    private int doorX;
    private int doorY;
    
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new char[height + 2][width + 2];
        
        for (int i = 0; i < height + 2; i++) {
            for (int j = 0; j < width + 2; j++) {
                if (i == 0 || i == height + 1 || j == 0 || j == width + 1) {
                    grid[i][j] = 'X';
                } else {
                    grid[i][j] = '.';
                }
            }
        }
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public char[][] getGrid() {
        return grid;
    }
    
    public Car[] getCars() {
        return cars;
    }
    
    public int getDoorX() {
        return doorX;
    }
    
    public int getDoorY() {
        return doorY;
    }
    
    public boolean isCellEmpty(int x, int y) {
        int gridX = x + 1;
        int gridY = y + 1;
        
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
        return grid[gridY][gridX] == '.';
    }
    
    public boolean isDoorAt(int x, int y) {
        return x == doorX && y == doorY;
    }
    
    public void updateGrid() {
        for (int i = 0; i < height + 2; i++) {
            for (int j = 0; j < width + 2; j++) {
                if (i == 0 || i == height + 1 || j == 0 || j == width + 1) {
                    grid[i][j] = 'X';
                } else {
                    grid[i][j] = '.';
                }
            }
        }
        if (doorX >= 0 && doorX < width && doorY >= 0 && doorY < height) {
            grid[doorY + 1][doorX + 1] = 'K';
        } else if (doorX == width && doorY >= 0 && doorY < height) {
            grid[doorY + 1][doorX + 1] = 'K';
        } else if (doorX >= 0 && doorX < width && doorY == height) {
            grid[doorY + 1][doorX + 1] = 'K';
        }
        
        if (cars != null) {
            for (Car car : cars) {
                int[][] cells = car.posisiCar();
                for (int[] cell : cells) {
                    int x = cell[0];
                    int y = cell[1];
                    if (x >= 0 && x < width && y >= 0 && y < height) {
                        grid[y + 1][x + 1] = car.getId();
                    }
                }
            }
        }
    }

    public Car getCarsAt(int x, int y) {
        for (Car car : cars) {
            int[][] positions = car.posisiCar();
            for (int[] pos : positions) {
                if (pos[0] == x && pos[1] == y) {
                    return car;
                }
            }
        }
        return null;
    }
    
    public void setCars(Car[] cars) {
        this.cars = cars;
        updateGrid();
    }
    
    public void setDoor(int x, int y) {
        this.doorX = x;
        this.doorY = y;
        updateGrid();
    }
    
    public boolean isSolved() {
    for (Car car : cars) {
        if (car.isPrimary()) {
            if (car.isHorizontal()) {
                //right door (doorX == width)
                if (doorX == width && car.getX() + car.getLength() == width && car.getY() == doorY) {
                    return true;
                }
                //left door (doorX == -1)
                else if (doorX == -1 && car.getX() == 0 && car.getY() == doorY) {
                    return true;
                }
                else if (doorX >= 0 && doorX < width && 
                         car.getX() + car.getLength() == doorX && car.getY() == doorY) {
                    return true;
                }
            } else {//vertical car
                if (doorY == height && car.getY() + car.getLength() == height && car.getX() == doorX) {
                    return true;
                }
                else if (doorY == -1 && car.getY() == 0 && car.getX() == doorX) {
                    return true;
                }
                else if (doorY >= 0 && doorY < height &&
                         car.getY() + car.getLength() == doorY && car.getX() == doorX) {
                    return true;
                }
            }
            return false;
        }
    }
    return false;
}
    
    public void printBoard() {
        for (int i = 0; i < height + 2; i++) {
            for (int j = 0; j < width + 2; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
    }
    
    public void printPlayableArea() {
        for (int i = 1; i <= height; i++) {
            for (int j = 1; j <= width; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
    }
    
    public Car getCarById(char id) {
        for (Car car : cars) {
            if (car.getId() == id) {
                return car;
            }
        }
        return null;
    }
    
    public Board copy() {
        Board newBoard = new Board(width, height);
        newBoard.setDoor(doorX, doorY);
        
        if (cars != null) {
            Car[] newCars = new Car[cars.length];
            for (int i = 0; i < cars.length; i++) {
                newCars[i] = cars[i].copy();
            }
            newBoard.setCars(newCars);
        }
        
        return newBoard;
    }
}