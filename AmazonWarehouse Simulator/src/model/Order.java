package model;

import java.util.HashSet;
import java.util.List;

public class Order {
	
	private int ticksToPack; //number of ticks it takes to pack it
	private boolean dispatched;  //Delivery dispatched or not

	private HashSet<Shelf> shelfs;
	

	
	public Order(int ticksToPack) {
		this(ticksToPack, null);	
	}
	
	public Order(int ticksToPack, List<String> shelfUid) {
		this.ticksToPack = ticksToPack;
		shelfs = new HashSet<Shelf>();
		dispatched = false;
		
		//use list to fill in shelfs.
		
	}
	
	public HashSet<Shelf> getShelfs() {
		return shelfs;
	}
	
	public void addShelf(Shelf shelf) {
		this.shelfs.add(shelf);
	}
	
	public void addShelfs(HashSet<Shelf> shelfs) {
		this.shelfs.addAll(shelfs);
	}
	
	public void setDispatched(boolean dispatched) {
		this.dispatched = dispatched;
	}

	public int getTicksToPack() {
		return ticksToPack;
	}

}