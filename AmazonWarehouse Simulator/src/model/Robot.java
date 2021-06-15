package model;

public class Robot extends Actor {
	
	private final static boolean OBSTRUCTIVE = true; //Can another robot move through it?
	
	public static int CAPACITY = 0; //capacity of the battery of all the robots
	private ChargingPod chargingPod; 
	private int Charge;
	private boolean carrying;

	public Robot(int x, int y) {
		super(x, y);
		chargingPod = new ChargingPod(this); //Assign a chargingpod automatically
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
