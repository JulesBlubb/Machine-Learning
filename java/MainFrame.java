package PingPong;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	public static final int	imageWidth		= 360;
	public static final int	imageHeight		= 360;
	public InputOutput		inputOutput		= new InputOutput(this);
	public boolean			stop			= false;
	ImagePanel				canvas			= new ImagePanel();
	ImageObserver			imo				= null;
	Image					renderTarget	= null;
	public int mousex,mousey,mousek;
	public int key;

	public MainFrame(String[] args) {
		super("PingPong");

		getContentPane().setSize(imageWidth, imageHeight);
		setSize(imageWidth + 50, imageHeight + 50);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		canvas.img = createImage(imageWidth, imageHeight);

		add(canvas);

		//run();
	}

    public double runAction(int xBall, int yBall, int xSchlaeger) {
            return (2.0*Math.random()-1.0);
        }

    public void learn(boolean reward) {
    }

	public void run() {

		int xBall=5, yBall=6, xSchlaeger=5, xV=1, yV=1;
		int score=0;

		while (!stop) {
			inputOutput.fillRect(0,0,imageWidth, imageHeight, Color.black);
			inputOutput.fillRect(xBall*30, yBall*30, 30, 30, Color.green);
			inputOutput.fillRect(xSchlaeger*30, 11*30+20, 90, 10, Color.orange);

			double action=runAction(xBall, yBall, xSchlaeger);//(2.0*Math.random()-1.0);
			if (action<-0.3){
				xSchlaeger--;
			}
			if (action>0.3){
				xSchlaeger++;
			}
			if (xSchlaeger<0){
				xSchlaeger=0;
			}
			if (xSchlaeger>9){
				xSchlaeger=9;
			}

			xBall+=xV;
			yBall+=yV;
			if (xBall>9 || xBall<1){
				xV=-xV;
			}
			if (yBall>10 || yBall<1){
				yV=-yV;
			}

			if (yBall==11){
				if (xSchlaeger==xBall || xSchlaeger==xBall-1 || xSchlaeger==xBall-2){
                                    learn(true);
					//positive reward
					System.out.println("positive reward");
				}
				else{
                                    learn(false);
					//negative reward
					System.out.println("negative reward");
				}
			}

			try {
			    Thread.sleep(100);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}

			repaint();
			validate();
		}

		setVisible(false);
                dispose();
	}

	public void mouseReleased(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
		mousek = e.getButton();
	}

	public void mousePressed(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
		mousek = e.getButton();
	}

	public void mouseExited(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
		mousek = e.getButton();
	}

	public void mouseEntered(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
		mousek = e.getButton();
	}

	public void mouseClicked(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
		mousek = e.getButton();
	}

	public void mouseMoved(MouseEvent e) {
		// System.out.println(e.toString());
		mousex = e.getX();
		mousey = e.getY();
		mousek = e.getButton();
	}

	public void mouseDragged(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
		mousek = e.getButton();
	}

	public void keyTyped(KeyEvent e) {
		key = e.getKeyCode();
	}

	public void keyReleased(KeyEvent e) {
		key = e.getKeyCode();
	}

	public void keyPressed(KeyEvent e) {
		System.out.println(e.toString());
	}

	/**
	 * Construct main frame
	 *
	 * @param args
	 *            passed to MainFrame
	 */
	public static void main(String[] args) {
		new MainFrame(args);
	}
}
