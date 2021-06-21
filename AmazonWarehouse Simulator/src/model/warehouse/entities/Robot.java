package model.warehouse.entities;

import model.strategies.PathEstimationAlgorithm;
import model.strategies.PathFindingAlgorithm;
import model.strategies.SimplePathEst;
import model.strategies.SimplePathFindingAlgorithm;
import model.warehouse.Location;
import model.warehouse.Proposal;
import model.warehouse.State;

public class Robot extends Actor {
	
	private final static boolean OBSTRUCTIVE = true; //Can another robot move through it?
	
	public static int CAPACITY = 0; //capacity of the battery of all the robots
	private ChargingPod chargingPod; 
	private int Charge = 20; //TODO: Change back to zero;
	private boolean carrying;
	private Proposal proposal; //current assignment
	private State state;
	private PathFindingAlgorithm pathFindingAlgo;
	
	public Robot(int x, int y) {
		
		this(x, y, null, null);
		
	}
	

	public Robot(int x, int y, String uid, String ChargingPodUid) {
		super(x, y, uid);
		
		chargingPod = new ChargingPod(this, ChargingPodUid); //Assign a chargingpod automatically
		setCarrying(false); // not carrying anything at the start.
		proposal = null;
		pathFindingAlgo = null;
		state = State.UNCOLLECTED;
	}
	
	public ChargingPod getChargingPod() {
		return chargingPod;
	}
	
	public int getCharge() {
		return Charge;
	}
	
	public void increaseCharge() {
		Charge = getCharge()+1;
	}

	/**
	 * Checks if it can complete the assignment with fuel count, if so it returns the
	 * approximate steps it will take to complete the assignment.
	 * @param proposal object to retrieve the shelf to travel to to pick up items & packingStation that proposed the assignment
	 * @return number of steps it will take to complete the assignment
	 */
	public Integer analyseProposal(Proposal proposal) {
		
		PathEstimationAlgorithm estimationAlgo = new SimplePathEst(proposal);
		
		Double distance = estimationAlgo.calculateDistance();
		if (distance != null) {
			return (Integer) distance.intValue();
		}else {
			return null;
		}
		
	}
	
	public void obeyProposal(Proposal proposal) {
		this.proposal = proposal;
		pathFindingAlgo = new SimplePathFindingAlgorithm(this.proposal);
		this.switchState();
	}
	

	@Override
	public void act() {
		
		if (proposal != null && pathFindingAlgo != null) {
			
			Location newLoc = pathFindingAlgo.getNewLocationForRobot();
			
			//If New Location is not just the old location (indicated by being null) then:
			if (newLoc != null) {
				//We know it is is a new location to move to, so set it as the robots location. else:
				this.setLocation(pathFindingAlgo.getNewLocationForRobot());
			}else { //If new Location == Old location, indicated by null, that means that there is nomore to move and you are at your desired location
				
				this.switchState();
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

	public boolean isCarrying() {
		return carrying;
	}

	public void setCarrying(boolean carrying) {
		this.carrying = carrying;
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
			pathFindingAlgo.setNewTargetDisplacement();
			break;
		case COLLECTING:
			state = State.COLLECTED;
			pathFindingAlgo.setNewTargetDisplacement();
			break;
		case COLLECTED:
			state = State.DISPATCHED;
			pathFindingAlgo.setNewTargetDisplacement();
			break;
		case DISPATCHED:
			state = State.UNCOLLECTED;
			pathFindingAlgo.setNewTargetDisplacement();
			break;
		default:
			break;
		}
	}

}
