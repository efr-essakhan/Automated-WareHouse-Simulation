package model;

/**
 * Value object, holding the details of the Shelf and packing station that is making the proposal - to be passed to the Robots.
 * @author Essa
 *
 */
public class Proposal { 
	
	private int lowestSteps;
	private Robot robotForTheJob;
	
	public Proposal() {
		lowestSteps = 0;
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
	
	
	
}
