package model;

public class Robot extends Actor {
	
	private final static boolean OBSTRUCTIVE = true; //Can another robot move through it?
	
	public static int CAPACITY = 0; //capacity of the battery of all the robots
	private ChargingPod chargingPod; 
	private int Charge;
	private boolean carrying;
	
	public Robot(int x, int y) {
		
		this(x, y, null, null);
		
	}
	

	public Robot(int x, int y, String uid, String ChargingPodUid) {
		super(x, y, uid);
		
		chargingPod = new ChargingPod(this, ChargingPodUid); //Assign a chargingpod automatically
		setCarrying(false); // not carrying anything at the start.
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
	public int analyseProposal(Proposal proposal) {
		
		//do pathfinding first.
		//decide if it can do the assignment based on fuel count.
		
		return Charge;
		
	}
	
	public void obeyProposal(Proposal proposal) {
//		return 1;
	}
	

	@Override
	public void act() {
		// TODO Auto-generated method stub
		
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

}
