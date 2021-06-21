package model.strategies;

import model.warehouse.Proposal;
import model.warehouse.entities.PackingStation;
import model.warehouse.entities.Robot;
import model.warehouse.entities.Shelf;

public abstract class PathEstimationAlgorithm extends Algorithm{

 	
 	public PathEstimationAlgorithm(Proposal proposal) {
 		super(proposal);
	}
	
	public abstract Double calculateDistance();
	
	

}
