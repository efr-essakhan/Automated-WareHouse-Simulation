package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GridTest {
	
	Grid grid;

	@BeforeEach
	void setUp() throws Exception {
		grid = new Grid(10,10);
		grid.addActor(new Robot(1,2));
		grid.addActor(new Robot(3,4));
		grid.addActor(new Robot(8,9));
		grid.addActor(new Robot(9,9));
		grid.addActor(new Robot(4,4));
		
	}

	@Test
	void testToString() {
		System.out.println(grid.toString());
	}

}
