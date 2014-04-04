package com.raphaelkargon.cellularautomata;

import java.awt.Color;

/**
 * Implements a 2-dimensional "Born Survive" cellular automaton. The rule for
 * this CA is defined by two things: the number of neighboring "on" (
 * <code>1</code>) cells for a cell to be born (go from <code>0</code> to
 * <code>1</code>), and the number of neighboring "on" (<code>1</code>) cells
 * for a cell to survive (remain in state <code>1</code>)
 * 
 * For example Conway's Game of Life could be represented as B3/S23, where a
 * <code>0</code> cell becomes <code>1</code> if it has exactly three
 * <code>1</code> neighbors, and <code>1</code> cells remain <code>1</code> if
 * they have either two or three <code>1</code> neighbors
 * 
 * @author raphaelkargon
 * 
 */
public class BornSurviveCA implements CellularAutomaton {

	/**
	 * Binary bytes that represent the rule. The nth bit (starting from the
	 * small end) represents what happens when a cell is surrounded by n "on"
	 * neighbors
	 */
	private int born, survive, gen = 0;

	private int[] LUT;

	// by default, create Conway's Game of Life
	public BornSurviveCA() {
		this(1 << 3, (1 << 2) | (1 << 3));
	}

	public BornSurviveCA(int born, int survive) {
		this.born = born;
		this.survive = survive;
		genLUT();
	}

	public BornSurviveCA(int[] born, int[] survive) {
		for (int i = 0; i < born.length; i++)
			this.born |= (1 << born[i]);
		for (int i = 0; i < survive.length; i++)
			this.survive |= (1 << survive[i]);
		genLUT();
	}

	protected void genLUT(){
		LUT = new int[512];
		for (int i = 0; i < 512; i++) {
			int s = Integer.bitCount(i);
			int m = (i & 16) >> 4;
			if (m == 0 && ((this.born & 1 << s) != 0) || m == 1
					&& ((this.survive & 1 << (s - 1)) != 0))
			LUT[i]=1;
		}
	}
	
	@Override
	public int getNumStates() {
		return 2;
	}

	@Override
	public Color getColor(int state) {
		return (state == 1) ? Color.WHITE : Color.BLACK;
	}

	@Override
	public int evalCell(int[][] n) {
		return LUT[n[0][0] | (n[0][1] << 1) | (n[0][2] << 2) | (n[1][0] << 3)
					| (n[1][1] << 4) | (n[1][2] << 5) | (n[2][0] << 6)
					| (n[2][1] << 7) | (n[2][2] << 8)];
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
