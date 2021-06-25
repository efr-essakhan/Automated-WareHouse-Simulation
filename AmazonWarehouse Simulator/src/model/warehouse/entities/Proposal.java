package model.warehouse.entities;

import model.warehouse.actors.PackingStation;
import model.warehouse.actors.Robot;
import model.warehouse.actors.Shelf;

/**
 * Value object, holding the details of the Shelf and packing station that is making the proposal - to be passed to the Robots.
 * @author Essa
 *
 */
public class Proposal { 
	
	private Integer lowestSteps;
	private Robot robotForTheJob;
	private Shelf shelf;
	private Order order;
	private PackingStation packingStation;
	
	/**
	 * This constructor used for formulating Robot charging related proposals.
	 * @param robot
	 */
	public Proposal(Robot robot) {
		this(null, null, null, robot);
	}
	
	/**
	 * 
	 * This constructor used for formulating Robot warehouse job related proposals.
	 * @param order
	 * @param shelf
	 * @param packingStation
	 * @param robot
	 */
	public Proposal(Order order, Shelf shelf, PackingStation packingStation, Robot robot) {
		this.shelf = shelf;
		this.packingStation = packingStation;
		this.order = order;
		lowestSteps = null;
		robotForTheJob = robot;
	}
	
	public Integer getLowestSteps() {
		return lowestSteps;
	}
	public void setLowestSteps(int lowestSteps) {
		this.lowestSteps = lowestSteps;
	}
	public Robot getRobotForTheJob() {
		return robotForTheJob;
	}
	public void setRobotForTheJob(Robot robotForTheJob) {
		this.robotForTheJob = robotForTheJob;
	}

	public Shelf getShelf() {
		return shelf;
	}

	public PackingStation getPackingStation() {
		return packingStation;
	}

	public Order getOrder() {
		return order;
	}
	
	/**
	 * 
	 * @return whether or not the proposal/job has been assigned to a Robot
	 */
	
	
	
}
