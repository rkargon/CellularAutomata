package com.raphaelkargon.cellularautomata;

import java.awt.Color;

public class CyclicCA implements CellularAutomaton {

	private int nstates, neighborhood_size, gen=0;
	Color[] cols;
	
	public CyclicCA(){
		this(4, 1);
	}
	
	public CyclicCA(int nstates, int neighbors){
		super();
		this.nstates=nstates;
		this.neighborhood_size=neighbors;
		
		cols=new Color[nstates];
		for(int i=0; i<cols.length; i++) cols[i] = new Color((int)(255 * (double)i/(nstates-1)), (int)(255 * (double)i/(nstates-1)), (int)(255 * (double)i/(nstates-1)));
	}
	
	@Override
	public int getNumStates() {
		return nstates;
	}

	@Override
	public Color getColor(int state) {
		return cols[state];
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
	@Override
	public void incGeneration(int i) {
		gen += i;

	}

	@Override
	public int getGenerationNumber() {
		return gen;
	}
}
