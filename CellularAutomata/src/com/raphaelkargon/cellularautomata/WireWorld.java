package com.raphaelkargon.cellularautomata;

import java.awt.Color;

public class WireWorld implements CellularAutomaton {

	public Color[] cols;

	public WireWorld() {
		super();
		cols = new Color[] { Color.BLACK, new Color(15, 35, 255), Color.WHITE, new Color(249, 147, 0)};
	}

	@Override
	public int getNumStates() {
		return 4;
	}

	@Override
	public Color getColor(int state) {
		state %= 4;
		return cols[state];
	}

	@Override
	public int evalCell(int[][] neighborhood) {
		int mid = neighborhood[1][1];
		if (mid == 0)
			return 0;
		if (mid == 1)
			return 2;
		if (mid == 2)
			return 3;
		if(mid==3){
			//TODO
			int heads=0;
			for(int i=0; i<3; i++){
				for(int j=0; j<3; j++){
					if(neighborhood[i][j]==1){
						heads++;
						if(heads>2) return 3;
					}
				}
			}
			if(heads==1 || heads==2) return 1;
			return 3;
		}
		return 0;
	}

	@Override
	public int getNeighborSize() {
		return 1;
	}

}
