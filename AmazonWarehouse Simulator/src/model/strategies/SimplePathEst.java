package model.strategies;

import model.warehouse.entities.Proposal;

//Manhattan
public class SimplePathEst extends PathEstimationAlgorithm {
	
	private int robotCharge;

	public SimplePathEst(Proposal proposal) {
		super(proposal);
		
		robotCharge = selfRobot.getCharge(); 
	}

	@Override
	public Double calculateRSPDistance() {
		
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

	@Override
	public Double calculateRCDistance() {

		//First the Robot to shelf
		int distanceRC = manhattanDistance(chargerX, robotX, chargerY, robotY);
		
		robotCharge = robotCharge-distanceRC; 
		if (robotCharge <= 0) {
			return null; //null represents distance not possible at client side - and the TODO: the simulation should fail because of this.
		}
		else
		{
			return (double) (distanceRC);
		}

	}





}



