package com.raphaelkargon.cellularautomata;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JFrame;

import com.raphaelkargon.*;

public class CAMain {
	
	public static void main(String[] args) {
		JFrame win = new JFrame("Cellular Automata");
		win.setSize(600, 600);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container pane = win.getContentPane();
		
		CAPanel z = new CAPanel(-100.0, 100.0, 1.0, 2.0);
		pane.add(z);
		z.setBackground(Color.WHITE);
		
		//set up keyboard focus
		z.setFocusable(true);
		z.requestFocusInWindow();
		
		z.paintCA();
		
		win.setVisible(true);
		
	}

}
