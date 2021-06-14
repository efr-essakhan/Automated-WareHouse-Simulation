//package model;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//class GridTest {
//	
//	Grid grid;
//
//	@BeforeEach
//	void setUp() throws Exception {
//		grid = new Grid(4,4);
//		Actor a = new Robot(2,2);
//		Actor b = new Robot(1,1);
//		Actor c = new Robot(2,2);
//		Actor x = new Robot(2,2);
//		
//		Actor d = new ChargingPod(a);
//		Actor e = new ChargingPod(b);
//		Actor f = new ChargingPod(c);
//		
//		grid.addActorToGrid(a);
//		grid.addActorToGrid(b);
//		grid.addActorToGrid(c);
//		grid.addActorToGrid(d);
//		grid.addActorToGrid(x);
//		
//		
//	}
//
//	@Test
//	void testToString() {
//		System.out.println(grid.toString());
//	}
//
//}
