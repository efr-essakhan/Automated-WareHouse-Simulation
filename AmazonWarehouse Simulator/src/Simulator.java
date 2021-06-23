import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import model.warehouse.actors.Actor;
import model.warehouse.actors.ChargingPod;
import model.warehouse.actors.PackingStation;
import model.warehouse.actors.Robot;
import model.warehouse.actors.Shelf;
import model.warehouse.entities.Grid;
import model.warehouse.entities.Order;

public class Simulator {
	
//	private final int GRID_LENGTH; //width of the warehouse grid, in cells. X
//	private final int GRID_HEIGHT; //height of the warehouse grid, in cells Y
//	private final int CAPACITY; //capacity of the battery of all the robots
//	private final int CHARGE_SPEED; //the number of power units that a charging pod recharges per tick
	
	private Grid grid;
	private boolean simulationTerminate;
	private List<Actor> robots;
	private List<Actor> shelfs;
	private List<Actor> packingStations;
	
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
		
		Simulator k = new Simulator(4,4, 20, 1);
		
		k.simulate();
		

	}
	
	public Simulator(int rows, int columns, int capacity, int chargeSpeed) {
		
		grid = new Grid(rows, columns);
		simulationTerminate = false;
		simulationActorSetup(capacity, chargeSpeed);
		
		System.out.println(grid);
	}
	
	public void simulate() {
		
		simulateOneTick();
		simulateOneTick();
		simulateOneTick();
//		simulateOneTick();
//		simulateOneTick();
//		simulateOneTick();
		
//		do {
//			
//		} while (simulationTerminate == false);
//		
	}
	
	
	/**
	 * Packing stations tick, then the charging pods,
	 * the shelves, and ﬁnally the robots. This way, the code in robots can assume that all packing
	 * stations have done what they had to for this tick.
	 */
	public void simulateOneTick() {
		
		for (Actor actor : packingStations) {
			actor.act();
		}
		
		for (Actor actor : robots) {
			Robot r = (Robot) actor;
			r.getChargingPod().act();
		}
		
		for (Actor actor : shelfs) {
			actor.act();
		}
		
		for (Actor actor : robots) {
			actor.act();
		}
		
		//Add to grid
//		grid.addActorsToGrid(robots);
//		grid.addActorsToGrid(shelfs);
//		grid.addActorsToGrid(packingStations);
		
		grid.updateActorsOnGrid();

		
//		grid.addActorsToGrid(actorList);
		System.out.println(grid);

	}
	
	public void simulationActorSetup(int capacity, int chargeSpeed) {
		
		Robot.CAPACITY = capacity;
		ChargingPod.CHARGE_SPEED = chargeSpeed;
		
		robots = new ArrayList<Actor>();
		shelfs = new ArrayList<Actor>();
		packingStations = new ArrayList<Actor>();
		
		robots.add(new Robot(0, 0, "r0", "c0"));
		
		Shelf s = new Shelf(2, 2, "ss0");
		shelfs.add(s);
		
		
		packingStations.add(new PackingStation(0, 2, robots, "ps0"));

		//Create orders
		Order a = new Order(2);
		a.addShelf((Shelf) shelfs.get(0));
		
		//Add the orders to global list utilized by PackingStation
		PackingStation.enterOrder(a);
		
		//Add to grid
		grid.addActorsToGrid(robots);
		grid.addActorsToGrid(shelfs);
		grid.addActorsToGrid(packingStations);

	}


	
}
