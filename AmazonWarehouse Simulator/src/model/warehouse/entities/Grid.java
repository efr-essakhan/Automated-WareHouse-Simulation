package model.warehouse.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.crypto.KeySelector.Purpose;

import model.warehouse.actors.Actor;
import model.warehouse.actors.Robot;

public class Grid {

	private Location[][] grid;

	public Grid(int rows, int columns) {

		grid = new Location[rows][columns]; //y,x

	}

	
	public void addActorsToGrid(List<Actor> actors) {
		
		for (Actor actor : actors) {
			
			Location loc = actor.getLocation();	
			
			Location orgloc = grid[loc.getY()][loc.getX()];
			
			if (orgloc != null) { //If location not occupied
				orgloc.mergeLocations(loc);
			}
			else {
				grid[loc.getY()][loc.getX()] = loc;
			}
			
			orgloc = grid[loc.getY()][loc.getX()];
			
			if (actor instanceof Robot) {
				Robot robot = (Robot) actor;
				orgloc.mergeLocations(robot.getChargingPod().getLocation());

			}
			
		}
		
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < grid.length; i++) {

			for (int j = 0; j < grid[i].length; j++) {

				Location loc = (Location) grid[i][j];
				
				if (loc != null) {
					for (Actor actor : loc.getActors()) {
						
						if (actor != null) {
							sb.append(actor.toString());
						}
					}	
					sb.append("\t");
				}
				else {

					sb.append(".").append("\t");

				}
	
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * Used for testing purposes in GridTest.Java
	 * @return
	 */
	public Location[][] getGrid() {
		return grid;
	}


}
