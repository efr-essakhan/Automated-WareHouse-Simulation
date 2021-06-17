package aston.jpd.warehouse.model.entities.estimation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import aston.jpd.warehouse.model.entities.Robot;
import aston.jpd.warehouse.model.entities.Robot.State;
import aston.jpd.warehouse.model.warehouse.Position;
import aston.jpd.warehouse.model.warehouse.Warehouse;

/**
 * Another estimation function also based on dynamic programming, but this one
 * allows for "collisions", albeit at a penalty.
 */
public class DPEstimationStrategy implements CostEstimationStrategy {

	private final double multiplier;

	public DPEstimationStrategy(double safetyMultiplier) {
		this.multiplier = safetyMultiplier;
	}

	@Override
	public int estimate(Robot r, List<Position> waypoints, List<State> states) {
		int totalCost = 0;

		for (int i = 0; i + 1 < waypoints.size(); i++) {
			final Position segmentFrom = waypoints.get(i);
			final Position segmentTo = waypoints.get(i+1);

			final Map<Position, Integer> table = generateTable(r, segmentTo);
			final int bestCost = states.get(i).getEnergyUsage() * table.get(segmentFrom);
			
			totalCost += bestCost;
		}

		return (int) Math.ceil(totalCost * multiplier);
	}

	private Map<Position, Integer> generateTable(final Robot r, Position destination) {
		final Warehouse warehouse = r.getWarehouse();
		final LinkedList<Position> queue = new LinkedList<>();
		queue.add(destination);

		final Map<Position, Integer> counters = new HashMap<>();
		counters.put(destination, 0);

		while (!queue.isEmpty()) {
			final Position position = queue.removeFirst();
			final int value = counters.get(position);
			final List<Position> adjacent = position.adjacent(warehouse.getWidth(), warehouse.getHeight());

			for (Position pAdjacent : adjacent) {
				int newValue;
				if (pAdjacent.equals(r.getPosition()) || r.canMoveInto(pAdjacent)) {
					newValue = value + 1;
				} else {
					newValue = value + 5;
				}
				final int existing = counters.getOrDefault(pAdjacent, Integer.MAX_VALUE);

				if (newValue < existing) {
					counters.put(pAdjacent, newValue);
					queue.add(pAdjacent);
				}
			}
		}

		return counters;
	}

}
