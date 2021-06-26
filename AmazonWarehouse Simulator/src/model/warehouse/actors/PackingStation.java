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
	public void act() {


		//Prioritise packing, if any order needs it
		if (packingThisOrder != null && packingThisOrder.getState() == State.COLLECTED) {

			packingThisOrder.pack();

		} 
		else {

			//Deal with orders
			if (!orders.isEmpty()) {
				

				for (Order order : orders) { //Take order one by one
					//TODO: Update order state somehow
					//Take a shelf from the order's HashMap
					for (Entry<Shelf, State> shelfs :  order.getShelfs().entrySet()) {
						Shelf shelf = shelfs.getKey();
						State currentState = shelfs.getValue();

						if (currentState == State.UNCOLLECTED) { //Order still to be collected, therefore try to assign a robot to it
							
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
									robot1.attemptCharge();
								}
							}

							//If there is a robot suitable to take the job, give it to them. Else try again next tick.
							if (proposal.getLowestSteps() != null) {

								proposal.getRobotForTheJob().obeyProposal(proposal);
								order.updateShelfState(shelf, State.COLLECTING);

							}
							else {
								break;
							}

						}

					}

				}
				
				checkIfOrdersDone();

			}

		}

	}

	/**
	 * Check if each order is done to terminate
	 */
	private void checkIfOrdersDone() {
		//TODO: Create another helper function for the first set of ifs.
		//Check if each order is done to terminate
		int count = 0;
		int count2 = 0;
		for (Order order : orders) {
			//First update order state based on shelf states.
			for (State state : order.getShelfs().values()) {
				if (state == State.COLLECTED) {
					count++;
				}
			}
			if (count == orders.size()) {
				order.setState(State.DISPATCHED);
			}
			if (order.getState() == State.DISPATCHED) {
				count2++;
			}
		}
		if (count2 == orders.size()) {
			endSimulation("All orders done!"); //TODO: This is called b4 the last print, thus have act() return an Hashmap or tuple of <String,Bool> String = Suspension message, Bool = Wether to suspend after the final print or nah!
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

	public static List<Order> getOrders() {
		return orders;
	}

	public static void enterOrder(Order order) {
		orders.add(order);
	}

}
