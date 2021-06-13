import model.Grid;

public class Simulator {
	
	private final int GRID_WIDTH; //width of the warehouse grid, in cells.
	private final int GRID_HEIGHT; //height of the warehouse grid, in cells
	private final int CAPACITY; //capacity of the battery of all the robots
	private final int CHARGE_SPEED; //the number of power units that a charging pod recharges per tick
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Take in a number of parameters.
		//Set those parameters
		
		
		//Will have a loop
		//1) Create the scene grid with the robots etc. all set based on parameters
		
		
		
		//2) Move the simulation based on one tick first.
		//3) A tick constitutes calling act on each actor
		
		Grid g = new Grid(20, 20);
		System.out.println(g.toString());

	}
	
	public Simulator(int gridWidth, int gridHeight, int chargeSpeed, int capacity) {

		this.GRID_WIDTH = gridWidth;
		this.GRID_HEIGHT = gridHeight;
		this.CAPACITY = capacity;
		this.CHARGE_SPEED = chargeSpeed;
	}
	
}
