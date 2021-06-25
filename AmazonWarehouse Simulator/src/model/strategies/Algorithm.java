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
 	protected int robotCharge;

	
	public Algorithm() {
		
	}
	
	public void setFieldsForProposal(Proposal proposal) {
		this.targetShelf = proposal.getShelf();
		this.targetPackingStation = proposal.getPackingStation();
		this.selfRobot = proposal.getRobotForTheJob();
		this.robotCharge = proposal.getRobotForTheJob().getCharge();
		
 		shelfX = targetShelf.getLocation().getX();
 		shelfY = targetShelf.getLocation().getY();
 		robotX = selfRobot.getLocation().getX();
 		robotY = selfRobot.getLocation().getY();
 		packingX = targetPackingStation.getLocation().getX();
 		packingY = targetPackingStation.getLocation().getY();
 		chargerX = selfRobot.getChargingPod().getLocation().getX();
 		chargerY = selfRobot.getChargingPod().getLocation().getY();
 		
	}
	
	public void setFieldsForCharging(Robot robot) {
		this.selfRobot = robot;
		
 		robotX = selfRobot.getLocation().getX();
 		robotY = selfRobot.getLocation().getY();
 		chargerX = selfRobot.getChargingPod().getLocation().getX();
 		chargerY = selfRobot.getChargingPod().getLocation().getY();
	}
}
