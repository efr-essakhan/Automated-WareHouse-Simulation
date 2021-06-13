package model;

public class Robot extends Actor {
	
	ChargingPod chargingPod;

	public Robot(int x, int y) {
		super(x, y);
		chargingPod = new ChargingPod();
		
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
