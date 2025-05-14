public class Car {
    private char id;
    private int x;
    private int y;
    private int length;
    private boolean isHorizontal;
    private boolean isPrimary; // mobil yg dikeluarin bukan 

    public Car() {
        this.id = 'K';
        this.x = 0;
        this.y = 0;
        this.length = 0;
        this.isHorizontal = false;
        this.isPrimary = false;
    }

    public Car(char id, int x, int y, int length, boolean isHorizontal, boolean isPrimary) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.length = length;
        this.isHorizontal = isHorizontal;
        this.isPrimary = isPrimary;
    }

    public char getId() {
        return id;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getLength() {
        return length;
    }
    
    public boolean isHorizontal() {
        return isHorizontal;
    }
    
    public boolean isPrimary() {
        return isPrimary;
    }
    public boolean canMoveUp(Board board) {
        if (isHorizontal) return false;//mobil horizontal ga bisa naik
        return y > 0 && board.isCellEmpty(x, y - 1);
    }
    
    public boolean canMoveDown(Board board) {
        if (isHorizontal) return false;  //mobil horizontal ga bisa turun
        return y + length < board.getHeight() && board.isCellEmpty(x, y + length);
    }
    
    public boolean canMoveLeft(Board board) {
        if (!isHorizontal) return false;  //mobil vertical ga bisa ke kiri
        return x > 0 && board.isCellEmpty(x - 1, y);
    }
    
    public boolean canMoveRight(Board board) {
        if (!isHorizontal) return false;  //mobil vertical ga bisa ke kanan
        int rightEdge = x + length;
        if (isPrimary && board.isDoorAt(rightEdge, y)) {
            return true;
        }
        return rightEdge < board.getWidth() && board.isCellEmpty(rightEdge, y);
    }
    
    public void moveUp() {
        y--;
    }
    
    public void moveDown() {
        y++;
    }
    
    public void moveLeft() {
        x--;
    }
    
    public void moveRight() {
        x++;
    }

    public int[][] posisiCar() {
        int[][] cells = new int[length][2];
        for (int i = 0; i < length; i++) {
            if (isHorizontal) {
                cells[i][0] = x + i;
                cells[i][1] = y;
            } else {
                cells[i][0] = x;
                cells[i][1] = y + i;
            }
        }
        return cells;
    }

     public Car copy() {
        return new Car(id, x, y, length, isHorizontal, isPrimary);
    }
}
