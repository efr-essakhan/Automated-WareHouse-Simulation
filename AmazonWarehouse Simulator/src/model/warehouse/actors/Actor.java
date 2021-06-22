package model.warehouse.actors;

import model.warehouse.entities.Location;

/**
 * 
 * A simple model of an actor in a simulation.
 * @author Essa
 *
 */
/**
 * @author xbox_
 *
 */
public abstract class Actor {
	
	protected Location location;
	private static String UID = "0";
	
	
	/**
	 * Constructer If you want self generating UID
	 * @param x
	 * @param y
	 */
	public Actor(int x, int y) {
		this(x, y, String.valueOf(Integer.parseInt(UID)+1));
	}
	
	
	/**
	 * Constructer if you want to pass in your own UID
	 * @param x
	 * @param y
	 * @param uid
	 */
	public Actor(int x, int y, String uid) {
		location = new Location(x, y);
		location.addActor(this);
		
		if (uid != null) {
			this.UID = uid;
		}
		else {
			this.UID = String.valueOf(Integer.parseInt(UID)+1);
		}
		
	}
	
	public abstract boolean getOBSTRUCTIVE();

	/**
	 * This is what actors do each tick
	 */
	public abstract void act();
	
	/**
	 * 
	 * @param row
	 * @param col
	 */
	public void setLocation(int x, int y) {
		location.setY(x);
		location.setX(y);
	}
	
	public void setLocation(Location loc) {
		setLocation(loc.getY(), loc.getX());
	}

	public Location getLocation() {
		return location;
	}
	
	public String getUid() {
		return UID;
	}
	
	public abstract String toString();

}
