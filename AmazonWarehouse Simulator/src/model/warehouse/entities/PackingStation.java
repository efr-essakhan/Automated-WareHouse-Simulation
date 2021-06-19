package model.warehouse.entities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import model.warehouse.Order;
import model.warehouse.Proposal;
import model.warehouse.State;

import java.util.TreeMap;

public class PackingStation extends Actor {

	private final static boolean OBSTRUCTIVE = false; //Can robot move through it?
	private static List<Order> orders;
	private List<Actor> robots;
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

				for (Order order : orders) {

					//Take a shelf from the order's HashMap
					for (Entry<Shelf, State> shelfs :  order.getShelfs().entrySet()) {
						Shelf shelf = shelfs.getKey();
						State currentState = shelfs.getValue();

						if (currentState == State.UNCOLLECTED) {
							
							//Holds the best responses to the proposal and the Robot
							Proposal proposal = new Proposal(order, shelf, this);

							//Propose shelf to each robot
							for (Actor robot : robots) {
								Robot robot1 = (Robot) robot;

								Integer stepsToTake = robot1.analyseProposal(proposal);

								if (stepsToTake != null) { // null = robot not possible to make the trip
									//Set the first potential robot //Check if this robot can be Set the Robot for the job with its corresponding steps.
									if (proposal.getRobotForTheJob() == null || proposal.getLowestSteps() > stepsToTake) {
										proposal.setLowestSteps(stepsToTake);
										proposal.setRobotForTheJob(robot1);
									}
								}else {
									//Charge the robot
									
								}
							}

							//If there is a robot suitable to take the job, give it to them. Else try again next tick.
							if (proposal.getRobotForTheJob() != null) {

								proposal.getRobotForTheJob().obeyProposal(proposal);
								order.updateShelfState(shelf, State.COLLECTING);

							}
							else {
								break;
							}

						}

					}

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

	public static List<Order> getOrders() {
		return orders;
	}

	public static void enterOrder(Order order) {
		orders.add(order);
	}

}
