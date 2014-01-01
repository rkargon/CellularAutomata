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
	private int born, survive;

	//by default, create Conway's Game of Life
	public BornSurviveCA() {
		this(1 << 3, (1 << 2) | (1 << 3));
	}

	public BornSurviveCA(int born, int survive) {
		this.born = born;
		this.survive = survive;
	}

	public BornSurviveCA(int[] born, int[] survive) {
		for (int i = 0; i < born.length; i++)
			this.born |= (1 << born[i]);
		for (int i = 0; i < survive.length; i++)
			this.survive |= (1 << survive[i]);
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
	public int evalCell(int[][] neighborhood) {
		int sum = neighborhood[0][0] + neighborhood[0][1] + neighborhood[0][2]
				+ neighborhood[1][0] + neighborhood[1][2] + neighborhood[2][0]
				+ neighborhood[2][1] + neighborhood[2][2];
		int mid = neighborhood[1][1];

		if ((mid == 1 && (survive & (1 << sum)) != 0)
				|| (mid == 0 && (born & (1 << sum)) != 0))
			return 1;

		return 0;
	}

	@Override
	public int getNeighborSize() {
		return 1;
	}

}
