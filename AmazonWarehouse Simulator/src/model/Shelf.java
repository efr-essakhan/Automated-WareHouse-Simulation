package model;

public class Shelf extends Actor {
	
	private final static boolean OBSTRUCTIVE = false; //Can robot move through it?
	
	public Shelf(int x, int y) {
		super(x, y);
	}

	public Shelf(int x, int y, String UID) {
		super(x, y, UID);
	}

	@Override
	public void act() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		
		return "S";
	}

	@Override
	public boolean getOBSTRUCTIVE() {
		
		return this.OBSTRUCTIVE;
	}

}
