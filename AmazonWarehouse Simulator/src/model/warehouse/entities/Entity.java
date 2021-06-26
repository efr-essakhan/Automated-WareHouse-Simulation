package model.warehouse.entities;

public abstract class Entity {
	
	protected static void endSimulation(String messageToPrint) {
		System.out.println();
		System.out.println(messageToPrint);
		System.exit(0);
	}

}
