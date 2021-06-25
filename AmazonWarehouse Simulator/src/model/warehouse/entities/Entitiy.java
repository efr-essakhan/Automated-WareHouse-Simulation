package model.warehouse.entities;

public abstract class Entitiy {
	
	protected void endSimulation(String messageToPrint) {
		System.out.println();
		System.out.println(messageToPrint);
		System.exit(0);
	}

}
