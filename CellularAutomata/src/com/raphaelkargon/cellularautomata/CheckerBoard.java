package com.raphaelkargon.cellularautomata;

import java.awt.Color;

public class CheckerBoard implements CellularAutomaton {
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
		return 2;
	}

	@Override
	public Color getColor(int state) {
		return (state == 1) ? Color.BLACK : Color.WHITE;
	}

	@Override
	public int evalCell(int[][] neighborhood) {
		int mid = neighborhood[1][1];
		int on_inf = 0;// how much influence for cell to be "on", determined by
						// neighbors

		// corners
		if (neighborhood[0][0] == 1)
			on_inf++;

		if (neighborhood[2][0] == 1)
			on_inf++;
		if (neighborhood[0][2] == 1)
			on_inf++;
		if (neighborhood[2][2] == 1)
			on_inf++;

		// sides
		if (neighborhood[0][1] == 0)
			on_inf++;
		if (neighborhood[1][0] == 0)
			on_inf++;
		if (neighborhood[1][2] == 0)
			on_inf++;
		if (neighborhood[2][1] == 0)
			on_inf++;

		if (on_inf > 4)
			return 1;
		else if (on_inf < 4)
			return 0;
		else
			return (int) (Math.random() * 2);// if could go both ways, random
												// change

	}

	@Override
	public int getNeighborSize() {
		return 1;
	}

}
