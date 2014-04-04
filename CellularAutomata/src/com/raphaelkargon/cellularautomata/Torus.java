package com.raphaelkargon.cellularautomata;

import java.awt.Color;

public class Torus implements CAUniverse {

	private final static int DEFAULT_GRID_WIDTH = 300;
	private final static int DEFAULT_GRID_HEIGHT = 300;
	private int[][] grid, buffer;

	private CellularAutomaton alg;

	public Torus() {
		this(DEFAULT_GRID_WIDTH, DEFAULT_GRID_HEIGHT, new ConwaysGameOfLife());
	}

	public Torus(int width, int height) {
		this(width, height, new ConwaysGameOfLife());
	}

	public Torus(CellularAutomaton alg) {
		this(DEFAULT_GRID_WIDTH, DEFAULT_GRID_HEIGHT, alg);
	}

	public Torus(int width, int height, CellularAutomaton alg) {
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

	/**
	 * Torus grid: when point reaches the boundary, it goes to the opposite
	 * side. grid[-1][-1] is grid[xmax][ymax]
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.raphaelkargon.cellularautomata.CAUniverse#getPoint(int, int)
	 */
	@Override
	public int getPoint(int x, int y) {
		if (x < 0)
			x = (grid.length + (x % grid.length)) % grid.length;
		else if (x >= grid.length)
			x %= grid.length;
		if (y < 0)
			y = (grid[0].length + (y % grid[0].length)) % grid[0].length;
		else if (y >= grid[0].length)
			y %= grid[0].length;

		return grid[x][y];
	}

	@Override
	public Color getColorPoint(int x, int y) {
		return alg.getColor(this.getPoint(x, y));
	}

	@Override
	public void setPoint(int x, int y, int state) {
		if (x < 0)
			x = (grid.length + (x % grid.length)) % grid.length;
		else if (x >= grid.length)
			x %= grid.length;
		if (y < 0)
			y = (grid[0].length + (y % grid[0].length)) % grid[0].length;
		else if (y >= grid[0].length)
			y %= grid[0].length;

		grid[x][y] = state%alg.getNumStates();
	}

	@Override
	public void nextGeneration() {
		int[][] neighborhood = new int[alg.getNeighborSize() * 2 + 1][alg
				.getNeighborSize() * 2 + 1];
		int i, j, di, dj, tmpi, tmpj, neighborsize = alg.getNeighborSize();

		for (i = 0; i < grid.length; i++) {
			for (j = 0; j < grid[0].length; j++) {
				for (di = -neighborsize; di <= neighborsize; di++) {
					// copy neighborhood onto array
					for (dj = -neighborsize; dj <= neighborsize; dj++) {
						try {
							neighborhood[di + neighborsize][dj + neighborsize] = grid[i
									+ di][j + dj];
						} catch (ArrayIndexOutOfBoundsException e) {
							tmpi = (grid.length + ((i+di) % grid.length)) % grid.length;
							tmpj = (grid[0].length + ((j+dj) % grid[0].length)) % grid[0].length;
							neighborhood[di + neighborsize][dj + neighborsize] = grid[(tmpi)][(tmpj)];
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
