package model;

import java.util.ArrayList;
import java.util.Iterator;

public class Grid {

	private Location[][] grid;

	public Grid(int gridLength, int gridHeight) {

		grid = new Location[gridLength][gridHeight];

	}

	public void addActor(Actor actor) {
		
		Location loc = actor.getLocation();
		grid[loc.getX()][loc.getY()] = actor;
	}
	
	public void addLocationToGrid(Location loc) {
		Location orgloc = grid[loc.getX()][loc.getY()];
		
		if (orgloc != null) {
			orgloc.mergeLocations(loc);
		}
		else {
			grid[loc.getX()][loc.getY()] = loc;
		}
		
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < grid.length; i++) {

			for (int j = 0; j < grid[i].length; j++) {

				Actor actor = grid[i][j];
				
				if (actor != null) {

					if (actor instanceof Robot) {

						Robot robot = (Robot) actor;

						int x = robot.getChargingPod().getLocation().getX();
						int y = robot.getChargingPod().getLocation().getY();

						if (x == i && y == j) { //Charging pod and robot on same location
							sb.append(robot.toString() + robot.getChargingPod().toString()).append("\t");
						}

					}
				}
				

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
