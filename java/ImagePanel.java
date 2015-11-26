package PingPong;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel {

	public Image			img				= null;
	public ImageObserver	imageObserver	= null;

	public void paint(Graphics g) {
		super.paint(g);

		g.drawImage(img, 0, 0, imageObserver);
	}
}
