package model;

import java.util.ArrayList;

public class PackingStation extends Actor {
	
	private final static boolean OBSTRUCTIVE = false; //Can robot move through it?
	private static ArrayList<Order> orders;
	
	public PackingStation(int x, int y, String uid) {
		super(x, y, uid);
		orders = new ArrayList<Order>();
	}

	@Override
	public void act() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "P";
	}

	@Override
	public boolean getOBSTRUCTIVE() {
	
		return this.OBSTRUCTIVE;
	}

	public static ArrayList<Order> getOrders() {
		return orders;
	}

	public static void enterOrder(Order order) {
		orders.add(order);
	}

}
