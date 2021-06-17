package model;

public class Pythagoras {
	
	private Shelf targetShelf;
	private PackingStation targetPackingStation;
 	private Robot self;
 	
 	public Pythagoras(Proposal proposal) {
		this.targetShelf = proposal.getShelf();
		this.targetPackingStation = proposal.getPackingStation();
		this.self = proposal.getRobotForTheJob();
	}

}
