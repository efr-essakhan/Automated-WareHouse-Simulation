package aston.jpd.warehouse.model.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import aston.jpd.warehouse.model.SimulationException;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.collections.FXCollections;

/**
 * Represents a single packing station in the simulation. Tries to acquire
 * orders from the main simulation, and when it obtains one it will try to find
 * robots that can take on the transport it requires.
 */
public class PackingStation extends Entity {

	private final ReadOnlyObjectWrapper<Order> currentOrder = new ReadOnlyObjectWrapper<>();
	private final ReadOnlySetWrapper<StorageShelf> waitingToAssign = new ReadOnlySetWrapper<>(
			FXCollections.observableSet(new HashSet<>()));
	private final ReadOnlyMapWrapper<Robot, StorageShelf> waitingToArrive = new ReadOnlyMapWrapper<>(
			FXCollections.observableMap(new HashMap<>()));
	private final ReadOnlyIntegerWrapper ticksPacking = new ReadOnlyIntegerWrapper(0);

	public PackingStation(String id) {
		super(id);
	}

	@Override
	public boolean canCoexist(IEntity other) {
		// You can only have a robot together with a packing station
		return other instanceof Robot;
	}

	public ReadOnlyObjectProperty<Order> currentOrderProperty() {
		return currentOrder.getReadOnlyProperty();
	}

	public ReadOnlySetProperty<StorageShelf> waitingToAssignProperty() {
		return waitingToAssign.getReadOnlyProperty();
	}

	public ReadOnlyMapProperty<Robot, StorageShelf> waitingToArriveProperty() {
		return waitingToArrive.getReadOnlyProperty();
	}

	public ReadOnlyIntegerProperty ticksPackingProperty() {
		return ticksPacking.getReadOnlyProperty();
	}

	@Override
	public void tick() throws SimulationException {
		if (currentOrder.get() == null && !simulation.unassignedOrdersProperty().isEmpty()) {
			currentOrder.set(simulation.unassignedOrdersProperty().get(0));
			simulation.assign(currentOrder.get(), this);

			waitingToAssign.clear();
			waitingToArrive.clear();

			waitingToAssign.addAll(currentOrder.get().getShelves());
			ticksPacking.set(0);
		}

		if (currentOrder.get() != null) {
			// Try to assign robots to the shelves we haven't visited yet:
			// we might not have enough robots available on this tick to ask them
			for (Iterator<StorageShelf> itShelf = waitingToAssign.iterator(); itShelf.hasNext();) {
				final StorageShelf shelf = itShelf.next();

				for (Robot r : simulation.robotsProperty()) {
					if (r.acceptMission(this, shelf)) {
						itShelf.remove();
						waitingToArrive.put(r, shelf);
						break;
					}
				}
			}

			// Check who has arrived here
			for (IEntity e : getEntitiesInSamePosition()) {
				if (e instanceof Robot) {
					Robot r = (Robot) e;

					// If we were waiting for this robot, take its items and let it go
					StorageShelf shelf = waitingToArrive.get(r);
					if (shelf != null && r.releaseMission(this, shelf)) {
						waitingToArrive.remove(r);
					}
				}
			}

			// If all the things we wanted are here, then start packing the order
			if (waitingToArrive.isEmpty() && waitingToAssign.isEmpty()) {
				if (ticksPacking.get() >= currentOrder.get().getPackingTicks()) {
					simulation.dispatch(currentOrder.get());
					currentOrder.set(null);
				} else {
					ticksPacking.set(ticksPacking.get() + 1);
				}
			}
		}
	}

	@Override
	public void accept(IEntityVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		if (currentOrder.get() == null) {
			return String.format("%s (idle)", getIdentifier());
		} else {
			
			final List<String> sArriving = waitingToArrive .entrySet().stream().map((e) ->
				String.format("%s(%s)", e.getKey().getIdentifier(), e.getValue().getIdentifier())
			).collect(Collectors.toList());

			return String.format("%s (%d/%d, waiting to arrive: %s)",
					getIdentifier(),
					ticksPacking.get(),
					currentOrder.get().getPackingTicks(),
					sArriving);
		}
	}
}
