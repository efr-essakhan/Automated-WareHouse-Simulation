package model.strategies;

import model.warehouse.entities.Proposal;

public class Pythagoras extends PathEstimationAlgorithm {
	
 	
 	public Pythagoras(Proposal proposal) {
 		super(proposal);
	}
 	
 	public Double calculateDistance() {
 		
 		//robot to shelf
 		double distanceRS = Math.sqrt(sqNum((shelfX-robotX))+sqNum((shelfY-robotY)));
 		
 		//Shelf to PackingStation
 		double distanceSP = Math.sqrt(sqNum((packingX-shelfX))+sqNum((packingY-shelfY)));
		return distanceSP+distanceRS;
 	}
 	
 	private int sqNum(int num) {
 		
 		return num*num;
 		
 	}

}
