package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Represent a location x,y on the map.
 * 
 * @author Essa
 * @version 2021
 */
public class Location {
	
	private int x;
	private int y;
	private Set<Actor> actorsInLoc;
	
	public Location(int x, int y) {
		this.x = x;
		this.y = y;
		actorsInLoc = new HashSet<Actor>();
	}
	
	public void addActor(Actor actor) {
		actorsInLoc.add(actor);
	}
	
	public Set<Actor> getActorsInLocSet() {
		return actorsInLoc;
	}
	
	public void mergeLocations(Location loc) {
		
		actorsInLoc.addAll(loc.getActorsInLocSet());	
		
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
