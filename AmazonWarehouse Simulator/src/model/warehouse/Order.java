package model.warehouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import model.warehouse.entities.Shelf;

public class Order {
	
	private int ticksToPack; //number of ticks it takes to pack it
	private int tickProgress; //to keep track how many ticks packed, when packing.
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
		tickProgress = 0;
		shelfs = new HashMap<>();
		state = State.UNCOLLECTED;
		
	}
	
	public HashMap<Shelf,State> getShelfs() {
		return shelfs;
	}
	
	public void addShelf(Shelf shelf) {
		this.shelfs.put(shelf, State.UNCOLLECTED);
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
	
//	public State getShelfState(Shelf shelf) {
//		
//		return this.shelfs.get(shelf);
//	}
//	

	public int getTicksToPack() {
		return ticksToPack;
	}

	public State getState() {
		return state;
	}

	public void pack() {
		tickProgress++;
		
		if (tickProgress == ticksToPack) {
			this.state = State.DISPATCHED;
		}
	}

}
