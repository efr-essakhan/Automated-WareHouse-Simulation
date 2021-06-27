package model.warehouse.actors;

import model.strategies.PathEstimationAlgorithm;
import model.strategies.PathFindingAlgorithm;
import model.strategies.ManhattanEst;
import model.strategies.SimplePathFindingAlgorithm;
import model.warehouse.entities.Location;
import model.warehouse.entities.Proposal;
import model.warehouse.entities.State;

public class Robot extends Actor {
	
	private final static boolean OBSTRUCTIVE = true; //Can another robot move through it?
	public static int CAPACITY = 0; //capacity of the battery of all the robots
	
	private ChargingPod chargingPod; 
	private int charge = 20; //TODO: Change back to zero;
	private Proposal proposalHandling; //current assignment
	private boolean charging; //Whether charging (or going to a charging pot to be charged) or not.
	private State state; //State related to doing an assignment
	private PathFindingAlgorithm pathFindingAlgo;
	PathEstimationAlgorithm estimationAlgo;
	
	public Robot(int x, int y) {
		
		this(x, y, null, null);
		
	}
	

	public Robot(int x, int y, String uid, String ChargingPodUid) {
		super(x, y, uid);
		
		chargingPod = new ChargingPod(this, ChargingPodUid); //Assign a chargingpod automatically
		proposalHandling = null;
		pathFindingAlgo = new SimplePathFindingAlgorithm();
		state = State.UNCOLLECTED;
		estimationAlgo = new ManhattanEst();
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
		
		Double stepsToCompleteJourney = estimationAlgo.calculateRSPDistance(proposal);
		
		if (stepsToCompleteJourney != null) { //I.e. robot does have enough charge for the journey
			return (Integer) stepsToCompleteJourney.intValue();
			
		}else {//Robot does not have enough charge for the journey
			
			return null;
		}
		
	}
	
	public void obeyProposal(Proposal proposal) {
		this.proposalHandling = proposal;
		pathFindingAlgo.setFieldsForProposal(proposal);
		this.switchState(); //Should go from UNCOLLECTED -> COLLECTING
		pathFindingAlgo.setNewTargetDisplacementBasedOnState();
	}
	
	
	public String attemptCharge() {
		
		//Attempt at making robot charge.
		state = State.CHARGING;
		Double distanceToCharger = estimationAlgo.calculateRCDistance(this);
		
		if (distanceToCharger != null) { //null represents: not possible.
			//TODO: Make robot charge
			
			this.proposalHandling = new Proposal(this);
			 
			pathFindingAlgo.setFieldsForCharging(this);
			pathFindingAlgo.setNewTargetDisplacementBasedOnState(); //Since state is Charging the target will be the charging Pot.
			handleProposal();
			return null;
		}

		else { 
			
			return "Robot on loc Y: " + this.location.getY()+ " X: " + this.location.getX() + " HAS NOT ENOUGH CHARGE TO REACH PACKINGSTATION.";
			
		}
		
	}
	

	@Override
	public String act() {
		
		handleProposal();
		return null;
		
		
	}


	private void handleProposal() {
		if (proposalHandling != null) { //If there is a proposal, handle it.
			
			boolean pathFound = false; //if a next loctation for the robot to move to has been found or nah
			
			while (pathFound == false) {
				Location newLoc = pathFindingAlgo.getNewLocationForRobot();
				
				//If New Location is not just the old location (indicated by being null) then:
				if (newLoc != null) {
					//We know it is is a new location to move to, so set it as the robots location. else:
					this.setLocation(newLoc);	
					useCharge(); 
					pathFound = true;	
				}else { //If new Location == Old location, indicated by null, that means that there is nomore to move and you are at your desired location
										
					this.switchState();
					if (this.state == State.COLLECTED) {  //from collecting > collected
						
						proposalHandling.getOrder().updateShelfState(proposalHandling.getShelf(), State.COLLECTED);
						pathFindingAlgo.setNewTargetDisplacementBasedOnState();
						
					}else if (this.state == State.UNCOLLECTED){ //will comeback to being uncollected - meaning done with this proposal.
						
						proposalHandling = null;
						break;
						
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
		case CHARGING:
			state = State.UNCOLLECTED;
			break;	
		default:
			break;
		}
	}

}
