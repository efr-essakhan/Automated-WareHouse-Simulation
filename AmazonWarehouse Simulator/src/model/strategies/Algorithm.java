package model.strategies;

import model.warehouse.actors.PackingStation;
import model.warehouse.actors.Robot;
import model.warehouse.actors.Shelf;
import model.warehouse.entities.Proposal;

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
		
 		shelfX = targetShelf.getLocation().getY();
 		shelfY = targetShelf.getLocation().getX();
 		robotX = selfRobot.getLocation().getY();
 		robotY = selfRobot.getLocation().getX();
 		packingX = targetPackingStation.getLocation().getY();
 		packingY = targetPackingStation.getLocation().getX();
 		chargerX = selfRobot.getChargingPod().getLocation().getY();
 		chargerY = selfRobot.getChargingPod().getLocation().getX();
	}

}
