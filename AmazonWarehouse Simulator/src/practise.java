import model.warehouse.entities.Entity;



public class practise extends Entity {
	public static void main(String args[])
	{
		int x = 0;
		while (true) {
			x++;
			
			if (x == 100000) {
				
				endSimulation("END " + x);
			}

			if (x == 1000000000) {
				endSimulation("END " + x);
			}
			
		}
	}
}



