package model;

import java.util.ArrayList;
import java.util.Iterator;

public class Grid {

	private Actor[][] grid;

	public Grid(int gridLength, int gridHeight) {
		
		grid = new Actor[gridLength][gridHeight];

	}

	public void addActor(Actor actor) {
		Location loc = actor.getLocation();
		grid[loc.getX()][loc.getY()] = actor;
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < grid.length; i++) {
			
			for (int j = 0; j < grid[i].length; j++) {
				
				Actor actor = grid[i][j];
				
				if (actor != null) {
					
					sb.append(actor.toString()).append("\t");
					
				}
				else {
					
					sb.append(".").append("\t");
					
				}
			}
			sb.append("\n");
		}
		
		return sb.toString();
		
	}


}
