package com.raphaelkargon.cellularautomata;

import java.awt.Color;

public class LangtonsAnt implements CellularAutomaton {

	private Color[] cols = { Color.WHITE, Color.BLACK, Color.RED };
	private int gen=0;

	@Override
	public int getNumStates() {
		/*
		 * 0, 1 - Two states of tape, off and on, with no ant 2, 3, 4, 5 - Ant
		 * on 'off' cell, with directions, respectively, N, E, S, W 6, 7, 8, 9 -
		 * Ant on 'on' cell, with directions, respectively, N, E, S, W
		 */
		return 10;
	}

	@Override
	public Color getColor(int state) {
		if (state > 1)
			return cols[2];
		return (state == 1) ? cols[1] : cols[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.raphaelkargon.cellularautomata.CellularAutomaton#evalCell(int[][])
	 * 
	 * If ant lands on 'off' square, turn right, turn square on, and move
	 * forwards. If ant lands on 'on' square, turn left,
	 * 
	 * Ant collision rules: Cell checks for ants in clockwise direction, N, E,
	 * S, W. The check stops at the first ant found. Thus, ants can die
	 */
	@Override
	public int evalCell(int[][] neighborhood) {
		int mid = neighborhood[1][1];
		if (mid >= 2 && mid <= 5)
			mid = 1; // cell becomes on
		else if (mid >= 6 && mid <= 9)
			mid = 0; // cell becomes off

		// check for nearby ants, in clockwise order starting from north
		if (neighborhood[0][1] == 3 || neighborhood[0][1] == 9)
			return (mid == 0) ? 4 : 8; // check north
		if (neighborhood[1][2] == 4 || neighborhood[1][2] == 6)
			return (mid == 0) ? 5 : 9; // check east
		if (neighborhood[2][1] == 5 || neighborhood[2][1] == 7)
			return (mid == 0) ? 2 : 6; // check south
		if (neighborhood[1][0] == 2 || neighborhood[1][0] == 8)
			return (mid == 0) ? 3 : 7; // check west

		return mid;
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
