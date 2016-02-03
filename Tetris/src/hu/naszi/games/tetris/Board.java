package hu.naszi.games.tetris;

import hu.naszi.games.tetris.Shape.Tetrominoes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {
	final int BOARDWIDTH = 10;
	final int BOARDHEIGHT = 22;

	Timer timer;
	boolean isFallingFinished = false;
	boolean isStarted = false;
	boolean isPaused = false;
	int numLinesRemoved = 0;
	int curX = 0;
	int curY = 0;
	JLabel statusBar;
	Shape curPiece;
	Tetrominoes[] board;

	public Board(Tetris parent) {
		setFocusable(true);
		curPiece = new Shape();
		timer = new Timer(400, this);
		timer.start();

		statusBar = parent.getStatusBar();
		board = new Tetrominoes[BOARDWIDTH * BOARDHEIGHT];
		addKeyListener(new TAdapter());
		clearBoard();
	}

	public void actionPerformed(ActionEvent e) {
		if (isFallingFinished) {
			isFallingFinished = false;
			newPiece();
		} else {
			oneLineDown();
		}

	}

	private void oneLineDown() {
		if (!tryMove(curPiece, curX, curY - 1))
			pieceDropped();
	}

	int squareWidth() {
		return (int) getSize().getWidth() / BOARDWIDTH;
	}

	int squareHeight() {
		return (int) getSize().getHeight() / BOARDHEIGHT;
	}

	Tetrominoes shapeAt(int x, int y) {
		return board[(y * BOARDWIDTH) + x];
	}

	public void start() {
		if (isPaused)
			return;

		isStarted = true;
		isFallingFinished = false;
		numLinesRemoved = 0;
		clearBoard();
		
		newPiece();
		timer.start();
	}

	private void pause() {
		if (!isStarted)
			return;

		isPaused = !isPaused;
		if (isPaused) {
			timer.stop();
			statusBar.setText("Paused");
		} else {
			timer.start();
			statusBar.setText(String.valueOf(numLinesRemoved));
		}
	}

	public void paint(Graphics g) {
		super.paint(g);

		Dimension size = getSize();
		int boardTop = (int) (size.getHeight() - BOARDHEIGHT * squareHeight());

		for (int i = 0; i < BOARDHEIGHT; ++i) {
			for (int j = 0; j < BOARDWIDTH; ++j) {
				Tetrominoes shape = shapeAt(j, BOARDHEIGHT - i - 1);
				if (shape != Tetrominoes.NOSHAPE) {
					drawSquare(g, 0 + j * squareWidth(), boardTop + i
							* squareHeight(), shape);
				}
			}
		}

		if (curPiece.getShape() != Tetrominoes.NOSHAPE) {
			for (int i = 0; i < 4; ++i) {
				int x = curX + curPiece.x(i);
				int y = curY - curPiece.y(i);
				drawSquare(g, 0 + x * squareWidth(), boardTop
						+ (BOARDHEIGHT - y - 1) * squareHeight(),
						curPiece.getShape());
			}
		}
	}

	private void dropDown() {
		int newY = curY;
		while (newY > 0) {
			if (!tryMove(curPiece, curX, newY - 1))
				break;
			--newY;
		}
		pieceDropped();
	}

	private void clearBoard() {
		for (int i = 0; i < BOARDHEIGHT * BOARDWIDTH; ++i)
			board[i] = Tetrominoes.NOSHAPE;
	}

	private void pieceDropped() {
		for (int i = 0; i < 4; ++i) {
			int x = curX + curPiece.x(i);
			int y = curY - curPiece.y(i);
			board[(y * BOARDWIDTH) + x] = curPiece.getShape();
		}
		removeFullLines();

		if (!isFallingFinished)
			newPiece();
	}

	private void newPiece() {
		curPiece.setRandomShape();
		curX = BOARDWIDTH / 2 + 1;
		curY = BOARDHEIGHT - 1 + curPiece.minY();

		if (!tryMove(curPiece, curX, curY)) {
			curPiece.setShape(Tetrominoes.NOSHAPE);
			timer.stop();
			isStarted = false;
			statusBar.setText("Game Over");
		}
	}

	private boolean tryMove(Shape newPiece, int newX, int newY) {
		for (int i = 0; i < 4; ++i) {
			int x = newX + newPiece.x(i);
			int y = newY - newPiece.y(i);
			if (x < 0 || x >= BOARDWIDTH || y < 0 || y >= BOARDHEIGHT)
				return false;
			if (shapeAt(x, y) != Tetrominoes.NOSHAPE)
				return false;
		}

		curPiece = newPiece;
		curX = newX;
		curY = newY;
		repaint();
		return true;
	}

	private void removeFullLines() {
		int numFullLines = 0;

		for (int i = BOARDHEIGHT - 1; i >= 0; --i) {

			boolean lineIsFull = true;

			for (int j = 0; j < BOARDWIDTH; ++j) {
				if (shapeAt(j, i) == Tetrominoes.NOSHAPE) {
					lineIsFull = false;
					break;
				}
			}

			if (lineIsFull) {
				++numFullLines;
				for (int k = i; k < BOARDHEIGHT - 1; ++k) {
					for (int j = 0; j < BOARDWIDTH; ++j)
						board[(k * BOARDWIDTH) + j] = shapeAt(j, k + 1);
				}
			}
		}

		if (numFullLines > 0) {
			numLinesRemoved += numFullLines;
			statusBar.setText(String.valueOf(numLinesRemoved));
			isFallingFinished = true;
			curPiece.setShape(Tetrominoes.NOSHAPE);
			repaint();
		}
	}

	private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
		Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102),
				new Color(102, 204, 102), new Color(102, 102, 204),
				new Color(204, 204, 102), new Color(204, 102, 204),
				new Color(102, 204, 204), new Color(204, 170, 0) };

		Color color = colors[shape.ordinal()];

		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

		g.setColor(color.brighter());
		g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y
				+ squareHeight() - 1);
		g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x
				+ squareWidth() - 1, y + 1);
	}

	class TAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (!isStarted || curPiece.getShape() == Tetrominoes.NOSHAPE)
				return;

			int keyCode = e.getKeyCode();

			if (keyCode == 'p' || keyCode == 'P') {
				pause();
				return;
			}

			if (isPaused)
				return;

			switch (keyCode) {
			case KeyEvent.VK_LEFT:
				tryMove(curPiece, curX - 1, curY);
				break;
			case KeyEvent.VK_RIGHT:
				tryMove(curPiece, curX + 1, curY);
				break;
			case KeyEvent.VK_DOWN:
				tryMove(curPiece.rotateRight(), curX, curY);
				break;
			case KeyEvent.VK_UP:
				tryMove(curPiece.rotateLeft(), curX, curY);
				break;
			case KeyEvent.VK_SPACE:
				dropDown();
				break;
			case 'd':
				oneLineDown();
				break;
			case 'D':
				oneLineDown();
				break;
			}
		}
	}
}
