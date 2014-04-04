package com.raphaelkargon.cellularautomata;

import java.awt.Color;

public class BLMTrafficModel implements CellularAutomaton {

	/*
	 * 0 - empty
	 * 1 - car moves right
	 * 2 - car moves down
	 */
	private Color[] cols = { Color.WHITE, Color.RED, Color.BLUE };
	private int gen = 0;

	@Override
	public void incGeneration(int i) {
		gen += i;
	}

	@Override
	public int getGenerationNumber() {
		return gen;
	}

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
		int mid = neighborhood[1][1];
		if(gen%2==0){
			if(mid==0 && neighborhood[0][1]==1) return 1;
			if(mid==1 && neighborhood[2][1]==0) return 0;
			if(mid==2) return 2;
		}
		else if(gen%2==1){
			if(mid==0 && neighborhood[1][2]==2) return 2;
			if(mid==1) return 1;
			if(mid==2 && neighborhood[1][0]==0) return 0;
		}
		return mid;
	}

	@Override
	public int getNeighborSize() {

		return 1;
	}

}
