package com.raphaelkargon.cellularautomata;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import com.raphaelkargon.ZoomablePanel;


public class CAPanel extends ZoomablePanel {
	
	/**
	 * Determines the edit mode of the panel.
	 * This is changed when the user decides to zoom, edit the cells, select, pan, etc.
	 * 
	 * 0 = zoom
	 * 1 = edit
	 * 2 = pan
	 * 3 = select
	 * 
	 * <code>prevmode</code> stores the previous mode, so user can easily switch back and forth.
	 */
	private int mode=0;
	private int prevmode=1; //user can switch to edit by default, will not be necessary when GUI is built
	
	private CAUniverse ca;

	private int brushstate; //current state the the brush paints on the grid
	private int previous_x, previous_y; //for interpolating between brush strokes
	
	private Toolkit toolkit;
	private Cursor zoomcursor;
	
	//cursors
	//private Cursor zoom = new Cursor(Cursor.CUSTOM_CURSOR);
	
	public CAPanel()
	{
		this(-1, -1, 200, 2);
	}
	
	public CAPanel(double xmin, double ymax, double delta, double zoomfactor) {
		super(xmin, ymax, delta, zoomfactor);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image zoomimage = toolkit.getImage("resources/cursors/zoom.png");
		zoomcursor = toolkit.createCustomCursor(zoomimage, new Point(5, 5),  "zoomcursor");
		
		
		ca = new InfiniteArray(new WireWorld());
		this.setBackground(ca.getColorPoint(0, 0)); //set background
		
		for(int i=0; i<200; i++){
			for(int j=0; j<200; j++){
				ca.setPoint(i, j, (int)(Math.random()*ca.getStates()));
			}
		}
		
	}

	protected void addListeners(){
		this.addMouseListener(new CAMouseAdapter());
		this.addMouseMotionListener(new CAMouseMotionAdapter());
		this.addKeyListener(new CAKeyAdapter());
	}
	
	/**
	 * Fills in cells along a specified line (using x,y coordinates)
	 * 
	 * @param x1 the x coordinate of one endpoint
	 * @param y1 the y coordinate of one endpoint
	 * @param x2 the x coordinate of one endpoint
	 * @param y2 the y coordinate of the second endpoint
	 */
	public void drawLine(int state, int x1, int y1, int x2, int y2)
	{
		/* DRAW LINE */
		double dx = x2-x1, dy=y2-y1;
		double length = Math.abs(dx)+Math.abs(dy); //length in squares, which is sum of width and height. Not actual geometric length
		for(int i=0; i<=length; i++){
			ca.setPoint((int)(x1+dx*i/length), (int)(y1+dy*i/length), state);
		}
		
	}
	
	public void paintCA()
	{
		this.clearShapes();
		
		int[][] grid = ca.getGrid();
		for(int i=0; i<ca.getWidth(); i++){
			for(int j=0; j<ca.getHeight(); j++){
				if(grid[i][j]!=0) this.addShape(new Rectangle(i+ca.getXOffset(),  j+ca.getYOffset(), 1, 1), ca.getColorPoint(i+ca.getXOffset(),  j+ca.getYOffset()), true);
			}
		}
		repaint();
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		
		//draw grid if zoomed in 4X or more
		if(this.getDelta()<=0.125){
			g2d.setColor(Color.LIGHT_GRAY);
			double delta=this.getDelta(); //x,y coordinates per pixel
			int min_x = (int)getXYCoordinates(new Point2D.Double(0, 0)).x-1; //starts from behind panel, to ensure all grid lines are drawn
			int min_y = (int)getXYCoordinates(new Point2D.Double(0, 0)).y+1; //this is +1 and not -1 to convert from cartesian to panel coordinates (pane has y axis reversed)
			int left = (int)getPixelCoordinates(new Point2D.Double(min_x, min_y)).x;
			int top = (int)getPixelCoordinates(new Point2D.Double(min_x, min_y)).y;

			int width=this.getWidth(), height=this.getHeight();
			for(double i=left; i<=width; i+=(1.0/delta)){
				g2d.draw(new Line2D.Double(i, top, i, height));
			}
			for(double j=top; j<=height; j+=(1.0/delta)){
				g2d.draw(new Line2D.Double(left, j, width, j));
			}
		}
		
		//g2d.setColor(Color.BLACK);
		//g2d.draw(xyToPixel.createTransformedShape(new Rectangle(cgl.getXOffset(), cgl.getYOffset(), cgl.getWidth(), cgl.getHeight())));
		
	}
	
	private class CAMouseAdapter extends ViewMouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			switch(mode){
			case 1:
				Point2D.Double p = getXYCoordinates(new Point2D.Double(e.getX(), e.getY()));
				int x=(int)(Math.floor(p.x)), y=(int)(Math.floor(p.y));
				ca.setPoint(x, y, (ca.getPoint(x, y)==1) ? 0 : 1);
				paintCA();
				break;
			}
		}
		
		public void mousePressed(MouseEvent e)
		{
			switch(mode){
			case 0:
				super.mouseClicked(e);
				break;
			case 1:
				Point2D.Double p = getXYCoordinates(new Point2D.Double(e.getX(), e.getY()));
				int x=(int)(Math.floor(p.x)), y=(int)(Math.floor(p.y));
				brushstate = (ca.getPoint(x, y)==1) ? 0 : 1;
				previous_x = x;
				previous_y = y;
				break;
			}
		}
		
		public void mouseReleased(MouseEvent e)
		{
			brushstate=1;
		}
	}
	
	private class CAMouseMotionAdapter extends MouseMotionAdapter
	{
		
		public void mouseDragged(MouseEvent e)
		{
			if(e.getX() > getWidth()) translatePixels(new Point(10, 0));
			else if(e.getX()<0) translatePixels(new Point(-10, 0));
			if(e.getY() > getHeight()) translatePixels(new Point(0, 10));
			else if(e.getY()<0) translatePixels(new Point(0, -10));
			
			switch(mode){
			case 1:
				Point2D.Double p = getXYCoordinates(new Point2D.Double(e.getX(), e.getY()));
				int x=(int)(Math.floor(p.x));
				int y=(int)(Math.floor(p.y));
				
				drawLine(brushstate, previous_x, previous_y, x, y);
				paintCA();
				
				previous_x=x;
				previous_y=y;
				break;
			}
		}
	}
	
	private class CAKeyAdapter extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			if(e.getKeyCode()==KeyEvent.VK_SPACE){
				ca.nextGeneration();
				paintCA();
			}
			else if(e.getKeyChar()=='z'){
				if(mode==0){
					mode=prevmode;
					prevmode=0;
					//setCursor(zoomcursor);
				}
				else{
					prevmode=mode;
					mode=0;
				}
			}
		}
	}
}
