import java.util.*;

/*
 * A piece
 * Every piece has 4 cells
 */
public class Piece {
	protected Cell[] cells = new Cell[4];
	protected State[] states; 	// The states of rotation
	private int rotateIndex = 0;// Save the rotation's state
	
	// Constructor
	public Piece() {
		
	}
	
	// Create a random piece
	public static Piece randomPiece() {
		Random r = new Random();
        int type = r.nextInt(7);
        switch(type){
        case 0: return new T();
        case 1: return new I();
        case 2: return new J();
        case 3: return new L();
        case 4: return new O();
        case 5: return new S();
        case 6: return new Z();
        }
        return null;
	}
	
	public Cell[] getCells() {
		return cells;
	}
	
	// Drop method
	public void softDrop() {
        for(Cell c:cells) {
            c.moveDown();
        }
    }
	
	// Move methods
    public void moveRight() {
        for(Cell c:cells) {
            c.moveRight();
        }
    } 
    public void moveLeft() {
        for(Cell c:cells) {
            c.moveLeft();
        }
    }
    
    // Rotate methods
    public void rotate() {
    	rotateIndex++;
    	State s = states[rotateIndex % states.length];
    	Cell origin = cells[0];
    	cells[1].setRow(origin.getRow() + s.row1);
        cells[1].setCol(origin.getCol() + s.col1);
        cells[2].setRow(origin.getRow() + s.row2);
        cells[2].setCol(origin.getCol() + s.col2);
        cells[3].setRow(origin.getRow() + s.row3);
        cells[3].setCol(origin.getCol() + s.col3);
    }
    public void rotateBack() {
    	rotateIndex--;
    	State s = states[rotateIndex % states.length];
    	Cell origin = cells[0];
    	cells[1].setRow(origin.getRow() + s.row1);
        cells[1].setCol(origin.getCol() + s.col1);
        cells[2].setRow(origin.getRow() + s.row2);
        cells[2].setCol(origin.getCol() + s.col2);
        cells[3].setRow(origin.getRow() + s.row3);
        cells[3].setCol(origin.getCol() + s.col3);
    }
	
	protected class State {
		public int row0, row1, row2, row3,
				   col0, col1, col2, col3;
		public State(int col0, int row0, 
				     int col1, int row1,
					 int col2, int row2, 
					 int col3, int row3) {
			this.col0 = col0;
			this.col1 = col1;
			this.col2 = col2;
			this.col3 = col3;
			this.row0 = row0;
			this.row1 = row1;
			this.row2 = row2;
			this.row3 = row3;
		}
	}
}