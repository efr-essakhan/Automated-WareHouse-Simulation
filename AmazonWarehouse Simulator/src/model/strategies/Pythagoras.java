package model.strategies;

import model.warehouse.actors.Robot;
import model.warehouse.entities.Proposal;

public class Pythagoras extends PathEstimationAlgorithm {
	
 	
 	public Pythagoras() {
 		super();
	}
 	
 	public Double calculateRSPDistance(Proposal proposal) {
 		
 		//robot to shelf
 		double distanceRS = Math.sqrt(sqNum((shelfX-robotX))+sqNum((shelfY-robotY)));
 		
 		//Shelf to PackingStation
 		double distanceSP = Math.sqrt(sqNum((packingX-shelfX))+sqNum((packingY-shelfY)));
		return distanceSP+distanceRS;
 	}
 	
 	private int sqNum(int num) {
 		
 		return num*num;
 		
 	}

	@Override
	public Double calculateRCDistance(Robot robot) {
		// TODO Auto-generated method stub
		return null;
	}

}
