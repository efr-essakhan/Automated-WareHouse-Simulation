package model.warehouse.entities.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.warehouse.actors.*;
import model.warehouse.entities.Grid;
import model.warehouse.entities.Order;

class GridTest {
	
	Grid grid;

	@BeforeEach
	void setUp() throws Exception {
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
		a.addShelf((Shelf) actors.get("Shelf").get(0));
		PackingStation.enterOrder(a);
		
		
	}

	@Test
	void testToString() {
		System.out.println(grid.toString());
	}

}
