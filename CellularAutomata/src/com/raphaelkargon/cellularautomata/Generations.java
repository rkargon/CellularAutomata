package com.raphaelkargon.cellularautomata;

import java.awt.Color;

public class Generations implements CellularAutomaton {

	private int born, survive, states, gen = 0;
	private Color[] cols;

	// by default, create Brian's Brain
	public Generations() {
		this(1 << 2, 0, 3);
	}

	public Generations(int born, int survive, int states) {
		this.born = born;
		this.survive = survive;
		this.states = states;
		genColors();
	}

	public Generations(int[] born, int[] survive, int states) {
		for (int i = 0; i < born.length; i++)
			this.born |= (1 << born[i]);
		for (int i = 0; i < survive.length; i++)
			this.survive |= (1 << survive[i]);

		this.states = states;
		genColors();
	}

	@Override
	public int getNumStates() {
		return states;
	}

	@Override
	public Color getColor(int state) {
		return cols[state];
	}

	@Override
	public int evalCell(int[][] neighborhood) {
		int mid = neighborhood[1][1], sum = 0, i, j;
		if (mid > 1) {
			mid++;
			return (mid < states) ? mid : 0;
		}

		for (i = 0; i < 3; i++) {
			for (j = 0; j < 3; j++) {
				if (neighborhood[i][j] == 1)
					sum++;
			}
		}
		if (mid == 1) {
			if ((survive & (1 << (sum - 1))) != 0)
				return 1;// sum-1 is used to account for middle square
			else
				return 2 % states;
		}
		if (mid == 0) {
			if ((born & (1 << sum)) != 0)
				return 1;
			else
				return 0;
		}
		return 0;
	}

	@Override
	public int getNeighborSize() {
		return 1;
	}

	public void genColors() {
		cols = new Color[states];
		cols[0] = Color.BLACK;
		for (int i = 1; i < states; i++) {
			cols[i] = new Color(255, (int) (255 * (double) i / (states - 1)), 0);
		}
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
