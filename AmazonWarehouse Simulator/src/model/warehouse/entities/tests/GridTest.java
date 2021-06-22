package model.warehouse.entities.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.warehouse.actors.*;
import model.warehouse.entities.Grid;
import model.warehouse.entities.Location;
import model.warehouse.entities.Order;

class GridTest {
	
	Grid grid;
	StringBuilder aim;
	StringBuilder sb;

	@BeforeEach
	void setUp() throws Exception {
		
		//Create new grid & add actors.
		grid = new Grid(3, 4);
		
		List<Actor> robots = new ArrayList<Actor>();
		List<Actor> shelf = new ArrayList<Actor>();
		List<Actor> packingStation = new ArrayList<Actor>();
		
		robots.add(new Robot(1, 0, "r0", "c0"));

		shelf.add(new Shelf(2, 2, "ss0"));
		shelf.add(new Shelf(2, 0, "ss0"));

		packingStation.add(new PackingStation(0, 2, robots, "ps0"));
		packingStation.add(new PackingStation(0, 3, robots, "ps0"));
		
		
		grid.addActorsToGrid(robots);
		grid.addActorsToGrid(shelf);
		grid.addActorsToGrid(packingStation);
		
		//Create new string 
		aim = new StringBuilder();
		
//		aim.append(".").append("\t").append(".").append("\t").append(".").append("\n");
//		aim.append("P").append("\t").append(".").append("\t").append("S").append("\n");
//		aim.append(".").append("\t").append(".").append("\t").append(".").append("\n");
//		aim.append(".").append("\t").append("CR").append("\t").append(".").append("\n");
		
		aim.append(".").append("\t").append(".").append("\t").append(".").append("\n");
		aim.append("P").append("\t").append(".").append("\t").append("S").append("\n");
		aim.append(".").append("\t").append(".").append("\t").append(".").append("\n");
		aim.append(".").append("\t").append("CR").append("\t").append(".").append("\n");

		
	}
	
	@Test
	void testPlayWith2DArray() {
		
		sb = new StringBuilder();
		
		for (int i = 0; i < grid.getGrid().length; i++) {
			
			for (int j = 0; j < grid.getGrid()[i].length; j++) {
				sb.append(j + " " + i).append("\t");
			}
			sb.append("\n");
		}
		
//		System.out.println(aim);
		System.out.println(sb);
	}
	
//	@Test
	void testPlayWith2DArrayWithActors() {
		
		sb = new StringBuilder();

		for (int i = 0; i < grid.getGrid().length; i++) {

			for (int j = 0; j < grid.getGrid()[i].length; j++) {
				
				Location loc = (Location) grid.getGrid()[i][j];

				if (loc != null) {
					//TODO: Fix orientation of this grid.
					for (Actor actor : loc.getActors()) {

						if (actor != null) {
							sb.append(actor.toString());
						}


					}

					sb.append("\t");

				}
				else {

					sb.append(".").append("\t");

				}
			}
			sb.append("\n");
		}

		System.out.println(aim);
		System.out.println(sb);
	}


//	@Test
	void testToString() {
		System.out.println(grid.toString());

	}

}
