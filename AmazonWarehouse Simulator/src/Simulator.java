import model.Actor;
import model.ChargingPod;
import model.Grid;
import model.Robot;

public class Simulator {
	
//	private final int GRID_LENGTH; //width of the warehouse grid, in cells. X
//	private final int GRID_HEIGHT; //height of the warehouse grid, in cells Y
//	private final int CAPACITY; //capacity of the battery of all the robots
//	private final int CHARGE_SPEED; //the number of power units that a charging pod recharges per tick
	
	private Grid grid;
	private boolean simulationTerminate;
	
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
		
		Simulator k = new Simulator(10,10);

	}
	
	public Simulator(int gridLength, int gridHeight) {
		
		grid = new Grid(gridLength, gridHeight);
		simulationTerminate = false;
		simulationActorSetup();
		
		
	}
	
	public void simulate() {
		
		do {
			
		} while (simulationTerminate == false);
		
	}
	
	public void simulateOneTick() {
		
		
		
	}
	
	public void simulationActorSetup() {
		
		grid.addActorToGrid(new Robot(1, 1));
		grid.addActorToGrid(new Robot(1, 2));
		grid.addActorToGrid(new Robot(1, 1));
		
		System.out.println(grid.toString());
	}
	
}
