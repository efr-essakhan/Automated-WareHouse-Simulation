package model;

/**
 * Value object, holding the details of the Shelf and packing station that is making the proposal - to be passed to the Robots.
 * @author Essa
 *
 */
public class Proposal { 
	
	private int lowestSteps;
	private Robot robotForTheJob;
	private Shelf shelf;
	private PackingStation packingStation;
	
	public Proposal(Shelf shelf, PackingStation packingStation) {
		this.shelf = shelf;
		this.packingStation = packingStation;
		lowestSteps = -1;
		robotForTheJob = null;
	}
	
	public int getLowestSteps() {
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
