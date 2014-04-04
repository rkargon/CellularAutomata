package com.raphaelkargon.cellularautomata;

import java.awt.Color;

public class InfiniteArray implements CAUniverse {

	private static final int DEFAULT_GRID_WIDTH = 200,
			DEFAULT_GRID_HEIGHT = 200;

	private int[][] grid, buffer;
	private int xoffset = 0, yoffset = 0; // the x,y position of grid[0][0]
	private int xmin, xmax, ymin, ymax; //in array coordinates, represent bounding box of existing living cells

	private CellularAutomaton alg;

	public InfiniteArray() {
		this(DEFAULT_GRID_WIDTH, DEFAULT_GRID_HEIGHT, new ConwaysGameOfLife());
	}

	public InfiniteArray(int width, int height) {
		this(width, height, new ConwaysGameOfLife());
	}

	public InfiniteArray(CellularAutomaton alg) {
		this(DEFAULT_GRID_WIDTH, DEFAULT_GRID_HEIGHT, alg);
	}

	public InfiniteArray(int width, int height, CellularAutomaton alg) {
		grid = new int[width][height];
		buffer = new int[width][height];
		this.alg = alg;
	}

	@Override
	/**
	 * Global coords
	 */
	public int getPoint(int x, int y) {
		if (x < xoffset || x >= grid.length + xoffset || y < yoffset
				|| y >= grid[0].length + yoffset) return 0;
		return grid[x - xoffset][y - yoffset];
	}

	@Override
	/**
	 * Uses global coords, not array coordinates
	 */
	public void setPoint(int x, int y, int state) {
		while (x < xoffset || x >= grid.length + xoffset || y < yoffset
				|| y >= grid[0].length + yoffset)
			expand();
		grid[x - xoffset][y - yoffset] = state % alg.getNumStates();

		//updates bounding rectangle
		if (x - xoffset < xmin) xmin = x - xoffset;
		else if (x - xoffset > xmax) xmax = x - xoffset;
		if (y - yoffset < ymin) ymin = y - yoffset;
		else if (y - yoffset > ymax) ymax = y - yoffset;

	}

	/**
	 * @return the width of the grid
	 */
	@Override
	public int getWidth() {
		return grid.length;
	}

	/**
	 * @return the height of the grid
	 */
	@Override
	public int getHeight() {
		return grid[0].length;
	}

	@Override
	public void nextGeneration() {
		int n = alg.getNeighborSize(), di, dj;
		int[][] neighborhood = new int[n][n];

		//makes sure bounding box of cells  +- 1 is not on array border
		while (xmin - 2 <= 0 || ymin - 2 <= 0 || xmax + 2 >= grid.length
				|| ymax + 2 >= grid[0].length)
			expand();

		//create new grid
		int newxmin = xmin, newxmax = xmax, newymin = ymin, newymax = ymax;

		//update cells in bounding box and a surrounding border. 
		for (int i = xmin - 1; i <= xmax + 1; i++) {
			for (int j = ymin - 1; j <= ymax + 1; j++) {
				//update grid cell
				if (n > 1) {
					//neighbor size > 1
					for (di = -n; di <= n; di++) {
						// copy neighborhood onto array
						for (dj = -n; dj <= n; dj++) {
							neighborhood[di + n][dj + n] = grid[i + di][j + dj];
						}
					}
					buffer[i][j] = alg.evalCell(neighborhood);
				}
				else {
					buffer[i][j] = alg.evalCell(new int[][] {
							{ grid[i - 1][j - 1], grid[i - 1][j],
									grid[i - 1][j + 1] },
							{ grid[i][j - 1], grid[i][j], grid[i][j + 1] },
							{ grid[i + 1][j - 1], grid[i + 1][j],
									grid[i + 1][j + 1] } });
				}
			}
		}

		//check top boundary for stretching
		for (int i = xmin - 1; i <= xmax + 1; i++) {
			if (buffer[i][ymin - 1] > 0) {
				newymin = ymin - 1;
				break;
			}
		}

		//check bottom boundary for stretching
		for (int i = xmin - 1; i <= xmax + 1; i++) {
			if (buffer[i][ymax + 1] > 0) {
				newymax = ymax + 1;
				break;
			}
		}

		//check left boundary for stretching
		for (int j = ymin - 1; j <= ymax + 1; j++) {
			if (buffer[xmin - 1][j] > 0) {
				newxmin = xmin - 1;
				break;
			}
		}

		//check right boundary for stretching
		for (int j = ymin - 1; j <= ymax + 1; j++) {
			if (buffer[xmax + 1][j] > 0) {
				newxmax = xmax + 1;
				break;
			}
		}

		xmin = newxmin;
		xmax = newxmax;
		ymin = newymin;
		ymax = newymax;

		//update grid
		int[][] tmp = grid;
		grid = buffer;
		buffer = tmp;

		alg.incGeneration(1);
	}

	public void expand() {
		int width = grid.length, height = grid[0].length; //the dimensions of the original grid
		int[][] newgrid = new int[width * 2][height * 2];
		int[][] newgrid_b = new int[width * 2][height * 2]; //the buffer

		System.out.println("expanding, " + width * 2 + ", " + height * 2);

		//copy grid
		for (int i = 0; i < width; i++) {
			System.arraycopy(grid[i], 0, newgrid[i + width / 2], height / 2, height);
			System.arraycopy(grid[i], 0, newgrid_b[i + width / 2], height / 2, height);
		}

		//update offset, so array expands equal on all sides of grid
		xoffset -= width / 2;
		yoffset -= height / 2;

		xmin += width / 2;
		xmax += width / 2;
		ymin += height / 2;
		ymax += width / 2;

		grid = newgrid;
		buffer = newgrid_b;
	}

	@Override
	public int[][] getGrid() {
		return grid;
	}

	@Override
	public void setGrid(int[][] newgrid) {
		grid = newgrid;
	}

	@Override
	public int getXOffset() {
		return xoffset;
	}

	@Override
	public int getYOffset() {
		return yoffset;
	}

	@Override
	public void setAlgorithm(CellularAutomaton alg) {
		if (this.alg.getNumStates() > alg.getNumStates()) {
			for (int i = 0; i < grid.length; i++) {
				for (int j = 0; j < grid[0].length; j++) {
					grid[i][j] %= alg.getNumStates();
				}
			}
		}

		this.alg = alg;
	}

	@Override
	public Color getColorPoint(int x, int y) {
		return alg.getColor(getPoint(x, y));
	}

	@Override
	public int getStates() {
		return alg.getNumStates();
	}

}
