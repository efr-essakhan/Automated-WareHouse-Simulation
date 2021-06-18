package model;

//Manhattan
public class SimplePathEst extends PathEstimationAlgorithm {

	public SimplePathEst(Proposal proposal) {
		super(proposal);
	}

	@Override
	public Double calculateDistance() {

		int robotCharge = selfRobot.getCharge();
		
		//First the Robot to shelf
		int distanceRS = manhattanDistance(shelfX, robotX, shelfY, robotY);

		//Now the shelf to station
		int distanceSP = manhattanDistance(packingX, shelfX, packingY, shelfY);
		
		//For guarantee that the robot won't be stranded after the job, station to charger
		int distancePC = manhattanDistance(chargerX, packingX, chargerY, packingY);
		

		robotCharge = robotCharge-((distanceSP*2) + distanceRS + distancePC); //distanceSP*2: this because it will take double charge when carrying per movement
		if (robotCharge <= 0) {
			return null; //null represents distance not possible at client side
		}
		else
		{
			return (double) (distanceSP+distanceRS);
		}



	}
	
	private int manhattanDistance(int x1, int x2, int y1, int y2) {
		
		return Math.abs(x1-x2) + Math.abs(y1-y2);
	}





}



