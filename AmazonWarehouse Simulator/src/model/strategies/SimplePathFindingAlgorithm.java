package model.strategies;

import model.warehouse.Location;
import model.warehouse.Proposal;
import model.warehouse.State;
import model.warehouse.entities.Actor;

public class SimplePathFindingAlgorithm extends PathFindingAlgorithm{

	
	public SimplePathFindingAlgorithm(Proposal proposal) {
		super(proposal);
	}
	
	@Override
	protected Location calcDisplacementBasedOnState() {
		
		Location displacement = null;
		
		if (selfRobot.getState() == State.COLLECTING) { 
			//First the Robot to shelf
			 displacement = calculateDisplacement(shelfX, robotX, shelfY, robotY);
			
		}else if (selfRobot.getState() == State.COLLECTED) {
			
			//Now the shelf to station
			 displacement = calculateDisplacement(packingX, shelfX, packingY, shelfY);
		}else if (selfRobot.getState() == State.DISPATCHED) {
			
			//For guarantee that the robot won't be stranded after the job, station to charger
			 displacement = calculateDisplacement(chargerX, packingX, chargerY, packingY);
		}

		return displacement;
	}
	
	private Location calculateDisplacement(int x1, int x2, int y1, int y2) {
		Location displacement = new Location(x1-x2, y1-y2);
		
		return displacement;
		
	}
	
	public void setNewTargetDisplacement() {
		targetDisplacement = calcDisplacementBasedOnState();
	}

	/**
	 * Provides a location that gives the next location for the robot the placed on to move towards the target.
	 */
	@Override
	public Location getNewLocationForRobot() {
		
		setNewTargetDisplacement(); //hold an instance reference to do this as you will be subtracting from it.
		
		//add to current robot loc the x first.
		
		Location loc = selfRobot.getLocation();
		
		if (targetDisplacement.getX() != 0) { //first move column wise
			
			//find out if targetDisplacement.getX() is neg or pos number
			if (targetDisplacement.getX() > 0) {
				loc.setX(loc.getX() + 1);
				targetDisplacement.setX(targetDisplacement.getX() - 1);
				
			} else if (targetDisplacement.getX() < 0) {
				loc.setX(loc.getX() - 1);
				targetDisplacement.setX(targetDisplacement.getX() + 1);
			}
		} else if (targetDisplacement.getY() != 0) { //Next move row wise
			
			if (targetDisplacement.getY() > 0) {
				loc.setX(loc.getX() + 1);
				targetDisplacement.setX(targetDisplacement.getX() - 1);
				
			} else if (targetDisplacement.getY() < 0) {
				loc.setX(loc.getX() - 1);
				targetDisplacement.setX(targetDisplacement.getX() + 1);
			}
			
		} 
		
		//If New Location is just the old location then:
		if (loc == selfRobot.getLocation()) {
			return null; //If new Location == Old location, that means that there is no more displacement, hence return a null, indicating no more displacement.
		}else {
			return loc;
		}
		
		
	}
	
	

}
