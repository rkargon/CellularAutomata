package com.raphaelkargon.cellularautomata;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class CAPanel extends JPanel {

	/* display data */
	private double zoomfactor;
	private AffineTransform xyToPixel;
	public boolean antiAliasing = true;

	/**
	 * Determines the edit mode of the panel. This is changed when the user
	 * decides to zoom, edit the cells, select, pan, etc.
	 * 
	 * 0 = zoom 1 = edit 2 = pan 3 = select
	 * 
	 * <code>prevmode</code> stores tahe previous mode, so user can easily
	 * switch
	 * back and forth.
	 */
	private int mode = 0;//TODO implement all the modes
	private int prevmode = 1; // user can switch to edit by default, will not be necessary when GUI is built

	/* brush data */
	private int brushstate = 1; // current state the the brush paints on the grid
	private int tmpbrushstate = brushstate;
	private int previous_x, previous_y; // for interpolating between brush strokes

	private Timer timer;
	private Cursor zoomcursor;
	
	/* Cellular automaton data */
	private CAUniverse ca;

	public CAPanel(double xmin, double ymax, double delta, double zoomfactor) {
		if (java.lang.Double.isInfinite(delta) || java.lang.Double.isNaN(delta)
				|| java.lang.Double.isInfinite(zoomfactor)
				|| java.lang.Double.isNaN(zoomfactor))
			throw new IllegalArgumentException("Zoom factor and delta ("
					+ zoomfactor + ", " + delta
					+ ") must be nonzero real numbers.");

		//set up transform from Cartesian coordinates to pixel coordinates
		xyToPixel = new AffineTransform();
		xyToPixel.scale(1.0 / delta, -1.0 / delta);
		xyToPixel.translate(-xmin, -ymax);
		this.zoomfactor = zoomfactor;

		addListeners();

		timer = new Timer(0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ca.nextGeneration();
				repaint();
			}
		});

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image zoomimage = toolkit.getImage("resources/cursors/zoom.png");
		zoomcursor = toolkit
				.createCustomCursor(zoomimage, new Point(5, 5), "zoomcursor");

		ca = new InfiniteArray(800, 800, new ConwaysGameOfLife());
		this.setBackground(ca.getColorPoint(0, 0)); // set background
		for (int i = 0; i < ca.getWidth(); i++) {
			for (int j = 0; j < ca.getHeight(); j++) {
				ca.setPoint(i, j, (int)(Math.random()*ca.getStates()));
			}
		}
		
		repaint();
	}

	public CAPanel() {
		this(-1, -1, 200, 2);
	}

	/**
	 * Adds mouse and keyboard listeners for the panel
	 */
	protected void addListeners() {
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				switch (mode) {
				case 1:
					Point2D p;
					try {
						p = getXYCoordinates(e.getPoint());
					}
					catch (NoninvertibleTransformException e1) {
						e1.printStackTrace();
						return;
					}
					int x = (int) (Math.floor(p.getX())),
					y = (int) (Math.floor(p.getY()));
					tmpbrushstate = brushstate;
					ca.setPoint(x, y, (ca.getPoint(x, y) != 0) ? 0 : brushstate);
					repaint();
					break;
				}
			}

			public void mousePressed(MouseEvent e) {
				switch (mode) {
				case 0:
					// left click, zoom in
					if (e.getButton() == MouseEvent.BUTTON1) {
						zoom(e.getPoint(), true);
					}
					// right click, zoom out
					else if (e.getButton() == MouseEvent.BUTTON3) {
						zoom(e.getPoint(), false);
					}
					// middle click, center origin
					else if (e.getButton() == MouseEvent.BUTTON2) {
						setOrigin(new Point2D.Double(getWidth() / 2, getHeight() / 2));
						repaint();
					}
					break;
				case 1:

					Point2D p;
					try {
						p = getXYCoordinates(e.getPoint());
					}
					catch (NoninvertibleTransformException e1) {
						e1.printStackTrace();
						return;
					}
					int x = (int) (Math.floor(p.getX())),
					y = (int) (Math.floor(p.getY()));

					tmpbrushstate = brushstate;
					brushstate = (ca.getPoint(x, y) != 0) ? 0 : brushstate;
					previous_x = x;
					previous_y = y;
					break;
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (mode == 1) brushstate = tmpbrushstate;
			}
		});

		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {

				//if brush is dragged past edge of screen
				if (e.getX() > getWidth()) translatePixels(new Point2D.Double(10, 0));
				else if (e.getX() < 0)
					translatePixels(new Point2D.Double(-10, 0));
				if (e.getY() > getHeight()) translatePixels(new Point2D.Double(0, 10));
				else if (e.getY() < 0)
					translatePixels(new Point2D.Double(0, -10));

				switch (mode) {
				case 1:

					Point2D p;
					try {
						p = getXYCoordinates(e.getPoint());
					}
					catch (NoninvertibleTransformException e1) {
						e1.printStackTrace();
						return;
					}
					int x = (int) (Math.floor(p.getX())),
					y = (int) (Math.floor(p.getY()));

					drawLine(brushstate, previous_x, previous_y, x, y);
					repaint();

					previous_x = x;
					previous_y = y;
					break;
				}
			}
		});

		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					if (timer.isRunning()) timer.stop();
					else if (e.isShiftDown()) {
						timer.start();
						return;
					}
					ca.nextGeneration();
					repaint();
				}
				else if (e.getKeyChar() == 'a') {
					antiAliasing = !antiAliasing;
					repaint();
				}
				else if (e.getKeyChar() == 'z') {
					if (mode == 0) {
						mode = prevmode;
						prevmode = 0;
						// setCursor(zoomcursor);
					}
					else {
						prevmode = mode;
						mode = 0;
					}
				}
				else if (e.getKeyCode() >= KeyEvent.VK_0
						&& e.getKeyCode() <= KeyEvent.VK_9) {
					brushstate = (e.getKeyCode() - 48) % ca.getStates();
				}
			}
		});
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform t_old = g2d.getTransform();

		double delta = 1.0 / xyToPixel.getScaleX();
		if (antiAliasing && delta > 1)
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.transform(xyToPixel);
		int[][] grid = ca.getGrid();
		for (int i = 0; i < ca.getWidth(); i++) {
			for (int j = 0; j < ca.getHeight(); j++) {
				if (grid[i][j] != 0) {
					g2d.setColor(ca.getColorPoint(i + ca.getXOffset(), j
							+ ca.getYOffset()));
					g2d.fill((new Rectangle(i + ca.getXOffset(), j
							+ ca.getYOffset(), 1, 1)));
				}
			}
		}

		// draw grid if zoomed in 4X or more
		if (delta <= 0.0625) {
			g2d.setTransform(t_old);
			g2d.setColor(Color.LIGHT_GRAY);
			Point2D gridcorner;
			Point2D gc_pixels;
			try {
				gridcorner = getXYCoordinates(new Point2D.Double(0, 0));
				gc_pixels = getPixelCoordinates(new Point2D.Double(Math.floor(gridcorner
						.getX()), Math.floor(gridcorner.getY())));
			}
			catch (NoninvertibleTransformException e) {
				e.printStackTrace();
				return;
			}

			double dxy = 1.0 / delta;

			//vertical lines
			for (double i = gc_pixels.getX(); i <= getWidth(); i += dxy) {
				g2d.draw(new Line2D.Double(i, 0, i, getHeight()));
			}
			//horizontal lines
			for (double j = gc_pixels.getY(); j <= getHeight(); j += dxy) {
				g2d.draw(new Line2D.Double(0, j, getWidth(), j));
			}
		}

	}

	public void zoom(Point2D zoomcenter, boolean isZoomingIn) {
		double actualZoomFactor = isZoomingIn ? zoomfactor : 1.0 / zoomfactor;
		// get x,y coordinates of zoom center (the point the user clicked on)
		Point2D zcentercoords;
		try {
			zcentercoords = getXYCoordinates(zoomcenter);
		}
		catch (NoninvertibleTransformException e) {
			e.printStackTrace();
			return;
		}
		// scale according to zoom factor, scaling is centered around origin
		xyToPixel.scale(actualZoomFactor, actualZoomFactor);
		Point2D newzoompixel = getPixelCoordinates(zcentercoords);
		// move screen so that the zoom center is on the same pixel it was when
		// the user clicked on it.
		xyToPixel.translate((zoomcenter.getX() - newzoompixel.getX())
				/ xyToPixel.getScaleX(), (zoomcenter.getY() - newzoompixel
				.getY()) / xyToPixel.getScaleY());
		repaint();
	}

	/* TRANSFORM MANIPULATION / GEOM THINGIES */

	/**
	 * Fills in cells along a specified line (using x,y coordinates)
	 * 
	 * @param x1
	 *            the x coordinate of one endpoint
	 * @param y1
	 *            the y coordinate of one endpoint
	 * @param x2
	 *            the x coordinate of one endpoint
	 * @param y2
	 *            the y coordinate of the second endpoint
	 */
	public void drawLine(int state, int x1, int y1, int x2, int y2) {
		/* DRAW LINE */
		double dx = x2 - x1, dy = y2 - y1;
		double length = Math.abs(dx) + Math.abs(dy); // length in squares, which
														// is sum of width and
														// height. Not actual
														// geometric length
		for (int i = 0; i <= length; i++) {
			ca.setPoint((int) (x1 + dx * i / length), (int) (y1 + dy * i
					/ length), state);
		}

	}

	public Point2D getOrigin() {
		return getPixelCoordinates(new Point2D.Double(0, 0));
	}

	public Point2D getPixelCoordinates(Point2D xypoint) {
		return xyToPixel.transform(xypoint, null);
	}

	public Point2D getXYCoordinates(Point2D pixel)
			throws NoninvertibleTransformException {
		return xyToPixel.inverseTransform(pixel, null);
	}

	public void translatePixels(Point2D move) {
		Point2D origin = getOrigin();
		setOrigin(new Point2D.Double(origin.getX() - move.getX(), origin.getY()
				- move.getY()));
	}

	public void setOrigin(Point2D neworigin) {
		xyToPixel.translate((neworigin.getX() - xyToPixel.getTranslateX())
				/ xyToPixel.getScaleX(), (neworigin.getY() - xyToPixel
				.getTranslateY()) / xyToPixel.getScaleY());
	}

	public static void main(String[] args) {
		JFrame win = new JFrame("Cellular Automata");
		win.setSize(600, 600);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container pane = win.getContentPane();

		CAPanel z = new CAPanel(-100.0, 100.0, 1.0, 2.0);
		pane.add(z);
		//z.setBackground(Color.BLACK);

		//set up keyboard focus
		z.setFocusable(true);
		z.requestFocusInWindow();

		win.setVisible(true);

	}
}
