package aston.jpd.warehouse.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import aston.jpd.warehouse.model.Simulation;
import aston.jpd.warehouse.model.entities.ChargingPod;
import aston.jpd.warehouse.model.entities.IEntity;
import aston.jpd.warehouse.model.entities.Order;
import aston.jpd.warehouse.model.warehouse.Position;
import aston.jpd.warehouse.model.warehouse.Warehouse;

/**
 * Writes a {@link Simulation} back into a file, in the format expected by
 * {@link SimulationReader}.
 */
public class SimulationWriter {

	/**
	 * Writes a simulation into a file. Robots are saved parked in their charging
	 * pods.
	 *
	 * @param sim
	 *            Simulation to be saved.
	 * @param writer
	 *            Destination for the characters of the file.
	 * @throws IOException
	 *             Error while saving the simulation.
	 * @throws IllegalStateException
	 *             The simulation is in an internally inconsistent state.
	 * @throws IllegalArgumentException
	 *             The simulation does not have a warehouse.
	 */
	public void write(Writer writer, Simulation sim) throws IOException {
		try (BufferedWriter bW = new BufferedWriter(writer)) {
			writeLine(bW, SimulationReader.CMD_FORMAT, SimulationReader.FORMAT_VERSION);

			final Warehouse warehouse = sim.warehouseProperty().get();
			if (warehouse == null) {
				throw new IllegalArgumentException("Simulation has no warehouse set");
			}
			writeLine(bW, SimulationReader.CMD_WIDTH, warehouse.getWidth());
			writeLine(bW, SimulationReader.CMD_HEIGHT, warehouse.getWidth());

			final Iterator<ChargingPod> itPod = sim.podsProperty().iterator();
			if (itPod.hasNext()) {
				ChargingPod pod = itPod.next();
				writeLine(bW, SimulationReader.CMD_CAPACITY, pod.getRobot().getMaximumCharge());
				writeLine(bW, SimulationReader.CMD_CHARGESPEED, pod.getChargingSpeed());
			}

			writePodsRobots(bW, sim);
			writeEntities(bW, sim, sim.shelvesProperty(), "shelf", SimulationReader.CMD_SHELF);
			writeEntities(bW, sim, sim.packingStationsProperty(), "packing station", SimulationReader.CMD_STATION);

			writeOrders(sim, bW, sim.unassignedOrdersProperty());
			writeOrders(sim, bW, sim.assignedOrdersProperty());
			writeOrders(sim, bW, sim.dispatchedOrdersProperty());
		}
	}

	private void writeOrders(Simulation sim, BufferedWriter bW, Iterable<Order> orders) throws IOException {
		for (Order o : orders) {
			final List<String> lineParts = new ArrayList<>(2 + o.getShelves().size());
			lineParts.add(SimulationReader.CMD_ORDER);
			lineParts.add(o.getPackingTicks() + "");
			o.getShelves().stream().map(s -> s.getIdentifier()).forEach(lineParts::add);

			writeLine(bW, lineParts);
		}
	}

	private void writeEntities(BufferedWriter bW, Simulation sim, Iterable<? extends IEntity> entities,
			String entityType, String command) throws IOException {
		for (IEntity entity : entities) {
			final Warehouse warehouse = sim.warehouseProperty().get();
			final Position p = warehouse.getPosition(entity);
			if (p == null) {
				throw new IllegalStateException(String.format("Simulation contains %s %s but it lacks a position",
						entityType, entity.getIdentifier()));
			}

			writeLine(bW, command, entity.getIdentifier(), p.getColumn(), p.getRow());
		}
	}

	private void writePodsRobots(BufferedWriter bW, Simulation sim) throws IOException {
		for (ChargingPod pod : sim.podsProperty()) {
			final Warehouse warehouse = sim.warehouseProperty().get();
			final Position p = warehouse.getPosition(pod);
			if (p == null) {
				throw new IllegalStateException(
						"Simulation contains pod" + pod.getIdentifier() + " but it lacks a position");
			}

			writeLine(bW, SimulationReader.CMD_PODROBOT, pod.getIdentifier(), pod.getRobot().getIdentifier(),
					p.getColumn(), p.getRow());
		}
	}

	/**
	 * Convenience version of {@link #writeLine(BufferedWriter, Iterable)}.
	 */
	private void writeLine(BufferedWriter writer, Object... args) throws IOException {
		writeLine(writer, Arrays.asList(args));
	}

	/**
	 * Writes a list of objects as a line.
	 */
	private void writeLine(BufferedWriter writer, Iterable<?> args) throws IOException {
		boolean first = true;
		for (Object arg : args) {
			if (first) {
				first = false;
			} else {
				writer.write(" ");
			}
			writer.write(arg.toString());
		}
		writer.newLine();
	}
}
