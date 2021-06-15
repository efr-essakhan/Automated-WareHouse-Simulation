import java.util.ArrayList;
import java.util.List;

import model.Actor;
import model.ChargingPod;
import model.Grid;
import model.Order;
import model.Robot;
import model.Shelf;

public class Simulator {
	
//	private final int GRID_LENGTH; //width of the warehouse grid, in cells. X
//	private final int GRID_HEIGHT; //height of the warehouse grid, in cells Y
//	private final int CAPACITY; //capacity of the battery of all the robots
//	private final int CHARGE_SPEED; //the number of power units that a charging pod recharges per tick
	
	private Grid grid;
	private boolean simulationTerminate;
	private ArrayList<Order> orders;
	private List<Actor> actors;
	
	public static void main(String[] args) { //Changes array from String[]
		
		//Setting up.
//		Robot.CAPACITY = args[2];
//		ChargingPod.CHARGE_SPEED = args[3];
		
		//Take in a number of parameters.
		//Set those parameters
		
		
		//Will have a loop
		//1) Create the scene grid with the robots etc. all set based on parameters
		
		
		
		//2) Move the simulation based on one tick first.
		//3) A tick constitutes calling act on each actor
		
		Simulator k = new Simulator(3,3, 20, 1);

	}
	
	public Simulator(int gridLength, int gridHeight, int capacity, int chargeSpeed) {
		
		grid = new Grid(gridLength, gridHeight);
		simulationTerminate = false;
		actors = new ArrayList<Actor>();
		simulationActorSetup(capacity, chargeSpeed);
		
		
	
		
		
	}
	
	public void simulate() {
		
		do {
			
		} while (simulationTerminate == false);
		
	}
	
	public void simulateOneTick() {
		
		
		
		
	}
	
	public void simulationActorSetup(int capacity, int chargeSpeed) {
		
		Robot.CAPACITY = capacity;
		ChargingPod.CHARGE_SPEED = chargeSpeed;
		
		
		actors.add(new Robot(1, 1, "r0"));
		actors.add(new Robot(1, 2));
		actors.add(new Robot(1, 1));
		actors.add(new Shelf(9, 9));
		
		grid.addActorsToGrid(actors);
		
		System.out.println(grid.toString());
	}

//	public List<Actor> getActor() {
//		return actor;
//	}

	
}
