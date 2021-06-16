import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import model.Actor;
import model.ChargingPod;
import model.Grid;
import model.Order;
import model.PackingStation;
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
//	private List<Actor> actors;
	private Map<String, List<Actor>> actors;
	
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
		actors = new HashMap<>();
		simulationActorSetup(capacity, chargeSpeed);
		
	
		
		
	}
	
	public void simulate() {
		
		do {
			
		} while (simulationTerminate == false);
		
	}
	
	
	/**
	 * Packing stations tick, then the charging pods,
	 * the shelves, and ﬁnally the robots. This way, the code in robots can assume that all packing
	 * stations have done what they had to for this tick.
	 */
	public void simulateOneTick() {

		List<Actor> actorList = actors.get("PackingStation");
		
		for (Actor actor : actorList) {
			actor.act();
		}
		
		actorList = actors.get("Robot");
		
		for (Actor actor : actorList) {
			Robot r = (Robot) actor;
			r.getChargingPod().act();
		}
		
		actorList = actors.get("Shelf");
		
		for (Actor actor : actorList) {
			actor.act();
		}
		
		actorList = actors.get("Robot");
		
		for (Actor actor : actorList) {
			actor.act();
		}

	}
	
	public void simulationActorSetup(int capacity, int chargeSpeed) {
		
		Robot.CAPACITY = capacity;
		ChargingPod.CHARGE_SPEED = chargeSpeed;
		
		List<Actor> robots = new ArrayList<Actor>();
		robots.add(new Robot(2, 0, "r0", "c0"));
		actors.put("Robot", robots);
		
		List<Actor> shelf = new ArrayList<Actor>();
		Shelf s = new Shelf(2, 2, "ss0");
		shelf.add(s);
		actors.put("Shelf", shelf);
		
		List<Actor> packingStation = new ArrayList<Actor>();
		packingStation.add(new PackingStation(0, 2, actors.get("Robot"), "ps0"));
		actors.put("PackingStation", packingStation);

		Order a = new Order(2);
		a.addShelf((Shelf) actors.get(1));
		PackingStation.enterOrder(a);
		

		//Add to grid.
		for (List<Actor> actors : actors.values()) {
			
			grid.addActorsToGrid(actors);
			
		}

		System.out.println(grid.toString());
	}


	
}
