package model.warehouse;

import model.warehouse.entities.PackingStation;
import model.warehouse.entities.Robot;
import model.warehouse.entities.Shelf;

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
	
	/**
	 * 
	 * @return whether or not the proposal/job has been assigned to a Robot
	 */
	
	
	
}
