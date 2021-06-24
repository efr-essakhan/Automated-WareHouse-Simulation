package model.warehouse.actors;

import model.strategies.PathEstimationAlgorithm;
import model.strategies.PathFindingAlgorithm;
import model.strategies.SimplePathEst;
import model.strategies.SimplePathFindingAlgorithm;
import model.warehouse.entities.Location;
import model.warehouse.entities.Proposal;
import model.warehouse.entities.State;

public class Robot extends Actor {
	
	private final static boolean OBSTRUCTIVE = true; //Can another robot move through it?
	public static int CAPACITY = 0; //capacity of the battery of all the robots
	
	private ChargingPod chargingPod; 
	private int charge = 20; //TODO: Change back to zero;
	private Proposal proposal; //current assignment
	private boolean charging; //Whether charging (or going to a charging pot to be charged) or not.
	private State state; //State related to doing an assignment
	private PathFindingAlgorithm pathFindingAlgo;
	
	public Robot(int x, int y) {
		
		this(x, y, null, null);
		
	}
	

	public Robot(int x, int y, String uid, String ChargingPodUid) {
		super(x, y, uid);
		
		chargingPod = new ChargingPod(this, ChargingPodUid); //Assign a chargingpod automatically
		proposal = null;
		pathFindingAlgo = null;
		state = State.UNCOLLECTED;
	}
	
	public ChargingPod getChargingPod() {
		return chargingPod;
	}
	
	/**
	 * Use charge when moving.
	 */
	public void useCharge() {
		if (state == State.COLLECTED) { //means it is carrying
			charge=-2;
		}else {
			charge=-1;
		}
	}
	
	public int getCharge() {
		return charge;
	}
	
	/**
	 * Increase charge every tick when charging.
	 */
	public void increaseCharge() {
		charge=+1;
	}

	/**
	 * Checks if it can complete the assignment with fuel count, if so it returns the
	 * approximate steps it will take to complete the assignment.
	 * @param proposal object to retrieve the shelf to travel to to pick up items & packingStation that proposed the assignment
	 * @return number of steps it will take to complete the assignment
	 */
	public Integer analyseProposal(Proposal proposal) {
		
		PathEstimationAlgorithm estimationAlgo = new SimplePathEst(proposal);
		Double distance = estimationAlgo.calculateRSPDistance();
		
		if (distance != null) { //I.e. robot does have enough charge for the journey
			return (Integer) distance.intValue();
			
		}else {//Robot does not have enough charge for the journey
			
			//Attempt at making robot charge.
			state = State.Charging;
			distance = estimationAlgo.calculateRCDistance();
			
			if (distance != null) {
				return (Integer) distance.intValue();
			}
			else { //TODO: Simulation should fail
				return null; 
			}
			
		
		}
		
	}
	
	public void attemptCharge() {
		
	}
	
	
	public void obeyProposal(Proposal proposal) {
		this.proposal = proposal;
		pathFindingAlgo = new SimplePathFindingAlgorithm(this.proposal);
		this.switchState(); //Should go from UNCOLLECTED -> COLLECTING
		pathFindingAlgo.setNewTargetDisplacement();
	}
	

	@Override
	public void act() {
		
		if (proposal != null && pathFindingAlgo != null) {
			
			
			boolean pathFound = false;
			
			while (pathFound == false) {
				Location newLoc = pathFindingAlgo.getNewLocationForRobot();
				
				//If New Location is not just the old location (indicated by being null) then:
				if (newLoc != null) {
					//We know it is is a new location to move to, so set it as the robots location. else:
					this.setLocation(newLoc);				
					pathFound = true;
					useCharge(); 
				}else { //If new Location == Old location, indicated by null, that means that there is nomore to move and you are at your desired location
										
					this.switchState();
					if (this.state == State.COLLECTED) {  //from collecting > collected
						
						proposal.getOrder().updateShelfState(proposal.getShelf(), State.COLLECTED);
						pathFindingAlgo.setNewTargetDisplacement();
						
					}else if (this.state == State.UNCOLLECTED){ //will comeback to being uncollected - meaning done with this proposal.
						
						proposal = null;
						pathFindingAlgo = null;
						
					}
					
				}
				
			}		
	
		}
		
	}
	

	@Override
	public String toString() {
		return "R";
	}

	@Override
	public boolean getOBSTRUCTIVE() {
		
		return this.OBSTRUCTIVE;
	}


	public State getState() {
		return state;
	}


	public void setState(State state) {
		this.state = state;
	}
	
	public void switchState() {
		switch (state) {
		case UNCOLLECTED:
			state = State.COLLECTING;
			break;
		case COLLECTING:
			state = State.COLLECTED;
			break;
		case COLLECTED:
			state = State.UNCOLLECTED;
			break;
		default:
			break;
		}
	}

}
