import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.imageio.*;
import javax.swing.*;

import java.util.*;
import java.util.Timer;

public class Tetris extends JPanel {
	private Piece currentOne, nextOne;
	public static final int ROWS = 20, COLS = 10;
	private Cell[][] wall = new Cell[ROWS][COLS];
	private int lines;	// Deleted lines
	private int score;	
	public static final int SCORE_TAB[] = {0, 10, 20, 40, 80};	// Score for delete 1, 2, 3 and 4 lines
	public static final int CELL_SIZE = 26;
	public static final int PERIOD = 1000;
	private static Image background;
	public static Image I, J, L, O, S, T, Z;
	// Load the images
	static {
		 try {
			background = ImageIO.read(Tetris.class.getResource("tetris.png"));
	     	T=ImageIO.read(Tetris.class.getResource("T.png"));
	      	I=ImageIO.read(Tetris.class.getResource("I.png"));
	      	S=ImageIO.read(Tetris.class.getResource("S.png"));
	      	Z=ImageIO.read(Tetris.class.getResource("Z.png"));
	       	L=ImageIO.read(Tetris.class.getResource("L.png"));
	       	J=ImageIO.read(Tetris.class.getResource("J.png"));
	      	O=ImageIO.read(Tetris.class.getResource("O.png"));
	     }
		 catch(Exception e) {
			 e.printStackTrace();
		 }
	}
	private boolean gameOver, pause;	// Game status
	private Timer timer;
	
	public void doIt() {
		start();
		repaint();
		KeyAdapter l = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_Q) 
					System.exit(0);
				if(gameOver) {
					if(key == KeyEvent.VK_SPACE)
						start();
					return;
				}
				if(pause) {
					if(key == KeyEvent.VK_SPACE)
						continueAction();
					return;
				}
				switch(key) {
				case KeyEvent.VK_UP: rotateAction(); break;
				case KeyEvent.VK_LEFT: moveLeftAction(); break;
				case KeyEvent.VK_RIGHT: moveRightAction(); break;
				case KeyEvent.VK_DOWN: softDropAction(); break;
				case KeyEvent.VK_SPACE: pauseAction(); break;
				}
				repaint();
			}
		};
		this.requestFocus();
		this.addKeyListener(l);
	}
	
	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, null);	// Paint the background
		g.translate(15, 15);					// Move the axis
		paintWall(g);
		paintCurrentOne(g);
		paintNextOne(g);
		paintScore(g);
	}
	
	public static final int FONT_COLOR = 0x667799;
	public static final int FONT_SIZE = 0x15;
	// Paint the current piece
	private void paintCurrentOne(Graphics g) {
		Cell[] cells = currentOne.getCells();
		for(Cell c:cells) {
			int x = c.getCol()*CELL_SIZE - 1;
			int y = c.getRow()*CELL_SIZE - 1;
			g.drawImage(c.getImage(), x, y, null);
		}
	}
	// Paint the next piece
	private void paintNextOne(Graphics g) {
		Cell[] cells = nextOne.getCells();
		for(Cell c:cells) {
			int x = (c.getCol()+14) * (CELL_SIZE-1);
			int y = (c.getRow()-3) * (CELL_SIZE-2);
			g.drawImage(c.getImage(), x, y, null);
		}
	}
	// Paint the wall
	private void paintWall(Graphics g) {
		for(int row = 0; row < wall.length; row++) {
			Cell[] line = wall[row];
			for(int col = 0; col < line.length; col++) {
				Cell cell = line[col];
				int x = col*CELL_SIZE - 1; 
				int y = row*CELL_SIZE - 1; 
				if(cell != null) 
					g.drawImage(cell.getImage(), x, y, null);
			}
		}
	}
	// Paint the score information
	private void paintScore(Graphics g) {
		Font f = getFont();
		Font font = new Font(f.getName(), Font.BOLD, FONT_SIZE);
		int x = 290, y = 162;
		g.setColor(new Color(FONT_COLOR));
		g.setFont(font);
		String s = "Score: " + score;
		g.drawString(s, x, y);
		y += 56;
		s = "Lines: " + lines;
		g.drawString(s, x, y);
		y += 56;
		if(pause)
			s = "Continue:SPACE";
		else
			s = "Pause:SPACE";
		g.drawString(s, x, y);
	}
	
	// Move and rotate actions
	private void rotateAction() {
		currentOne.rotate();
		if(outOfBound() || coincide())
			currentOne.rotateBack();
	}
	private void moveLeftAction() {
		currentOne.moveLeft();
		if(outOfBound() || coincide())
			moveRightAction();
	}
	private void moveRightAction() {
		currentOne.moveRight();
		if(outOfBound() || coincide())
			moveLeftAction();
	}
	private void softDropAction() {
		if(canDrop())
			currentOne.softDrop();
		else {
			landToWall();
			deleteLines();
			endCheck();
			currentOne = nextOne;
			nextOne = Piece.randomPiece();
		}
	}
	
	// Check if the piece is out of bound
	private boolean outOfBound() {
		Cell[] cells = currentOne.getCells();
		for(Cell c:cells) {
			int col = c.getCol();
			if(col >= COLS || col < 0) 
				return true;
		}
		return false;
	}
	
	// Check if there is another cell at the position of a cell of current piece
	private boolean coincide() {
		Cell[] cells = currentOne.getCells();
		for(Cell c:cells) {
			int row = c.getRow();
			int col = c.getCol();
			if(row < 0 || row >= ROWS || wall[row][col] != null) 
				return true;
		}
		return false;
	}
	
	// Add the current piece to the wall
	private void landToWall() {
		Cell[] cells = currentOne.getCells();
		for(Cell c:cells) {
			int row = c.getRow();
			int col = c.getCol();
			wall[row][col] = c;
		}
	}
	
	// Check if the current piece can drop
	private boolean canDrop() {
		Cell[] cells = currentOne.getCells();
		for(Cell c:cells) {
			int row = c.getRow();
			int col = c.getCol();
			if((row == ROWS - 1) || (wall[row + 1][col] != null))
				return false;
		}
		return true;
	}
	
	// Delete the full lines
	private void deleteLines() {
		int lines = 0;
		for(int row = 0; row < wall.length; row++) {
			if(fullLine(row)) {
				deleteRow(row);
				lines++;
			}
			this.lines += lines;
			this.score += SCORE_TAB[lines];
		}
	}
	
	// Check if the line is full
	private boolean fullLine(int row) {
		Cell[] line = wall[row];
		for(Cell c:line) {
			if(c == null)
				return false;
		}
		return true;
	}
	
	// Delete the row
	private void deleteRow(int row) {
		// wall[i - 1] => wall[i], wall[0] = null
		for(int i = row; i > 0; i--) 
			System.arraycopy(wall[i - 1], 0, wall[i], 0, COLS);
		Arrays.fill(wall[0], null);
	}
	
	// Check if the game is over
	private void endCheck() {
		if(wall[4][0] == null)
			return;
		gameOver = true;
		timer.cancel();
		repaint();			
		JOptionPane.showMessageDialog(null, "Game Over!\n Press [Space] to restart...");
	}

	// Initialize the game and start
	private void start() {
		clearWall();
		currentOne = Piece.randomPiece();
		nextOne = Piece.randomPiece();
		lines = 0;
		score = 0;
		pause = false;
		gameOver = false;
		timer = new Timer();
		timer.schedule(new TimerTask() {		
			@Override
			public void run() {
				softDropAction();
				repaint();
			}
		}, PERIOD, PERIOD);
	}
	
	// Reinitialize the wall
	private void clearWall() {
		for(int row = 0; row < wall.length; row++) {
			Arrays.fill(wall[row], null);
		}
	}
	
	// Pause and continue the game
	private void pauseAction() {
		timer.cancel();
		pause = true;
		repaint();
	}
	private void continueAction() {
		timer = new Timer();
		timer.schedule(new TimerTask() {			
			@Override
			public void run() {
				softDropAction();
				repaint();
			}
		}, PERIOD, PERIOD);
		pause = false;
		repaint();
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Tetris");
		Tetris tetris = new Tetris();
		frame.add(tetris);
		frame.setSize(525, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		tetris.doIt();
	}
}
