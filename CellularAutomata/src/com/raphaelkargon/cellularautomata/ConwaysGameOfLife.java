package com.raphaelkargon.cellularautomata;

import java.awt.Color;
import java.util.Arrays;

public class ConwaysGameOfLife implements CellularAutomaton {

	private static int[] LUT = new int[512];

	//initialize lookup table with every possible neighbor state
	static {
		for (int i = 0; i < 511; i++) {
			int s = Integer.bitCount(i);
			if ((i & 16) != 0) {
				if (s == 3 || s == 4) LUT[i] = 1;
			}
			else if (s == 3) LUT[i] = 1;
			else LUT[i] = 0;
		}
	}

	private int gen = 0;

	public ConwaysGameOfLife() {
		super();
	}

	@Override
	public int getNumStates() {
		return 2;
	}

	@Override
	public Color getColor(int state) {
		if (state == 1) return Color.BLACK;
		else return Color.WHITE;
	}

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
