package model.strategies;

import model.warehouse.actors.Actor;
import model.warehouse.entities.Location;
import model.warehouse.entities.Proposal;
import model.warehouse.entities.State;

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
		Location loc = null;
		try {
			loc = (Location) selfRobot.getLocation().clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		} 
	
		if (targetDisplacement.getY() != 0) { //first move column wise
			
			//find out if targetDisplacement.getX() is neg or pos number
			if (targetDisplacement.getY() > 0) {
				loc.setY(loc.getY() + 1);
				targetDisplacement.setY(targetDisplacement.getY() - 1);
				
			} else if (targetDisplacement.getY() < 0) {
				loc.setY(loc.getY() - 1);
				targetDisplacement.setY(targetDisplacement.getY() + 1);
			}
		} else if (targetDisplacement.getX() != 0) { //Next move row wise
			
			if (targetDisplacement.getX() > 0) {
				loc.setY(loc.getY() + 1);
				targetDisplacement.setY(targetDisplacement.getY() - 1);
				
			} else if (targetDisplacement.getX() < 0) {
				loc.setY(loc.getY() - 1);
				targetDisplacement.setY(targetDisplacement.getY() + 1);
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
