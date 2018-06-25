package com.pushbox.component;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.*;

enum Direction {
   UP(0), RIGHT(1), DOWN(2), LEFT(3);
   
   int order;
   
   Direction(int id) {
	   order = id;
   }
   
   public int getOrder() {
	return order;
   }
}

class Cell {

	static final byte NONE = 0;
	static final byte UP1 = 1;
	static final byte UP2 = 2;
	static final byte RIGHT1 = 3;
	static final byte RIGHT2 = 4;
	static final byte DOWN1 = 5;
	static final byte DOWN2 = 6;
	static final byte LEFT1 = 7;
	static final byte LEFT2 = 8;

	boolean black;
	boolean wall;
	boolean floor;
	boolean box;
	boolean ball;
	boolean spirit;
	int action;

	public Cell() {
		black = false;
		wall = false;
		floor = false;
		box = false;
		ball = false;
		spirit = false;
		action = NONE;
	}
}

class Position {
	public byte x;
	public byte y;
	public Direction dir;
	public boolean boxmoved;
}

public class PushBox extends JFrame {

	private static final int IMG_BLACK = 0;
	private static final int IMG_BALL = 1;
	private static final int IMG_FLOOR = 2;
	private static final int IMG_WALL = 3;
	private static final int IMG_BOX = 4;
	private static final int IMG_BOXFULL = 5;
	private static final int IMG_PUSHU1 = 6;
	private static final int IMG_PUSHU2 = 7;
	private static final int IMG_PUSHR1 = 8;
	private static final int IMG_PUSHR2 = 9;
	private static final int IMG_PUSHD1 = 10;
	private static final int IMG_PUSHD2 = 11;
	private static final int IMG_PUSHL1 = 12;
	private static final int IMG_PUSHL2 = 13;

	
	private BufferedImage[] IMG_Resource;
	private Cell[][] grid;

	private byte currX, currY;
	private int taskID;
	private int boxCount;
	private int boxDone;
	private int steps;
    public Stack<Position> history;
    
	public JLabel lblView;
	public StatusBar lblStatus;

	public PushBox() {
		initializeResource();
		initializeGUI();
	}

	public void newGame(int taksId) {
		initializeTask(taksId);
		drawView();
		animate();
		setVisible(true);
	}

	private void initializeResource() {
		IMG_Resource = new BufferedImage[14];
		try {
			IMG_Resource[IMG_BLACK] = ImageIO.read(this.getClass().getResource("images/Black.bmp"));
			IMG_Resource[IMG_BALL]  = ImageIO.read(this.getClass().getResource("images/Ball.bmp"));
			IMG_Resource[IMG_FLOOR] = ImageIO.read(this.getClass().getResource("images/Floor.bmp"));
			IMG_Resource[IMG_WALL]  = ImageIO.read(this.getClass().getResource("images/Wall.bmp"));
			IMG_Resource[IMG_BOX]   = ImageIO.read(this.getClass().getResource("images/Box.bmp"));
			IMG_Resource[IMG_BOXFULL] = ImageIO.read(this.getClass().getResource("images/BoxFull.bmp"));
			
			IMG_Resource[IMG_PUSHU1] = ImageIO.read(this.getClass().getResource("images/PushU1.bmp"));
			IMG_Resource[IMG_PUSHU2] = ImageIO.read(this.getClass().getResource("images/PushU2.bmp"));
			IMG_Resource[IMG_PUSHR1] = ImageIO.read(this.getClass().getResource("images/PushR1.bmp"));			
			IMG_Resource[IMG_PUSHR2] = ImageIO.read(this.getClass().getResource("images/PushR2.bmp"));
			IMG_Resource[IMG_PUSHD1] = ImageIO.read(this.getClass().getResource("images/PushD1.bmp"));
			IMG_Resource[IMG_PUSHD2] = ImageIO.read(this.getClass().getResource("images/PushD2.bmp"));
			IMG_Resource[IMG_PUSHL1] = ImageIO.read(this.getClass().getResource("images/PushL1.bmp"));			
			IMG_Resource[IMG_PUSHL2] = ImageIO.read(this.getClass().getResource("images/PushL2.bmp"));
		} catch (IOException e) {
			System.out.println("Failed to load game resources!");
		}
		
		SoundEffect.init();
		SoundEffect.volume = SoundEffect.Volume.LOW; 

		history = new Stack<Position>();
	}
	
	private void initializeGUI() {
		setTitle("Push Box");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(480, 420+60);
		setResizable(false);

		lblView = new JLabel();

		lblView.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				processKeyDown(e);
			}

			public void keyReleased(KeyEvent e) {

			}
		});

		lblView.setFocusable(true);
		
		lblStatus = new StatusBar();

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(lblView, BorderLayout.NORTH);
		cp.add(lblStatus, BorderLayout.SOUTH);
	}

	protected void processKeyDown(KeyEvent e) {
		byte xflag = 0, yflag = 0;
		Direction direct;

		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			yflag = -1;
			direct = Direction.LEFT;
			break;
		case KeyEvent.VK_UP:
			xflag = -1;
			direct = Direction.UP;
			break;
		case KeyEvent.VK_RIGHT:
			yflag = 1;
			direct = Direction.RIGHT;
			break;
		case KeyEvent.VK_DOWN:
			xflag = 1;
			direct = Direction.DOWN;
			break;
		case KeyEvent.VK_F2:
			Undo();
			return;
		default:
			SoundEffect.NOMOVE.play();
			return;
		}

		// current call, next cell and next next cell.
		Cell cCell = grid[currX][currY];
		Cell nCell = grid[currX + xflag][currY + yflag];
		Cell nnCell = grid[currX + xflag + xflag][currY + yflag + yflag];

		// Can't move
		if (nCell.wall) {
			SoundEffect.NOMOVE.play();
			return;
		}

		if (nCell.box && (nnCell.box || nnCell.wall)) {
			SoundEffect.NOMOVE.play();
			return;
		}

		// Can move.
		cCell.spirit = false;
		nCell.spirit = true;
		nCell.action = direct.getOrder()*2 + 1;  //To calculate the image index.
		
		Position pos = new Position();
		pos.x = currX;
		pos.y = currY;
		pos.dir = direct;
		
		if (nCell.box) {
			nCell.box = false;
			nnCell.box = true;
			pos.boxmoved = true;

			if(nnCell.ball) {
				boxDone++;
				//the logic is too complicated to track this.
				// instead of checking this, we can scan all the balls, check whether they are all covered with box.
                if(boxDone == boxCount) {
					JOptionPane.showMessageDialog(this, "You win!");
					//todo will be a dialog to go next or replay.
					newGame(taskID + 1);
				}
			}
			SoundEffect.PUSHBOX.play();;
		} else {
			SoundEffect.MOVE.play();
		}
				
		history.push(pos);  //save the move history.
		
		currX = (byte) (currX + xflag);
		currY = (byte) (currY + yflag);

		drawView();
	}

	private void Undo() {
		byte xflag=0, yflag =0;
		 
		if (history.empty()) {
			SoundEffect.NOMOVE.play();
			return;
		}
		
		//restore current position.
		grid[currX][currY].spirit = false;
		
		Position pos = history.pop();
		
		if(pos.boxmoved) {
			grid[currX][currY].box = true;
			
			switch(pos.dir.getOrder()) {
			case 0: //UP
				xflag = -1;
				break;
			case 1: //Right
				yflag = 1;
			    break;
			    
			case 2: //Down;
				xflag = 1;
				break;
			case 3: //Left
				yflag = -1;
				break;
			default:
				System.out.println("Error, should not come here.!");
			}
			grid[currX + xflag][currY + yflag].box = false;
		}
		
		//set new current position.
		currX = pos.x;
		currY = pos.y;		
		grid[currX][currY].spirit = true;
		
		SoundEffect.MOVE.play();	
		
		drawView();
	}

	// check for
	// http://stackoverflow.com/questions/10051638/updating-an-image-contained-in-a-jlabel-problems
	// better solutions.
	private void drawView() {
		int i, j;
		BufferedImage view = new BufferedImage(480, 420, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = view.createGraphics();

		for (i = 0; i < 14; i++) {
			for (j = 0; j < 16; j++) {
				BufferedImage img = prepareImg(grid[i][j]);
				g.drawImage(img, j * 30, i * 30, null);
			}
		}
		g.dispose();

		lblView.setIcon(new ImageIcon(view));
	}

	class TimerTaskTest extends TimerTask {

		public void run() {
			if (grid[currX][currY].action % 2 == 0) {
				grid[currX][currY].action--;
			} else {
				grid[currX][currY].action++;
			}

			drawView();
		}
	}

	private void animate() {
		Timer timer = new Timer();
		timer.schedule(new TimerTaskTest(), 500, 500);
	}

	private boolean initializeTask(int taskID) {
		this.taskID = taskID;

		String fileName = "task/task" + taskID + ".tsk";

		byte[] data = null;

		boxCount = 0;
		boxDone = 0;
		try {
			URI uri = this.getClass().getResource(fileName).toURI();
			data = Files.readAllBytes(Paths.get(uri));
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(Array.getLength(data));

		if (Array.getLength(data) != 224) {
			return false;
		} else {
			
			grid = new Cell[14][16];  //14 rows and 16 columns.
			
			for (byte i = 0; i < 14; i++) {
				for (byte j = 0; j < 16; j++) {

					grid[i][j] = new Cell();

					// hardcoded data in task file.
					switch (data[i * 16 + j]) {
					case 0:
						grid[i][j].black = true;
						break;

					case 1:
						grid[i][j].wall = true;
						break;

					case 2:
						grid[i][j].floor = true;
						break;

					case 3:
						grid[i][j].floor = true;
						grid[i][j].box = true;
						boxCount++;
						break;

					case 4:
						grid[i][j].ball = true;
						break;

					case 7: //box is full.
						grid[i][j].box = true;
						grid[i][j].ball = true;
						boxDone++;
						boxCount++;
						break;

					case 57:
						grid[i][j].floor = true;
						grid[i][j].spirit = true;
						grid[i][j].action = Cell.DOWN1;
						currX = i;
						currY = j;
						break;

					case 58:
						grid[i][j].floor = true;
						grid[i][j].spirit = true;
						grid[i][j].action = Cell.DOWN2;
						currX = i;
						currY = j;
						break;

					default:
						System.out.println("error task data!");
					}
				}
			}
			return true;
		}
	}

	private BufferedImage prepareImg(Cell cell) {

		if (cell.black) {
			return IMG_Resource[IMG_BLACK];
		}

		if (cell.wall) {
			return IMG_Resource[IMG_WALL];
		}

		// Priority to display the upper layer.
		// Box or Spirit -> Ball -> Floor.
		if (cell.box) {
			if (cell.ball) {
				return IMG_Resource[IMG_BOXFULL];
			} else {
				return IMG_Resource[IMG_BOX];
			}
		}

		if (cell.spirit) {
			switch (cell.action) {
			case Cell.DOWN1:
				return IMG_Resource[IMG_PUSHD1];
			case Cell.DOWN2:
				return IMG_Resource[IMG_PUSHD2];
			case Cell.LEFT1:
				return IMG_Resource[IMG_PUSHL1];
			case Cell.LEFT2:
				return IMG_Resource[IMG_PUSHL2];
			case Cell.RIGHT1:
				return IMG_Resource[IMG_PUSHR1];
			case Cell.RIGHT2:
				return IMG_Resource[IMG_PUSHR2];
			case Cell.UP1:
				return IMG_Resource[IMG_PUSHU1];
			case Cell.UP2:
				return IMG_Resource[IMG_PUSHU2];
			case Cell.NONE:
				System.out.println("Incorrect setting spirit action!");
			default:
				System.out.println("Incorrect setting spirit action!");
			}
		}

		if (cell.ball) {
			return IMG_Resource[IMG_BALL];
		}

		if (cell.floor) {
			return IMG_Resource[IMG_FLOOR];
		}
		return null;
	}


}
