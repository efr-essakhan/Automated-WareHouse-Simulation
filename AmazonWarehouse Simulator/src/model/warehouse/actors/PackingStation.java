package model.warehouse.actors;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import model.warehouse.entities.Order;
import model.warehouse.entities.Proposal;
import model.warehouse.entities.State;

import java.util.TreeMap;

public class PackingStation extends Actor {

	private final static boolean OBSTRUCTIVE = false; //Can robot move through it?
	private static List<Order> orders; //List of all orders in the simulation
	private List<Actor> robots; //List of all robots in the simulation
	private Order packingThisOrder; //set by a robot

	public PackingStation(int x, int y, List<Actor> robots) {

		this(x, y, robots, null);

	}

	public PackingStation(int x, int y, List<Actor> robots, String uid) {
		super(x, y, uid);
		orders = new ArrayList<Order>();
		this.robots = robots;
		packingThisOrder = null;
	}

	@Override
	public String act() {
		
		String whatToReturn = null;
		
		
		
		if (packingThisOrder != null) {
			
			switch (packingThisOrder.getState()) {
			case COLLECTING:
				whatToReturn =  allocateShelfsOfOrderToRobots(packingThisOrder);
			case COLLECTED: //TODO:Even when all orders done, the order is not marked as collected
				packingThisOrder.pack();
				break;
			case DISPATCHED: 
				packingThisOrder = null;
				break;
			default:
				break;
			}
		}else {
			
			for (Order order : orders) { //Take the next order.
				
				if (order.getState() == State.UNCOLLECTED) {
					packingThisOrder = order;
					String temp = allocateShelfsOfOrderToRobots(order);
					if (whatToReturn == null && temp != null) { //To avoid unnessecary termination.
						whatToReturn = temp;
					}
					
				}
			}
			
		}
		
		String temp = checkIfOrdersDone();
		if (whatToReturn == null && temp != null) { //To avoid unnessecary termination.
			whatToReturn = temp;
		}
		
		return whatToReturn;

	}

	private String allocateShelfsOfOrderToRobots(Order order) {
		for (Entry<Shelf, State> shelfs :  order.getShelfs().entrySet()) {
			Shelf shelf = shelfs.getKey();
			State currentShelfState = shelfs.getValue();

			if (currentShelfState == State.UNCOLLECTED) { //Order still to be collected, therefore try to assign a robot to it
				
				Proposal proposal = null;
				//Propose shelf to each robot
				for (Actor robot : robots) {
					Robot robot1 = (Robot) robot;
					
					//intended to holds the best responses to the proposal from the Robot
					proposal = new Proposal(order, shelf, this, robot1);

					Integer stepsToTake = robot1.analyseProposal(proposal);

					if (stepsToTake != null) { // null = this robot not possible to make the trip
						//Set the first potential robot OR Check if this robot can be Set the Robot for the job with its corresponding steps.
						if ( proposal.getLowestSteps() == null || proposal.getLowestSteps() > stepsToTake ) {
							proposal.setLowestSteps(stepsToTake);
						}
					}else {
						//The stepsToTake will only be null if the journey cant be made due to insufficient charge, thus make the robot go charge!
						return robot1.attemptCharge();
					}
				}

				//If there is a robot suitable to take the job, give it to them. Else try again next tick.
				if (proposal.getLowestSteps() != null) {

					proposal.getRobotForTheJob().obeyProposal(proposal);
					order.updateShelfState(shelf, State.COLLECTING);
					order.setState(State.COLLECTING);
					this.packingThisOrder = order;

				}
				else {
					break;
				}

			}

		}
		return null;
	}


	/**
	 * Checks if all shelfs have been collected and this sets the Order state as collected.
	 * And Checks if each order is done to terminate
	 * @return String holding simulation end message, if =null then simulation should not finish.
	 */
	private String checkIfOrdersDone() {
		//TODO: Create another helper function for the first set of ifs.
		//Check if each order is done to terminate
		int count = 0;
		int count2 = 0;
		for (Order order : orders) {
			//First update order state based on shelf states.
			if (order.getState() == State.COLLECTING) {

				for (State state : order.getShelfs().values()) {
					if (state == State.COLLECTED) {
						count++;
					}
				}
				
				if (count == order.getShelfs().size()) {
					order.setState(State.COLLECTED);
				}
			}
			else if(order.getState() == State.DISPATCHED) { //Check if order dispatched
				count2++;
			}
		}
		if (count2 == orders.size()) {
			return "All orders done!"; 
		}
		return null;
	}




	@Override
	public String toString() {
		return "P";
	}

	@Override
	public boolean getOBSTRUCTIVE() {

		return this.OBSTRUCTIVE;
	}

	public static List<Order> getOrders() {
		return orders;
	}

	public static void enterOrder(Order order) {
		orders.add(order);
	}

}
