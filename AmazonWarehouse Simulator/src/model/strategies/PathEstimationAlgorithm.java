package model.strategies;

import model.warehouse.actors.PackingStation;
import model.warehouse.actors.Robot;
import model.warehouse.actors.Shelf;
import model.warehouse.entities.Proposal;

public abstract class PathEstimationAlgorithm extends Algorithm{

 	
 	public PathEstimationAlgorithm(Proposal proposal) {
 		super(proposal);
	}
	
	/**
	 * Calculates total number of steps to take to go from current Robot>Shelf>Packing Station
	 * @return Total number of movements needed to complete journey
	 */
	public abstract Double calculateRSPDistance();
	
	
	
	/**
	 * Distance from robot>charging pod
	 * @return Total number of movements needed to complete journey
	 */
	public abstract Double calculateRCDistance();
	
	

}
