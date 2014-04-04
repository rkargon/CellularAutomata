package com.raphaelkargon.cellularautomata;

import java.awt.Color;

public class BoundedBox implements CAUniverse {

	private final static int DEFAULT_GRID_WIDTH = 200;
	private final static int DEFAULT_GRID_HEIGHT = 200;
	private int[][] grid, buffer;

	private CellularAutomaton alg;

	public BoundedBox() {
		this(DEFAULT_GRID_WIDTH, DEFAULT_GRID_HEIGHT, new ConwaysGameOfLife());
	}

	public BoundedBox(int width, int height) {
		this(width, height, new ConwaysGameOfLife());
	}

	public BoundedBox(CellularAutomaton alg) {
		this(DEFAULT_GRID_WIDTH, DEFAULT_GRID_HEIGHT, alg);
	}

	public BoundedBox(int width, int height, CellularAutomaton alg) {
		grid = new int[width][height];
		buffer = new int[width][height];
		this.alg = alg;
	}

	@Override
	public int[][] getGrid() {
		return grid;
	}

	@Override
	public void setGrid(int[][] newgrid) {
		this.grid = newgrid;
	}

	@Override
	public int getHeight() {
		return grid[0].length;
	}

	@Override
	public int getWidth() {
		return grid.length;
	}

	@Override
	public int getXOffset() {
		return 0;
	}

	@Override
	public int getYOffset() {
		return 0;
	}

	@Override
	public int getPoint(int x, int y) {
		if (x < 0 || x >= grid.length || y < 0 || y >= grid[0].length)
			return 0;
		else
			return grid[x][y];
	}

	@Override
	public Color getColorPoint(int x, int y) {
		return alg.getColor(this.getPoint(x, y));
	}

	@Override
	public void setPoint(int x, int y, int state) {
		if (x >= 0 && x < grid.length && y >= 0 && y < grid[0].length)
			grid[x][y] = state%alg.getNumStates();

	}

	@Override
	public void nextGeneration() {
		int i, j, di, dj, n = alg.getNeighborSize();
		int[][] neighborhood = new int[n * 2 + 1][n * 2 + 1];

		for (i = 0; i < grid.length; i++) {
			for (j = 0; j < grid[0].length; j++) {
				for (di = -n; di <= n; di++) {
					// copy neighborhood onto array, while using '0' for values
					// outside boundary
					for (dj = -n; dj <= n; dj++) {
						try {
							neighborhood[di + n][dj + n] = grid[i
									+ di][j + dj];
						} catch (ArrayIndexOutOfBoundsException e) {
							neighborhood[di + n][dj + n] = 0;
						}
					}

				}
				buffer[i][j] = alg.evalCell(neighborhood);
			}
		}
		
		int[][] tmp = grid;
		grid=buffer;
		buffer = tmp;
		alg.incGeneration(1);
	}

	@Override
	public void setAlgorithm(CellularAutomaton alg) {
		this.alg = alg;

	}

	@Override
	public int getStates() {
		return alg.getNumStates();
	}

}
