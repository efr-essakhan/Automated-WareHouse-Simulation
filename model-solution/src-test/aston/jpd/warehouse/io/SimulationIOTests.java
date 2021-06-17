package aston.jpd.warehouse.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import aston.jpd.warehouse.io.SimulationReader.SimulationReaderException;
import aston.jpd.warehouse.model.Simulation;
import aston.jpd.warehouse.model.entities.ChargingPod;
import aston.jpd.warehouse.model.entities.Order;
import aston.jpd.warehouse.model.entities.PackingStation;
import aston.jpd.warehouse.model.entities.Robot;
import aston.jpd.warehouse.model.entities.StorageShelf;
import aston.jpd.warehouse.model.warehouse.Warehouse;

/**
 * Tests the saving and loading of simulations.
 */
public class SimulationIOTests {

	@Test
	public void emptyFile() throws Exception {
		assertThrows(SimulationReaderException.class, () -> {
			File fTmp = Files.createTempFile("test", "sim").toFile();
			fTmp.deleteOnExit();
			new SimulationReader().load(new FileReader(fTmp));
		});
	}

	@Test
	public void unsetWarehouse() throws Exception {
		assertThrows(IllegalArgumentException.class,
			() -> assertSaveLoad(new Simulation()));
	}

	@Test
	public void emptyWarehose() throws Exception {
		final Simulation sim = new Simulation();
		sim.warehouseProperty().set(new Warehouse(5, 5));
		assertSaveLoad(sim);
	}

	@Test
	public void oneShelf() throws Exception {
		final Simulation sim = new Simulation();
		final Warehouse w = new Warehouse(4, 4);
		sim.warehouseProperty().set(w);

		w.placeEntity(new StorageShelf("treasures"), 2, 2);
		assertSaveLoad(sim);
	}

	@Test
	public void oneStation() throws Exception {
		final Simulation sim = new Simulation();
		final Warehouse w = new Warehouse(4, 4);
		sim.warehouseProperty().set(w);

		w.placeEntity(new PackingStation("kingscross"), 2, 2);
		assertSaveLoad(sim);
	}

	@Test
	public void onePodRobot() throws Exception {
		final Simulation sim = new Simulation();
		final Warehouse w = new Warehouse(4, 4);
		sim.warehouseProperty().set(w);

		final Robot r = new Robot("c3po");
		r.setMaximumCharge(40);
		final ChargingPod pod = new ChargingPod("racer", r);
		pod.setChargingSpeed(5);

		w.placeEntity(pod, 2, 0);
		w.placeEntity(r, 2, 0);
		assertSaveLoad(sim);
	}

	@Test
	public void fullConfig() throws Exception {
		final Simulation sim = new Simulation();
		final Warehouse w = new Warehouse(4, 4);
		sim.warehouseProperty().set(w);

		final Robot r = new Robot("c3po");
		r.setMaximumCharge(40);
		final ChargingPod pod = new ChargingPod("racer", r);
		pod.setChargingSpeed(5);
		w.placeEntity(pod, 2, 0);
		w.placeEntity(r, 2, 0);

		w.placeEntity(new PackingStation("kingscross"), 2, 2);
		final StorageShelf shelf = new StorageShelf("treasures");
		w.placeEntity(shelf, 0, 2);

		sim.placeOrder(new Order(5, shelf));

		assertSaveLoad(sim);
		assertEquals(5, sim.unassignedOrdersProperty().iterator().next().getPackingTicks());
	}

	/**
	 * Checks if we can load and save the simulation and get equivalent results.
	 */
	private void assertSaveLoad(final Simulation sim) throws Exception {
		final File fTemp = Files.createTempFile("simio", "conf").toFile();
		try {
			new SimulationWriter().write(new FileWriter(fTemp), sim);
			final Simulation loaded = new SimulationReader().load(new FileReader(fTemp));
			assertEquals(sim, loaded);
		} finally {
			if (fTemp.exists()) {
				fTemp.delete();
			}
		}
	}

	@Test
	public void oldMacSeparatorsWork() throws Exception {
		SimulationReader reader = new SimulationReader();

		// Uses old Mac CR line separators
		Simulation simCR = reader.load(new FileReader(new File("configs/bottomStationsOldMac.sim")));
		// Uses UNIX LF line separators
		Simulation simLF = reader.load(new FileReader(new File("configs/bottomStations.sim")));

		assertEquals(4, simCR.robotsProperty().size());
		assertEquals(simCR, simLF);
	}

	@Test
	public void winSeparatorsWork() throws Exception {
		SimulationReader reader = new SimulationReader();

		// Uses Windows CRLF line separators
		Simulation simCRLF = reader.load(new FileReader(new File("configs/bottomStationsWin.sim")));
		// Uses UNIX LF line separators
		Simulation simLF = reader.load(new FileReader(new File("configs/bottomStations.sim")));

		assertEquals(4, simCRLF.robotsProperty().size());
		assertEquals(simCRLF, simLF);
	}
}
