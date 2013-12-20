package com.raphaelkargon.cellularautomata;

import java.awt.Color;

/**
 * Represents the grid for a cellular automaton.
 * 
 * @author Raphael Kargon
 *
 */
public interface CAUniverse {
	
	
	/**
	 * Converts a universe into a 2d array of grid cells
	 * 
	 * @return a 2d array representing the cells in the universe.
	 */
	public int[][] getGrid();
	
	public void setGrid(int[][] newgrid);
	
	public int getHeight();
	
	public int getWidth();	
	
	public int getXOffset();
	
	public int getYOffset();
	
	public int getPoint(int x, int y);
	
	public Color getColorPoint(int x, int y);
	
	public void setPoint(int x, int y, int state);
	
	public void nextGeneration();
	
	public void setAlgorithm(CellularAutomaton alg);
	
	public String toString();
	
}
