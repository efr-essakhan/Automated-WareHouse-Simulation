package model.strategies;

import model.warehouse.Proposal;
import model.warehouse.entities.PackingStation;
import model.warehouse.entities.Robot;
import model.warehouse.entities.Shelf;

public abstract class Algorithm {
	
	protected Shelf targetShelf;
	protected PackingStation targetPackingStation;
 	protected Robot selfRobot;
 	protected int shelfX;
 	protected int shelfY;
 	protected int robotX;
 	protected int robotY;
 	protected int packingX;
 	protected int packingY;
 	protected int chargerX;
 	protected int chargerY;

	
	public Algorithm(Proposal proposal) {
		this.targetShelf = proposal.getShelf();
		this.targetPackingStation = proposal.getPackingStation();
		this.selfRobot = proposal.getRobotForTheJob();
		
 		shelfX = targetShelf.getLocation().getX();
 		shelfY = targetShelf.getLocation().getY();
 		robotX = selfRobot.getLocation().getX();
 		robotY = selfRobot.getLocation().getY();
 		packingX = targetPackingStation.getLocation().getX();
 		packingY = targetPackingStation.getLocation().getY();
 		chargerX = selfRobot.getChargingPod().getLocation().getX();
 		chargerY = selfRobot.getChargingPod().getLocation().getY();
	}

}
