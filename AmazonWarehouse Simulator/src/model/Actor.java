package model;
/**
 * 
 * A simple model of an actor in a simulation.
 * @author Essa
 *
 */
public abstract class Actor {
	
	protected Location loc;
	
	public Actor(int x, int y) {
		loc = new Location(x, y);
	}
	
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
		loc.setX(x);
		loc.setY(y);
	}
	

	public Location getLocation() {
		return loc;
	}
	
	public abstract String toString();

}
