package model.warehouse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import model.warehouse.entities.Actor;

/**
 * Represent a location x,y on the map.
 * 
 * @author Essa
 * @version 2021
 */
public class Location {
	
	private int x;
	private int y;
	private Set<Actor> actors;
	
	public Location(int x, int y) {
		this(x, y, null);
	}
	
	public Location(int x, int y, Actor actor) {
		this.x = x;
		this.y = y;
		actors = new HashSet<Actor>();
		addActor(actor);
	}
	
	public void addActor(Actor actor) {
		actors.add(actor);
	}
	
	public Set<Actor> getActors() {
		return actors;
	}
	
	
	public void mergeLocations(Location loc) {
		
		actors.addAll(loc.getActors());	
		
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
