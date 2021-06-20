package model.strategies;

import model.warehouse.Proposal;
import model.warehouse.Location;
import model.warehouse.entities.Actor;

public abstract class PathFindingAlgorithm extends Algorithm {
	
	protected Location targetDisplacement; //Holds the total column and row displacement a robot needs to make to reach target location.
	protected Location newDisplacementForRobot; //Holds the coordinates to move on tick to move towards target displacement.
	
	
 	public PathFindingAlgorithm(Proposal proposal) {
 		super(proposal);
	}
 	
	public void setNewTargetDisplacement() {
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
