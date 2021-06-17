package aston.jpd.warehouse.model.entities;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import aston.jpd.warehouse.model.SimulationException;
import aston.jpd.warehouse.model.entities.estimation.CostEstimationStrategy;
import aston.jpd.warehouse.model.entities.estimation.DPEstimationStrategy;
import aston.jpd.warehouse.model.entities.paths.DPPathFindingStrategy;
import aston.jpd.warehouse.model.entities.paths.PathFindingStrategy;
import aston.jpd.warehouse.model.warehouse.CollisionException;
import aston.jpd.warehouse.model.warehouse.Position;
import aston.jpd.warehouse.model.warehouse.Warehouse;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;

public class Robot extends Entity {

	/** Minimum charge level before leaving pod. */
	private static final double MIN_PCT_CHARGE = 0.5;

	/** Power units required to move 1 square while empty. */
	private static final int POWER_USE_EMPTY = 1;

	/** Power units required to move 1 squares while loaded. */
	private static final int POWER_USE_LOADED = 2;

	/** Default maximum charge. */
	public static final int DEFAULT_MAX_CHARGE = 50;

	public enum State {
		/** At the packing station, waiting to be released. */
		WAITING_RELEASE {
			public int getEnergyUsage() { return POWER_USE_EMPTY; }
		},
		/** Empty, parked at the charging pod. */
		PARKED {
			public int getEnergyUsage() { return POWER_USE_EMPTY; }
		},
		/** Empty, heading to the storage shelf. */
		TO_SHELF {
			public int getEnergyUsage() { return POWER_USE_EMPTY; }
		},
		/** Loaded, heading to the packing station. */
		TO_STATION {
			public int getEnergyUsage() { return POWER_USE_LOADED; }
		},
		/** Empty, heading to the charging pod. */
		TO_CHARGER {
			public int getEnergyUsage() { return POWER_USE_EMPTY; }
		};

		/** Returns the required energy to move 1 square. */
		public abstract int getEnergyUsage();
	}

	private int maxCharge = DEFAULT_MAX_CHARGE;

	private final IntegerProperty currentCharge = new SimpleIntegerProperty();
	private final ReadOnlyListWrapper<Position> waypoints =
			new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
	private final ReadOnlyObjectWrapper<State> currentState =
			new ReadOnlyObjectWrapper<>(State.PARKED);

	// Pick the desired implementations here!

	//private PathFindingStrategy pathFinder = new AvoidCollisionsStrategy();	// deadlocks
	//private PathFindingStrategy pathFinder = new BlindStrategy();				// collisions
	//private PathFindingStrategy pathFinder = new ShortestManhattanStrategy();	// simple, works fine without obstacles
	private PathFindingStrategy pathFinder = new DPPathFindingStrategy();		// works fine, works better with obstacles

	//private CostEstimationStrategy costEstimator = new ManhattanEstimationStrategy(1.1);	// usually OK, bit too optimistic
	private CostEstimationStrategy costEstimator = new DPEstimationStrategy(1.1);			// requires DP path finding, safer

	public Robot(String id) {
		super(id);
		currentCharge.set(maxCharge);
	}

	public Warehouse getWarehouse() {
		return simulation.warehouseProperty().get();
	}

	@Override
	public boolean canCoexist(IEntity other) {
		// You cannot have two robots in the same space
		return !(other instanceof Robot);
	}

	/**
	 * Returns the maximum charge level for the batteries in this robot, measured in
	 * power units.
	 */
	public int getMaximumCharge() {
		return maxCharge;
	}

	/**
	 * Changes the maximum charge level for the batteries in this robot, measured in
	 * power units.
	 */
	public void setMaximumCharge(int maxCharge) {
		this.maxCharge = maxCharge;
		this.currentCharge.set(maxCharge);
	}

	/**
	 * Returns the current charge level for the batteries in this robot, measured in
	 * power units.
	 */
	public IntegerProperty currentChargeProperty() {
		return currentCharge;
	}

	/**
	 * Returns the current waypoints set for this robot.
	 */
	public ReadOnlyListProperty<Position> waypointsProperty() {
		return waypoints.getReadOnlyProperty();
	}

	/**
	 * Returns the current state for this robot.
	 */
	public ReadOnlyObjectProperty<State> stateProperty() {
		return currentState.getReadOnlyProperty();
	}

	/**
	 * Returns <code>true</code> if this robot is able and willing to go to the
	 * storage shelf <code>s</code> and take things to the packing station
	 * <code>p</code>.
	 * 
	 * @throws SimulationException
	 *             Error while deciding whether to accept mission.
	 */
	public boolean acceptMission(PackingStation p, StorageShelf s) throws SimulationException {
		final Position shelfPosition = getPosition(s);
		final Position stationPosition = getPosition(p);

		switch (currentState.get()) {
		case PARKED:
		case TO_CHARGER:
			final int estimatedCost = estimateCost(
					Arrays.asList(getPosition(), shelfPosition, stationPosition, getPosition(getOwnChargingPod())),
					Arrays.asList(State.TO_SHELF, State.TO_STATION, State.TO_CHARGER));
			if (estimatedCost <= currentCharge.get()) {
				waypoints.clear();
				waypoints.add(shelfPosition);
				waypoints.add(stationPosition);
				currentState.set(State.TO_SHELF);
				return true;
			} else {
				return false;
			}
		default:
			return false;
		}
	}

	/**
	 * Estimates the total cost of following a route, plus a safety margin.
	 */
	private int estimateCost(List<Position> waypoints, List<State> states) {
		return Math.min(maxCharge, costEstimator.estimate(this, waypoints, states));
	}

	/**
	 * Retrieves the position of an entity in the warehouse.
	 */
	private Position getPosition(IEntity s) {
		return simulation.warehouseProperty().get().getPosition(s);
	}

	/**
	 * Notifies the robot that its mission is done, and that it can become available
	 * for other orders, or for going to charge itself.
	 * 
	 * @throws SimulationException
	 *             Could not find charging pod to return to.
	 */
	public boolean releaseMission(PackingStation packingStation, StorageShelf shelf) throws SimulationException {
		switch (currentState.get()) {
		case WAITING_RELEASE:
			waypoints.clear();
			waypoints.add(getPosition(getOwnChargingPod()));
			currentState.set(State.TO_CHARGER);
			return true;
		default:
			// Ignoring request: not waiting yet
			return false;
		}
	}

	@Override
	public void tick() throws SimulationException {
		final Position currentPosition = getPosition();

		Position destination = null;
		while (!waypoints.isEmpty() && destination == null) {
			final Position firstWaypoint = waypoints.get(0);

			// See if we have enough battery to go to the next endpoint and make it to the charging pod
			final Position podPosition = getPosition(getOwnChargingPod());
			final int estimatedCost = estimateCost(
					Arrays.asList(currentPosition, firstWaypoint, podPosition),
					Arrays.asList(currentState.get(), currentState.get()));

			if (estimatedCost > currentCharge.get() && !firstWaypoint.equals(podPosition)) {
				// We won't get there in time - retreat to charging pod for now
				destination = podPosition;
			} else if (getPosition().equals(podPosition) && currentCharge.get() < maxCharge * Robot.MIN_PCT_CHARGE) {
				// Do not leave the pod until you reach a certain level of charge
				destination = podPosition;
			} else if (currentPosition.equals(firstWaypoint)) {
				arrived(firstWaypoint);
			} else {
				destination = firstWaypoint;
			}
		}

		if (destination != null) {
			try {
				moveTowards(destination);
			} catch (CollisionException e) {
				throw new SimulationException(String.format(
					"Robot %s ran into %s", getIdentifier(), e.getPreviousEntity().getIdentifier()), e);
			}
		}
	}

	private void arrived(Position firstWaypoint) throws SimulationException {
		final State state = currentState.get();
		IEntity firstEntity = getFirstEntityAt(firstWaypoint);

		switch (state) {
		case TO_SHELF:
			if (firstEntity instanceof StorageShelf) {
				waypoints.remove(0);
				currentState.set(State.TO_STATION);
			}
			break;
		case TO_STATION:
			if (firstEntity instanceof PackingStation) {
				waypoints.remove(0);
				currentState.set(State.WAITING_RELEASE);
			}
			break;
		case TO_CHARGER:
			if (firstEntity instanceof ChargingPod) {
				// This was an intentional parking cycle: stay here
				waypoints.remove(0);
				currentState.set(State.PARKED);
			}
			break;
		default:
			throw new SimulationException(
					String.format("Unexpected arrival of robot %s at %s in state %s", getIdentifier(), firstWaypoint));
		}
	}

	private IEntity getFirstEntityAt(Position firstWaypoint) {
		Set<IEntity> entities = simulation.warehouseProperty().get().entitiesForProperty(firstWaypoint);
		IEntity firstEntity = entities.isEmpty() ? null : entities.iterator().next();
		return firstEntity;
	}

	private void moveTowards(final Position destination) throws CollisionException, SimulationException {
		final Position current = getPosition();
		if (!current.equals(destination)) {
			final int requiredEnergy = currentState.get().getEnergyUsage();
			if (currentCharge.get() < requiredEnergy) {
				throw new SimulationException(String.format("Robot %s cannot move: out of battery", getIdentifier()));
			}

			final Position newPosition = pathFinder.moveTowards(this, destination);
			if (newPosition != null && !newPosition.equals(current)) {
				currentCharge.set(currentCharge.get() - requiredEnergy);
				simulation.warehouseProperty().get().moveEntity(this, newPosition);
			}
		}
	}

	public boolean canMoveInto(final Position position) {
		return simulation.warehouseProperty().get()
			.entitiesForProperty(position)
			.stream().allMatch(e -> e.canCoexist(this));
	}

	@Override
	public void accept(IEntityVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + maxCharge;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Robot other = (Robot) obj;
		if (maxCharge != other.maxCharge)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
			"%s (%d/%d): %s, %s",
			getIdentifier(),
			currentCharge.get(),
			maxCharge,
			currentState.get(),
			waypoints.get()
		);
	}

	private ChargingPod getOwnChargingPod() throws SimulationException {
		for (ChargingPod pod : simulation.podsProperty()) {
			if (pod.getRobot() == this) {
				return pod;
			}
		}
		throw new SimulationException(
			String.format("Could not find charging pod for robot %s", getIdentifier()));
	}
}
