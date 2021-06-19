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
	private int Charge;
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
		
		return (Integer) estimationAlgo.calculateDistance().intValue();
	}
	
	public void obeyProposal(Proposal proposal) {
		this.proposal = proposal;
		pathFindingAlgo = new SimplePathFindingAlgorithm(this.proposal);
		setState(State.COLLECTING);
	}
	

	@Override
	public void act() {
		
		if (proposal != null && pathFindingAlgo != null) {
//			PathFindingAlgorithm pathFindingAlg = new SimplePathFindingAlgorithm(proposal);
			this.setLocation();
			
			
			//Move using loc
			
			
			
			
			//Once 
			
		}
		
	}
	
	private void goTo() {
		
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

}
