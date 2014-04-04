package com.raphaelkargon.cellularautomata;

import java.awt.Color;

/**
 * Implements a Brian's Brain cellular automaton, designed by Brian Silverman.
 * An off cell turns into a firing cell if it is surrounded by exactly two other
 * firing cells. A firing cell becomes 'dying' cell, and a dying cell becomes an
 * off cell
 * 
 * @author raphaelkargon
 * 
 */
public class BriansBrain implements CellularAutomaton {

	/*
	 * 0 -- Off/Dead cells are BLACK 1 -- Firing cells are RED 2 --
	 * Dying/Refractory cells are BLUE
	 */
	private Color[] cols = { Color.BLACK, Color.RED, Color.BLUE };
	private int gen=0;

	@Override
	public int getNumStates() {
		return 3;
	}

	@Override
	public Color getColor(int state) {
		return cols[state];
	}

	@Override
	public int evalCell(int[][] neighborhood) {
		int mid = neighborhood[1][1], numfiring = 0, i, j;
		if (mid == 1)
			return 2; // firing cells go to "dying"
		if (mid == 2)
			return 0; // dying cells go to "off"
		if (mid == 0) {
			
			//TODO replace this with simple sum
			for (i = 0; i < 3; i++) {
				for (j = 0; j < 3; j++) {
					if (neighborhood[i][j] == 1) {
						numfiring++;
						if (numfiring > 2)
							return 0;
					}
				}
			}
			if (numfiring == 2)
				return 1;
		}
		return 0;
	}

	@Override
	public int getNeighborSize() {
		return 1;
	}

	@Override
	public void incGeneration(int i) {
		gen += i;

	}

	@Override
	public int getGenerationNumber() {
		return gen;
	}
}
