package model.warehouse.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.crypto.KeySelector.Purpose;

import model.warehouse.actors.Actor;
import model.warehouse.actors.Robot;

public class Grid extends Entitiy{

	private Location[][] grid;

	public Grid(int rows, int columns) {

		grid = new Location[rows][columns]; //y,x

	}
	
	/**
	 * Updates the actors placement on the grid based on their location.
	 */
	public void updateActorsOnGrid() {
		
		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[y].length; x++) {
				
				Location loc = grid[y][x];
				
				if (loc != null) {
					ArrayList<Actor> actorsToRemove = new ArrayList<Actor>();
					for (Actor actor : loc.getActors()) {
						if (actor instanceof Robot) {
							int actorX = actor.getLocation().getX();
							int actorY = actor.getLocation().getY();
							if (y != actorY || x != actorX) {
								//Store this actor to remove this actor later outside of this loop (to avoid error) from the Loc in the grid as it doesn't belong there.
								actorsToRemove.add(actor); //TODO: change this into an iterable to remove
								
								if (grid[actorY][actorX] != null) {
									
									grid[actorY][actorX].addActor(actor);
									actor.setLocation(grid[actorY][actorX]);
									
								}else {
									Location actorLoc = new Location(actorY, actorX, actor);
									grid[actorY][actorX] = actorLoc;
									actor.setLocation(actorLoc);
								}
								
								
							}
							
						}
						
					}
					
					
					loc.getActors().removeAll(actorsToRemove);
					
					
				}
				
			}
		}
		
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
					if (loc.getActors().isEmpty() == false) {
						for (Actor actor : loc.getActors()) {
							
							if (actor != null) {
								sb.append(actor.toString());
							}
						}	
					}else {
						sb.append(".");
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
