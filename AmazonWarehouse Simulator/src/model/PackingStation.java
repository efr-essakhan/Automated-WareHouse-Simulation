package model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PackingStation extends Actor {
	
	private final static boolean OBSTRUCTIVE = false; //Can robot move through it?
	private static LinkedList<Order> orders;
	private List<Actor> robots;

	public PackingStation(int x, int y, List<Actor> robots) {

		this(x, y, robots, null);

	}
	
	public PackingStation(int x, int y, List<Actor> robots,String uid) {
		super(x, y, uid);
		orders = new LinkedList<Order>();
		this.robots = robots;
	}

	@Override
	public void act() {
		Order order = getOrders().pollFirst();
		
		if (order != null) {
			//Take a shelf from the order's HashSet
			for (Shelf shelf : order.getShelfs()) {
				

				//Holds the best responses to the proposal and the Robot
				int lowestSteps = -1;
				Robot robotForTheJob = null;
				
				//Propose shelf to each robot
				for (Actor robot : robots) {
					Robot robot1 = (Robot) robot;
					
					Integer stepsToTake = robot1.analyseProposal(shelf, this);

					if (stepsToTake != null) {
						//Set the first potential robot
						if (robotForTheJob == null) {
							lowestSteps = stepsToTake;
							robotForTheJob = robot1;
						}//Check if this robot can be Set the Robot for the job with its corresponding steps.
						else if (lowestSteps > stepsToTake) {
							lowestSteps = stepsToTake;
							robotForTheJob = robot1;
						}
					}
				}
				
				//Check if a robot has responded to the proposal.
				if (robotForTheJob != null) {
					
					
					
				}
				
				
				
				
				
				
				
				
				
				
			}
			
			

			
		}
		
	}
	
	
	

	@Override
	public String toString() {
		return "P";
	}

	@Override
	public boolean getOBSTRUCTIVE() {
	
		return this.OBSTRUCTIVE;
	}

	public static LinkedList<Order> getOrders() {
		return orders;
	}

	public static void enterOrder(Order order) {
		orders.add(order);
	}

}
