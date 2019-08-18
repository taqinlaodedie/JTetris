import java.awt.Image;

/*
 * A cell
 * Every cell has its x, y position and image
 */
public class Cell {
	private int row, col;
	private Image image;
	
	// Constructors
	public Cell() {
		
	}
	public Cell(int col, int row, Image image) {
		this.col = col;
		this.row = row;
		this.image = image;
	}
	
	// Get methods
	public int getCol() {
		return col;
	}	
	public int getRow() {
		return row;
	}
	public Image getImage() {
		return image;
	}
	
	// Set methods
	public void setCol(int col) {
		this.col = col;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	
	// Move methods
	public void moveRight() {
		col++;
	}
	public void moveLeft() {
		col--;
	}
	public void moveDown() {
		row++;
	}
	public void moveUp() {
		row--;
	}
}