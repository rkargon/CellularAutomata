package com.raphaelkargon.cellularautomata;

import java.awt.Color;

public class CyclicCA implements CellularAutomaton {

	private int nstates, neighborhood_size;
	Color[] cols;
	
	public CyclicCA(){
		this(4, 1);
	}
	
	public CyclicCA(int nstates, int neighbors){
		super();
		this.nstates=nstates;
		this.neighborhood_size=neighbors;
		
		cols=new Color[nstates];
		for(int i=0; i<cols.length; i++) cols[i] = new Color((int)(255 * (double)i/nstates), (int)(255 * (double)i/nstates), (int)(255 * (double)i/nstates));
	}
	
	@Override
	public int getNumStates() {
		return nstates;
	}

	@Override
	public Color getColor(int state) {
		return cols[state%cols.length];
	}

	@Override
	public int evalCell(int[][] neighborhood) {
		int mid = neighborhood[neighborhood.length/2][neighborhood[0].length/2];
		
		for(int i=0; i<neighborhood.length; i++){
			for(int j=0; j<neighborhood[0].length; j++){
				if(neighborhood[i][j] == (mid+1)%nstates) return neighborhood[i][j];
			}
		}
		
		return mid;
	}


	@Override
	public int getNeighborSize() {
		//works with any size, really
		return neighborhood_size;
	}

}