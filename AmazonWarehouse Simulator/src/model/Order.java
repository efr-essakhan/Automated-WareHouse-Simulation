package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Order {
	
	private int ticksToPack; //number of ticks it takes to pack it
	private boolean dispatched;  //Delivery dispatched or not

	private HashMap<Shelf, ShelfStates> shelfs; // Values: Null = Not touched yet, False = Dispatches, 
	

	
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
		dispatched = false;
		
	}
	
	public HashMap<Shelf,ShelfStates> getShelfs() {
		return shelfs;
	}
	
	public void addShelf(Shelf shelf) {
		this.shelfs.put(shelf, ShelfStates.WAITING);
	}
	
	public void addShelfs(HashMap<Shelf,ShelfStates> shelfs) {
		this.shelfs.putAll(shelfs);
	}
	
	public void removeShelf(Shelf shelf) {
		
		shelfs.remove(shelf);
		
	}
	
	public void updateShelfState(Shelf shelf, ShelfStates newState) {
		
		this.shelfs.put(shelf, newState);
	}
	
	
	public void setShelfToDispatched(Shelf shelf) {
		
		shelfs.remove(shelf);
		
	}
	
	public void setDispatched(boolean dispatched) {
		this.dispatched = dispatched;
	}

	public int getTicksToPack() {
		return ticksToPack;
	}

}
