package model.warehouse.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import model.warehouse.actors.Actor;

/**
 * Represent a location y,x on the map.
 * 
 * @author Essa
 * @version 2021
 */
public class Location implements Cloneable {
	
	private int y;
	private int x;
	private Set<Actor> actors;
	
	public Location(int y, int x) {
		this(y, x, null);
	}
	
	public Location(int y, int x, Actor actor) {
		this.y = y;
		this.x = x;
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
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getX() {
		return x;
	}
	
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
}
