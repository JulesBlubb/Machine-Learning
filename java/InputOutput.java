package PingPong;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputOutput {

	public MainFrame	mainFrame	= null;
	Graphics			graphics	= null;

	public InputOutput(MainFrame frame) {
		this.mainFrame = frame;

		this.mainFrame.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				mainFrame.mouseReleased(e);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				mainFrame.mousePressed(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				mainFrame.mouseExited(e);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				mainFrame.mouseEntered(e);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				mainFrame.mouseClicked(e);
			}
		});

		this.mainFrame.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				mainFrame.mouseMoved(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				mainFrame.mouseDragged(e);
			}
		});

		this.mainFrame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				mainFrame.keyTyped(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				mainFrame.keyReleased(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				mainFrame.keyPressed(e);
			}
		});
	}

	public synchronized void drawPixel(int x, int y, Color color) {
		if (graphics == null)
			graphics = mainFrame.canvas.img.getGraphics();

		graphics.setColor(color);
		graphics.fillRect(x, y, 1, 1);

	}

	public synchronized void fillRect(int x, int y, int width, int height,
			Color color) {
		if (graphics == null)
			graphics = mainFrame.canvas.img.getGraphics();

		graphics.setColor(color);
		graphics.fillRect(x, y, width, height);
	}
}
