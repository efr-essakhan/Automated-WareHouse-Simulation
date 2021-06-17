package aston.jpd.warehouse.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import aston.jpd.warehouse.model.entities.ChargingPod;
import aston.jpd.warehouse.model.entities.IEntityVisitor;
import aston.jpd.warehouse.model.entities.Order;
import aston.jpd.warehouse.model.entities.PackingStation;
import aston.jpd.warehouse.model.entities.Robot;
import aston.jpd.warehouse.model.entities.StorageShelf;
import aston.jpd.warehouse.model.warehouse.Warehouse;
import aston.jpd.warehouse.model.warehouse.Warehouse.IdInformation;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;

/**
 * Discrete agent-based simulation for an automated warehouse. Mostly acts as a
 * container, letting all the other components tick in lockstep.
 *
 * Uses JavaFX properties to automatically update observable lists of the
 * various types of actors in its observable lists, allowing for easy binding of
 * the UI.
 *
 * The only property that we can change directly is the warehouse. Orders can
 * only be manipulated through {@link #placeOrder(Order)},
 * {@link #cancelOrder(Order)}, {@link #assign(Order, PackingStation)} and
 * {@link #dispatch(Order)}.
 */
public class Simulation implements MapChangeListener<String, IdInformation> {

	private final ReadOnlyIntegerWrapper tick = new ReadOnlyIntegerWrapper(0);

	private class ListAdderVisitor implements IEntityVisitor {
		@Override
		public void visit(ChargingPod chargingPod) {
			pods.add(chargingPod);
		}

		@Override
		public void visit(PackingStation packingStation) {
			packingStations.add(packingStation);
		}

		@Override
		public void visit(Robot robot) {
			robots.add(robot);
		}

		@Override
		public void visit(StorageShelf shelf) {
			shelves.add(shelf);
		}
	}

	private class ListRemoverVisitor implements IEntityVisitor {
		@Override
		public void visit(ChargingPod chargingPod) {
			pods.remove(chargingPod);
		}

		@Override
		public void visit(PackingStation packingStation) {
			packingStations.remove(packingStation);
		}

		@Override
		public void visit(Robot robot) {
			robots.remove(robot);
		}

		@Override
		public void visit(StorageShelf shelf) {
			shelves.remove(shelf);
		}
	}

	private final ObservableList<PackingStation> packingStations =
		FXCollections.observableArrayList(
			(station) -> new Observable[]{station.currentOrderProperty(), station.waitingToArriveProperty(), station.waitingToAssignProperty(), station.ticksPackingProperty() }
		),
		packingStationsReadOnly = FXCollections.unmodifiableObservableList(packingStations);

	private final ObservableList<Robot> robots = FXCollections.observableArrayList(
			(robot) -> new Observable[]{robot.currentChargeProperty(), robot.stateProperty(), robot.waypointsProperty() }
		),
		robotsReadOnly = FXCollections.unmodifiableObservableList(robots);

	private final ObservableList<StorageShelf> shelves = FXCollections.observableArrayList(),
			shelvesReadOnly = FXCollections.unmodifiableObservableList(shelves);

	private final ObservableList<ChargingPod> pods = FXCollections.observableArrayList(),
			podsReadOnly = FXCollections.unmodifiableObservableList(pods);

	private final ObjectProperty<Warehouse> warehouse = new SimpleObjectProperty<>();

	private final ObservableList<Order> unassignedOrders = FXCollections.observableArrayList(),
			unassignedOrdersReadOnly = FXCollections.unmodifiableObservableList(unassignedOrders);

	private final ObservableList<Order> assignedOrders = FXCollections.observableArrayList(),
			assignedOrdersReadOnly = FXCollections.unmodifiableObservableList(assignedOrders);

	private final ObservableList<Order> dispatchedOrders = FXCollections.observableArrayList(),
			dispatchedOrdersReadOnly = FXCollections.unmodifiableObservableList(dispatchedOrders);

	private final ReadOnlyObjectWrapper<SimulationException> simulationFailure = new ReadOnlyObjectWrapper<>();

	private final BooleanExpression canContinue = Bindings.and(
			Bindings.isNull(simulationFailure),
			Bindings.or(
				Bindings.isNotEmpty(unassignedOrders),
				Bindings.isNotEmpty(assignedOrders)
			));

	public Simulation() {
		warehouse.addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) {
				oldValue.positionsProperty().removeListener(this);

				// Clear all the old information
				packingStations.clear();
				robots.clear();
				shelves.clear();
				pods.clear();
			}

			if (newValue != null) {
				// Load all the new information
				ListAdderVisitor visitor = new ListAdderVisitor();
				for (IdInformation info : newValue.positionsProperty().values()) {
					info.getEntity().accept(visitor);
				}

				// Leave a listener to update based on future changes
				newValue.positionsProperty().addListener(this);
			}
		});
	}

	public ObjectProperty<Warehouse> warehouseProperty() {
		return warehouse;
	}

	/**
	 * Returns an unmodifiable, observable view of all the unassigned orders. To add
	 * new orders, use {@link #
	 */
	public ObservableList<Order> unassignedOrdersProperty() {
		return unassignedOrdersReadOnly;
	}

	/**
	 * Returns an unmodifiable, observable view of all the assigned orders.
	 */
	public ObservableList<Order> assignedOrdersProperty() {
		return assignedOrdersReadOnly;
	}

	/**
	 * Returns an unmodifiable, observable view of all the dispatched orders.
	 */
	public ObservableList<Order> dispatchedOrdersProperty() {
		return dispatchedOrdersReadOnly;
	}

	/**
	 * Returns an unmodifiable view over all the packing stations in the warehouse.
	 * This view is kept automatically up to date by the simulation.
	 */
	public ObservableList<PackingStation> packingStationsProperty() {
		return packingStationsReadOnly;
	}

	/**
	 * Returns an unmodifiable view over all the robots in the warehouse. This view
	 * is kept automatically up to date by the simulation.
	 */
	public ObservableList<Robot> robotsProperty() {
		return robotsReadOnly;
	}

	/**
	 * Returns an unmodifiable view over all the shelves in the warehouse. This view
	 * is kept automatically up to date by the simulation.
	 */
	public ObservableList<StorageShelf> shelvesProperty() {
		return shelvesReadOnly;
	}

	/**
	 * Returns an unmodifiable view over all the charging pods in the warehouse.
	 * This view is kept automatically up to date by the simulation.
	 */
	public ObservableList<ChargingPod> podsProperty() {
		return podsReadOnly;
	}

	/**
	 * Returns a read only property with the current tick of the simulation. Useful
	 * for binding from the UI.
	 */
	public ReadOnlyIntegerProperty currentTickProperty() {
		return tick.getReadOnlyProperty();
	}

	/**
	 * Returns a read only property with the final {@link SimulationException}
	 * generated by {@link #tick()}, if any.
	 */
	public ReadOnlyObjectProperty<SimulationException> simulationFailureProperty() {
		return simulationFailure.getReadOnlyProperty();
	}

	/**
	 * Places a new order at the end of the queue of this simulation.
	 */
	public void placeOrder(Order order) {
		unassignedOrders.add(order);
	}

	/**
	 * Removes an unassigned order from the queue of this simulation.
	 */
	public void cancelOrder(Order order) {
		unassignedOrders.remove(order);
	}

	/**
	 * If unassigned, marks an order as assigned to a packing station. Otherwise, it
	 * throws an exception.
	 * 
	 * @throws IllegalStateException
	 *             The order was not assigned at this moment.
	 */
	public void assign(Order order, PackingStation packingStation) {
		if (unassignedOrders.remove(order)) {
			assignedOrders.add(order);
		} else {
			throw new IllegalStateException("The order " + order + " was not unassigned");
		}
	}

	/**
	 * If assigned, marks an order as dispatched. The simulation will end when all
	 * orders are dispatched. If the order is not assigned, it throws an exception.
	 *
	 * @throws IllegalStateException
	 *             The order was not assigned at this moment.
	 */
	public void dispatch(Order order) {
		if (assignedOrders.remove(order)) {
			dispatchedOrders.add(order);
		} else {
			throw new IllegalStateException("The order " + order + " was not assigned");
		}
	}

	/**
	 * Returns an observable expression over whether this simulation can continue
	 * for one more tick, or not. Normally, the simulation will continue until it
	 * either fails, or all orders are dispatched.
	 * 
	 * @return Observable boolean expression that produces <code>true</code> if this
	 *         simulation has finished, or <code>false</code> if it has not.
	 */
	public BooleanExpression canContinue() {
		return canContinue;
	}

	/**
	 * Advances the simulation by one tick.
	 */
	public void tick() throws SimulationException {
		try {
			if (canContinue.get()) {
				tick.set(tick.get() + 1);

				for (PackingStation station : packingStations) {
					station.tick();
				}
				for (ChargingPod pod : pods) {
					pod.tick();
				}
				for (StorageShelf shelf : shelves) {
					shelf.tick();
				}

				// Defensive copy - robots may appear and disappear temporarily while they move
				for (Robot robot : new ArrayList<>(robots)) {
					robot.tick();
				}
			}
		} catch (SimulationException ex) {
			simulationFailure.set(ex);
			throw ex;
		} catch (Throwable t) {
			t.printStackTrace();

			final SimulationException ex = new SimulationException(t);
			simulationFailure.set(ex);
			throw ex;
		}
	}

	@Override
	public void onChanged(Change<? extends String, ? extends IdInformation> change) {
		if (change.wasRemoved()) {
			change.getValueRemoved().getEntity().accept(new ListRemoverVisitor());
		}
		if (change.wasAdded()) {
			change.getValueAdded().getEntity().accept(new ListAdderVisitor());
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Simulation)) {
			return false;
		}
		final Simulation otherSim = (Simulation) o;

		final Warehouse myWarehouse = warehouseProperty().get();
		final Warehouse otherWarehouse = otherSim.warehouseProperty().get();

		if (myWarehouse == otherWarehouse) {
			return true;
		} else if (myWarehouse == null) {
			return false;
		}

		return myWarehouse.equals(otherWarehouse);
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("Simulation [");
		if (tick != null) {
			builder.append("tick=");
			builder.append(tick);
			builder.append(",\n");
		}
		if (packingStations != null) {
			builder.append("packingStations=");
			builder.append(toString(packingStations, maxLen));
			builder.append(",\n");
		}
		if (robots != null) {
			builder.append("robots=");
			builder.append(toString(robots, maxLen));
			builder.append(",\n");
		}
		if (shelves != null) {
			builder.append("shelves=");
			builder.append(toString(shelves, maxLen));
			builder.append(",\n");
		}
		if (pods != null) {
			builder.append("pods=");
			builder.append(toString(pods, maxLen));
			builder.append(",\n");
		}
		if (warehouse != null) {
			builder.append("warehouse=");
			builder.append(warehouse);
			builder.append(",\n");
		}
		if (unassignedOrders != null) {
			builder.append("unassignedOrders=");
			builder.append(toString(unassignedOrders, maxLen));
			builder.append(",\n");
		}
		if (assignedOrders != null) {
			builder.append("assignedOrders=");
			builder.append(toString(assignedOrders, maxLen));
			builder.append(",\n");
		}
		if (dispatchedOrders != null) {
			builder.append("dispatchedOrders=");
			builder.append(toString(dispatchedOrders, maxLen));
		}
		builder.append("]");
		return builder.toString();
	}

}
