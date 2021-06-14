package model;

/**
 * @author xbox_
 *
 */
public class ChargingPod extends Actor {
	
	public static int CHARGE_SPEED = 0; //the number of power units that a charging pod recharges per tick
	private Robot bot;
	public ChargingPod(int x, int y, Robot bot) {
		super(x, y);
		this.bot = bot;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Charges the batteries of a robot C power units per tick, it 
	 * continues charging until the battery is half full.
	 */
	public void chargeRobot() {
		
		while (bot.getCharge() >= ((Robot.CAPACITY)/50)) {
			bot.increaseCharge();
		}
		
	}

	@Override
	public void act() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "C";
	}

}
