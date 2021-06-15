package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Grid {

	private Location[][] grid;

	public Grid(int gridLength, int gridHeight) {

		grid = new Location[gridLength][gridHeight];

	}

//	public void addActor(Actor actor) {
//		
//		Location loc = actor.getLocation();
//		grid[loc.getX()][loc.getY()] = actor;
//	}
	
	public void addLocationToGrid(Location loc) {
		Location orgloc = grid[loc.getX()][loc.getY()];
		
		if (orgloc != null) {
			orgloc.mergeLocations(loc);
		}
		else {
			grid[loc.getX()][loc.getY()] = loc;
		}
		
	}
	
	public void addActorsToGrid(List<Actor> actors) {
		
		for (Actor actor : actors) {
			
			Location loc = actor.getLocation();
			
			Location orgloc = grid[loc.getX()][loc.getY()];
			
			if (orgloc != null) { //If location not occupied
				orgloc.mergeLocations(loc);
			}
			else {
				grid[loc.getX()][loc.getY()] = loc;
			}
			
			orgloc = grid[loc.getX()][loc.getY()];
			
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
	
				
//				if (actor != null) {
//
//					if (actor instanceof Robot) {
//
//						Robot robot = (Robot) actor;
//
//						int x = robot.getChargingPod().getLocation().getX();
//						int y = robot.getChargingPod().getLocation().getY();
//
//						if (x == i && y == j) { //Charging pod and robot on same location
//							sb.append(robot.toString() + robot.getChargingPod().toString()).append("\t");
//						}
//
//					}
//				}
//				
//
//				if (actor != null) {
//
//					sb.append(actor.toString()).append("\t");
//
//				}
//				else {
//
//					sb.append(".").append("\t");
//
//				}
			}
			sb.append("\n");
		}

		return sb.toString();

	}


}
