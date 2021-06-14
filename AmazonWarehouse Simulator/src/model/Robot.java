package model;

public class Robot extends Actor {
	
	public static int CAPACITY = 0; //capacity of the battery of all the robots
	private ChargingPod chargingPod; 
	private int Charge;
	private boolean carrying;

	public Robot(int x, int y) {
		super(x, y);
		chargingPod = new ChargingPod(x,y, this);
		carrying = false; // not carrying anything at the start.
		
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

}
