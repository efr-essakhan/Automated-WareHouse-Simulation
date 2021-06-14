import model.ChargingPod;
import model.Grid;
import model.Robot;

public class Simulator {
	
//	private final int GRID_LENGTH; //width of the warehouse grid, in cells. X
//	private final int GRID_HEIGHT; //height of the warehouse grid, in cells Y
//	private final int CAPACITY; //capacity of the battery of all the robots
//	private final int CHARGE_SPEED; //the number of power units that a charging pod recharges per tick
	
	private Grid grid;
	
	public static void main(Integer[] args) { //Changes array from String[]
		
		//Setting up.
		Robot.CAPACITY = args[2];
		ChargingPod.CHARGE_SPEED = args[3];
		
		//Take in a number of parameters.
		//Set those parameters
		
		
		//Will have a loop
		//1) Create the scene grid with the robots etc. all set based on parameters
		
		
		
		//2) Move the simulation based on one tick first.
		//3) A tick constitutes calling act on each actor
		
		Grid grid = new Grid(20, 20);
		System.out.println(grid.toString());

	}
	
	public Simulator(int gridLength, int gridHeight, int chargeSpeed, int capacity) {
		
		grid = new Grid(gridLength, gridHeight);
		
		
	}
	
}
