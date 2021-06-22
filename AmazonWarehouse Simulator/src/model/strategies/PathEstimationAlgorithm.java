package model.strategies;

import model.warehouse.actors.PackingStation;
import model.warehouse.actors.Robot;
import model.warehouse.actors.Shelf;
import model.warehouse.entities.Proposal;

public abstract class PathEstimationAlgorithm extends Algorithm{

 	
 	public PathEstimationAlgorithm(Proposal proposal) {
 		super(proposal);
	}
	
	public abstract Double calculateDistance();
	
	

}
