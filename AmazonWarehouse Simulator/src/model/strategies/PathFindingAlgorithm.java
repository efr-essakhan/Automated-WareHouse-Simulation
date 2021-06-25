package model.strategies;

import model.warehouse.actors.Actor;
import model.warehouse.entities.Location;
import model.warehouse.entities.Proposal;

public abstract class PathFindingAlgorithm extends Algorithm {
	
	protected Location targetDisplacement; //Holds the total column and row displacement a robot needs to make to reach target location.
	protected Location newDisplacementForRobot; //Holds the coordinates to move on tick to move towards target displacement.
	
	
 	public PathFindingAlgorithm() {
 		super();
	}
 	
	/**
	 * Sets the remaining Y and X values to traverse for the robot to reach its destination.
	 */
	public void setNewTargetDisplacementBasedOnState() { 
		targetDisplacement = calcDisplacementBasedOnState();
	}
 	
 	/**
 	 * Called to get the displacement x,y for a robot to the actor location.
 	 * @param actor to go to
 	 * @return Location object holding the X and Y moveset.
 	 */
 	protected abstract Location calcDisplacementBasedOnState();
 	
 	public abstract Location getNewLocationForRobot();
 	
	

}
