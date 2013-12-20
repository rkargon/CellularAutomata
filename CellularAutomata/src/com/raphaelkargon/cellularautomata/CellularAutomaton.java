package com.raphaelkargon.cellularautomata;

import java.awt.Color;

/**
 * Represents a cellular automaton algorithm.
 * 
 * @author Raphael Kargon
 *
 */
public interface CellularAutomaton {
	
	public int getNumStates();
	
	public Color getColor(int state);
	
	public int evalCell(int[][] neighborhood);
	
	public int getNeighborSize();
	
	public String toString();
}
