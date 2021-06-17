package aston.jpd.warehouse.ui.config;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import aston.jpd.warehouse.model.Simulation;
import aston.jpd.warehouse.model.entities.ChargingPod;
import aston.jpd.warehouse.model.entities.Order;
import aston.jpd.warehouse.model.entities.Robot;
import aston.jpd.warehouse.model.entities.StorageShelf;
import aston.jpd.warehouse.model.warehouse.Warehouse;
import aston.jpd.warehouse.model.warehouse.Warehouse.IdInformation;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;

/**
 * Configuration model for setting up the simulation.
 */
public class ConfigurationModel {
	private LongProperty seed = new SimpleLongProperty(System.currentTimeMillis());
	private ObjectProperty<ToolbarState> state = new SimpleObjectProperty<>();
	private IntegerProperty capacity = new SimpleIntegerProperty(Robot.DEFAULT_MAX_CHARGE);
	private IntegerProperty chargeSpeed = new SimpleIntegerProperty(ChargingPod.DEFAULT_CHARGING_SPEED);
	private IntegerProperty warehouseWidth = new SimpleIntegerProperty(10);
	private IntegerProperty warehouseHeight = new SimpleIntegerProperty(10);

	private ObjectProperty<Warehouse> warehouse = new SimpleObjectProperty<>(new Warehouse(warehouseWidth.get(), warehouseHeight.get()));
	private ListProperty<Order> orders = new SimpleListProperty<Order>(FXCollections.observableArrayList());

	public ConfigurationModel() {
		ChangeListener<Number> whListener =
			(obs, oldValue, newValue) -> {
				warehouseProperty().set(new Warehouse(warehouseWidth.get(), warehouseHeight.get()));
			};

		warehouseWidth.addListener(whListener);
		warehouseHeight.addListener(whListener);
		warehouse.addListener((obs, oldValue, newValue) -> orders.clear());
	}

	public LongProperty seedProperty() {
		return seed;
	}

	public IntegerProperty capacityProperty() {
		return capacity;
	}

	public IntegerProperty chargeSpeedProperty() {
		return chargeSpeed;
	}

	public IntegerProperty warehouseWidthProperty() {
		return warehouseWidth;
	}

	public IntegerProperty warehouseHeightProperty() {
		return warehouseHeight;
	}

	public ObjectProperty<Warehouse> warehouseProperty() {
		return warehouse;
	}

	public ListProperty<Order> ordersProperty() {
		return orders;
	}

	public ObjectProperty<ToolbarState> toolbarStateProperty() {
		return state;
	}

	public void loadSimulation(final Simulation sim) {
		final Warehouse w = sim.warehouseProperty().get();
		warehouseWidth.set(w.getWidth());
		warehouseHeight.set(w.getHeight());
		warehouse.set(w);
	
		final Iterator<ChargingPod> itPods = sim.podsProperty().iterator();
		if (itPods.hasNext()) {
			ChargingPod pod = itPods.next();
			capacityProperty().set(pod.getRobot().getMaximumCharge());
			chargeSpeedProperty().set(pod.getChargingSpeed());
		}

		orders.clear();
		orders.addAll(sim.unassignedOrdersProperty());
	}

	public Simulation createSimulation() {
		Simulation sim = new Simulation();
		sim.warehouseProperty().set(warehouseProperty().get());

		for (IdInformation info : sim.warehouseProperty().get().positionsProperty().values()) {
			info.getEntity().setSimulation(sim);
		}
		for (ChargingPod pod : sim.podsProperty()) {
			pod.setChargingSpeed(chargeSpeedProperty().get());
			pod.getRobot().setMaximumCharge(capacityProperty().get());
		}
		for (Order order : orders) {
			sim.placeOrder(order);
		}

		return sim;
	}

	public void regenerateOrders() {
		final List<StorageShelf> shelves = warehouse.get().allOfType(StorageShelf.class); 
		final Random gen = new Random(seedProperty().get());
		final int nOrders = 5 + gen.nextInt(96);

		orders.clear();
		for (int iOrder = 0; iOrder < nOrders; iOrder++) {
			final int packingTicks = 1 + gen.nextInt(20);

			final int nShelves = Math.min(shelves.size(), 1 + gen.nextInt(6));
			final Set<StorageShelf> orderShelves = new HashSet<>(nShelves);
			for (int iShelf = 0; iShelf < nShelves; iShelf++) {
				orderShelves.add(shelves.get(gen.nextInt(shelves.size())));
			}

			orders.add(new Order(packingTicks, orderShelves));
		}
	}
}