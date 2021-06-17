package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Order {
	
	private int ticksToPack; //number of ticks it takes to pack it
	private State state; //The state of the Order

	private HashMap<Shelf, State> shelfs; //Holds the shelf and its state respective of the order.
	

	
//	public Order(int ticksToPack) {
//		this(ticksToPack, null);	
//	}
//	
//	public Order(int ticksToPack, String[] shelfsUid) {
//		this.ticksToPack = ticksToPack;
//		shelfs = new HashSet<Shelf>();
//		dispatched = false;
//		
//		//use list to fill in shelfs.
//
//		
//	}
	
	public Order(int ticksToPack) {
		this.ticksToPack = ticksToPack;
		shelfs = new HashMap<>();
		state = State.WAITING;
		
	}
	
	public HashMap<Shelf,State> getShelfs() {
		return shelfs;
	}
	
	public void addShelf(Shelf shelf) {
		this.shelfs.put(shelf, State.WAITING);
	}
	
	public void addShelfs(HashMap<Shelf,State> shelfs) {
		this.shelfs.putAll(shelfs);
	}
	
	public void removeShelf(Shelf shelf) {
		
		shelfs.remove(shelf);
		
	}
	
	public void updateShelfState(Shelf shelf, State newState) {
		
		this.shelfs.put(shelf, newState);
	}
	
	
	public void setShelfToDispatched(Shelf shelf) {
		
		shelfs.remove(shelf);
		
	}

	public int getTicksToPack() {
		return ticksToPack;
	}

}
