package com.raphaelkargon.cellularautomata;

import java.awt.Color;

public class ConwaysGameOfLife implements CellularAutomaton {

	private int gen=0;
	
	public ConwaysGameOfLife() {
		super();
	}
	
	@Override
	public int getNumStates() {
		return 2;
	}

	@Override
	public Color getColor(int state) {
		if(state==1) return Color.BLACK;
		else return Color.WHITE;
	}

	@Override
	// later, use this for variable neighborhood
	public int evalCell(int[][] n) {
		int neighborssum = n[0][0] + n[0][1] + n[0][2] + n[1][0] + n[1][2] + n[2][0] + n[2][1] + n[2][2];
		int center = n[1][1];
		
		if (center == 0 && neighborssum == 3)
			return 1;
		else if (center == 1 && (neighborssum == 2 || neighborssum == 3))
			return 1;
		else
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
