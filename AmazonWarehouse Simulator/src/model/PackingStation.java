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
			for (Shelf shelf : order.getShelfs().keySet()) {
				
				//Holds the best responses to the proposal and the Robot
				Proposal proposal = new Proposal(order, shelf, this);
				
				//Propose shelf to each robot
				for (Actor robot : robots) {
					Robot robot1 = (Robot) robot;
					
					Integer stepsToTake = robot1.analyseProposal(proposal);
					
					if (stepsToTake != null) {
						//Set the first potential robot //Check if this robot can be Set the Robot for the job with its corresponding steps.
						if (proposal.getRobotForTheJob() == null || proposal.getLowestSteps() > stepsToTake) {
							proposal.setLowestSteps(stepsToTake);
							proposal.setRobotForTheJob(robot1);
						}
					}
				}
				
				//If there is a robot suitable to take the job, give it to them. Else try again next tick.
				if (proposal.getRobotForTheJob() != null) {
					
					proposal.getRobotForTheJob().obeyProposal(proposal);
					order.updateShelfState(shelf, ShelfStates.WORKEDON);
					
				}
				else {
					break;
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
