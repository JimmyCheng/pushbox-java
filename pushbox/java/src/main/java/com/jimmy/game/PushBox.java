package com.jimmy.game;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PushBox extends JFrame {

	private static final int BG_BLACK = 0;
	private static final int BG_WALL = 1;
	private static final int BG_FLOOR = 2;
	private static final int BOX_EMPTY = 3;
	private static final int BALL = 4;
	private static final int BOX_FULL = 7;

	private static final int SOUND_BACKBOX = 1;
	private static final int SOUND_BACKSOUND = 2;
	private static final int SOUND_CLICK = 3;
	private static final int SOUND_MOVE = 4;
	private static final int SOUND_NOMOVE = 5;
	private static final int SOUND_OVER = 6;
	private static final int SOUND_PUSHBOX = 7;
	private static final int SOUND_RETRY = 8;

	public JPanel view;
	public JLabel label;
	// check for
	// http://stackoverflow.com/questions/10051638/updating-an-image-contained-in-a-jlabel-problems
	// better solutions.

	public BufferedImage imgBlack;
	public BufferedImage imgBall;
	public BufferedImage imgFloor;
	public BufferedImage imgPushD1;
	public BufferedImage imgPushD2;
	public BufferedImage imgPushL1;
	public BufferedImage imgPushL2;
	public BufferedImage imgPushR1;
	public BufferedImage imgPushR2;
	public BufferedImage imgPushU1;
	public BufferedImage imgPushU2;
	public BufferedImage imgWall;
	public BufferedImage imgBox;
	public BufferedImage imgBoxFull;

	private int[][] box = new int[14][16]; // 14 rows and 16 columns

	private byte lastX, lastY;
	private byte currX, currY;
	private boolean BoxMoved;
	private int taskID;
	private int boxnum;
	private int steps;
	private boolean soundflag;

	private void drawView() {
		int i, j;
		// disable animate timer

		BufferedImage image = new BufferedImage(480, 420,
				BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = image.createGraphics();

		for (i = 0; i < 14; i++) {
			for (j = 0; j < 16; j++) {
				BufferedImage cell = prepareCell(box[i][j]);
				g.drawImage(cell, j * 30, i * 30, null);
			}
			System.out.println();
		}
		g.dispose();

		label.setIcon(new ImageIcon(image));
		// enable animate timer.
	}

	class TimerTaskTest extends TimerTask {

		public void run() {
			if ((box[currX][currY] % 2) == 0)
				box[currX][currY]--;
			else
				box[currX][currY]++;

			drawView();
		}
	}

	private void animate() {
		Timer timer = new Timer();
		timer.schedule(new TimerTaskTest(), 500, 500); // after 0.5seconds,
														// animates every 0.5
														// seconds.
	}

	// following code should be optimized...use array, load resource in advance.
	private void playSound(int soundNomove) {

		URL url;

		switch (soundNomove) {
		case SOUND_BACKBOX:
			url = this.getClass().getResource("sound/backbox.wav");
			break;
		case SOUND_CLICK:
			url = this.getClass().getResource("sound/click.wav");
			break;

		case SOUND_MOVE:
			url = this.getClass().getResource("sound/move.wav");
			break;
		case SOUND_NOMOVE:
			url = this.getClass().getResource("sound/nomove.wav");
			break;
		case SOUND_OVER:
			url = this.getClass().getResource("sound/over.wav");
			break;
		case SOUND_PUSHBOX:
			url = this.getClass().getResource("sound/pushbox.wav");
			break;

		case SOUND_RETRY:
			url = this.getClass().getResource("sound/retry.wav");
			break;

		default:
			System.out.println("invalide music!");
			return;
		}

		try {
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(url);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
		} catch (Exception ex) {
			System.out.println("Error with playing sound.");
			ex.printStackTrace();
		}
	}

	// todo: enhance the code to load the image at the beginning.
	private void loadImage() throws IOException {

		imgBlack = ImageIO
				.read(this.getClass().getResource("images/Black.bmp"));
		imgBall = ImageIO.read(this.getClass().getResource("images/Ball.bmp"));
		imgFloor = ImageIO
				.read(this.getClass().getResource("images/Floor.bmp"));
		imgPushD1 = ImageIO.read(this.getClass().getResource(
				"images/PushD1.bmp"));
		imgPushD2 = ImageIO.read(this.getClass().getResource(
				"images/PushD2.bmp"));
		imgPushL1 = ImageIO.read(this.getClass().getResource(
				"images/PushL1.bmp"));
		imgPushL2 = ImageIO.read(this.getClass().getResource(
				"images/PushL2.bmp"));
		imgPushR1 = ImageIO.read(this.getClass().getResource(
				"images/PushR1.bmp"));
		imgPushR2 = ImageIO.read(this.getClass().getResource(
				"images/PushR2.bmp"));
		imgPushU1 = ImageIO.read(this.getClass().getResource(
				"images/PushU1.bmp"));
		imgPushU2 = ImageIO.read(this.getClass().getResource(
				"images/PushU2.bmp"));
		imgWall = ImageIO.read(this.getClass().getResource("images/Wall.bmp"));
		imgBox = ImageIO.read(this.getClass().getResource("images/Box.bmp"));
		imgBoxFull = ImageIO.read(this.getClass().getResource(
				"images/BoxFull.bmp"));
	}

	private boolean readTask(int taskID) {

		String fileName = "task/task" + taskID + ".tsk";
		byte[] data = null;
		boxnum = 0;

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
			for (byte i = 0; i < 14; i++) {
				System.out.println();
				for (byte j = 0; j < 16; j++) {
					box[i][j] = data[i * 16 + j];
					System.out.print(box[i][j] + ",");
					if ((box[i][j] == 57) || (box[i][j] == 58)) {
						currX = i;
						currY = j;
					}

					if (box[i][j] == BOX_EMPTY) {
						boxnum++;
					}
				}
			}
			return true;
		}
	}

	private BufferedImage prepareCell(int imgIndex) {
		switch (imgIndex) {
		case BG_BLACK:
			return imgBlack;
		case BG_WALL:
			return imgWall;
		case BG_FLOOR:
			return imgFloor;
		case BOX_EMPTY:
			return imgBox;
		case BALL:
			return imgBall;
		case BOX_FULL:
			return imgBoxFull;
		case 51:
		case 61:
			return imgPushU1;
		case 52:
		case 62:
			return imgPushU2;
		case 53:
		case 63:
			return imgPushL1;
		case 54:
		case 64:
			return imgPushL2;
		case 55:
		case 65:
			return imgPushR1;
		case 56:
		case 66:
			return imgPushR2;
		case 57:
		case 67:
			return imgPushD1;
		case 58:
		case 68:
			return imgPushD2;
		default:
			System.out.println("Invalid imgae index.");
			return null;
		}
	}

	public PushBox() {
		setTitle("Push Box");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(480, 420);
		setResizable(false);

		label = new JLabel();

		label.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				processKeyDown(e);
			}

			public void keyReleased(KeyEvent e) {

			}
		});

		label.setFocusable(true);

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(label, BorderLayout.NORTH);

		try {
			loadImage();
		} catch (IOException e) {
		}

		if (!readTask(88)) {
			System.out.println("Failed to load task!");
		}

		drawView();

		setVisible(true);
	}

	protected void processKeyDown(KeyEvent e) {
		byte xflag = 0, yflag = 0;
		byte UP = 0, LEFT = 0, RIGHT = 0, DOWN = 0;

		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			xflag = 0;
			yflag = -1;
			LEFT = 1;
			break;
		case KeyEvent.VK_UP:
			xflag = -1;
			yflag = 0;
			UP = 1;
			break;
		case KeyEvent.VK_RIGHT:
			xflag = 0;
			yflag = 1;
			RIGHT = 1;
			break;
		case KeyEvent.VK_DOWN:
			xflag = 1;
			yflag = 0;
			DOWN = 1;
			break;
		default:
			playSound(SOUND_NOMOVE);
			return;
		}

		switch (box[currX + xflag][currY + yflag]) {
		case BG_FLOOR:
			playSound(SOUND_MOVE);

			box[currX + xflag][currY + yflag] = 50 + UP * 1 + LEFT * 3 + RIGHT
					* 5 + DOWN * 7;
			switch (box[currX][currY]) {
			case 51:
			case 52:
			case 53:
			case 54:
			case 55:
			case 56:
			case 57:
			case 58:
				box[currX][currY] = BG_FLOOR;
				break;

			case 61:
			case 62:
			case 63:
			case 64:
			case 65:
			case 66:
			case 67:
			case 68:
				box[currX][currY] = BALL;
				break;
			}
			BoxMoved = false;
			break;

		case BALL: // if next position is BALL, use different indicator.
			playSound(SOUND_MOVE);

			box[currX + xflag][currY + yflag] = 60 + UP * 1 + LEFT * 3 + RIGHT
					* 5 + DOWN * 7;
			switch (box[currX][currY]) {
			case 51:
			case 52:
			case 53:
			case 54:
			case 55:
			case 56:
			case 57:
			case 58:
				box[currX][currY] = BG_FLOOR;
				break;

			case 61:
			case 62:
			case 63:
			case 64:
			case 65:
			case 66:
			case 67:
			case 68:
				box[currX][currY] = BALL;
				break;
			}
			BoxMoved = false;
			break;

		case BOX_EMPTY:
			// check whether the empty box can move.
			switch (box[currX + xflag + xflag][currY + yflag + yflag]) {
			case BG_FLOOR:
				playSound(SOUND_PUSHBOX);

				box[currX + xflag + xflag][currY + yflag + yflag] = BOX_EMPTY;
				box[currX + xflag][currY + yflag] = 50 + UP * 1 + LEFT * 3
						+ RIGHT * 5 + DOWN * 7;

				switch (box[currX][currY]) {
				case 51:
				case 52:
				case 53:
				case 54:
				case 55:
				case 56:
				case 57:
				case 58:
					box[currX][currY] = BG_FLOOR;
					break;

				case 61:
				case 62:
				case 63:
				case 64:
				case 65:
				case 66:
				case 67:
				case 68:
					box[currX][currY] = BALL;
					break;
				}
				BoxMoved = true;
				break;

			case BALL:
				playSound(SOUND_PUSHBOX);
				box[currX + xflag + xflag][currY + yflag + yflag] = BOX_FULL;
				box[currX + xflag][currY + yflag] = 50 + UP * 1 + LEFT * 3
						+ RIGHT * 5 + DOWN * 7;
				boxnum--;

				switch (box[currX][currY]) {
				case 51:
				case 52:
				case 53:
				case 54:
				case 55:
				case 56:
				case 57:
				case 58:
					box[currX][currY] = BG_FLOOR;
					break;
				case 61:
				case 62:
				case 63:
				case 64:
				case 65:
				case 66:
				case 67:
				case 68:
					box[currX][currY] = BALL;
					break;
				}
				BoxMoved = true;
				break;

			default:
				playSound(SOUND_NOMOVE);
				return;
			}
			break;

		case BOX_FULL:
			switch (box[currX + xflag + xflag][currY + yflag + yflag]) {
			case BG_FLOOR:
				playSound(SOUND_PUSHBOX);

				box[currX + xflag + xflag][currY + yflag + yflag] = BOX_EMPTY;
				box[currX + xflag][currY + yflag] = 60 + UP * 1 + LEFT * 3
						+ RIGHT * 5 + DOWN * 7;

				boxnum++;
				switch (box[currX][currY]) {
				case 51:
				case 52:
				case 53:
				case 54:
				case 55:
				case 56:
				case 57:
				case 58:
					box[currX][currY] = BG_FLOOR;
					break;
				case 61:
				case 62:
				case 63:
				case 64:
				case 65:
				case 66:
				case 67:
				case 68:
					box[currX][currY] = BALL;
					break;
				}
				BoxMoved = true;
				break;

			case BALL:
				playSound(SOUND_PUSHBOX);

				box[currX + xflag + xflag][currY + yflag + yflag] = BOX_FULL;
				box[currX + xflag][currY + yflag] = 60 + UP * 1 + LEFT * 3
						+ RIGHT * 5 + DOWN * 7;

				switch (box[currX][currY]) {
				case 51:
				case 52:
				case 53:
				case 54:
				case 55:
				case 56:
				case 57:
				case 58:
					box[currX][currY] = BG_FLOOR;
					break;
				case 61:
				case 62:
				case 63:
				case 64:
				case 65:
				case 66:
				case 67:
				case 68:
					box[currX][currY] = BALL;
					break;
				}
				BoxMoved = true;
				break;

			default:
				playSound(SOUND_NOMOVE);
				return;
			}
			break;

		default:
			playSound(SOUND_NOMOVE);
			return;
		}

		lastX = currX;
		lastY = currY;
		currX = (byte) (currX + xflag);
		currY = (byte) (currY + yflag);

		drawView();
	}

	public static void main(String[] args) {
		PushBox pb = new PushBox();
		pb.setVisible(true);
		pb.animate();
	}

}
