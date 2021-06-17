package aston.jpd.warehouse.model.entities.estimation;

import java.util.List;

import aston.jpd.warehouse.model.entities.Robot;
import aston.jpd.warehouse.model.entities.Robot.State;
import aston.jpd.warehouse.model.warehouse.Position;

/**
 * Performs an estimation of the required power to follow a route, using
 * Manhattan distances with no pathfinding.
 */
public class ManhattanEstimationStrategy implements CostEstimationStrategy {

	private final double safetyMultiplier;

	public ManhattanEstimationStrategy(double m) {
		this.safetyMultiplier = m;
	}

	@Override
	public int estimate(Robot r, List<Position> waypoints, List<State> states) {
		int totalDirectCost = 0;

		for (int i = 0; i + 1 < waypoints.size(); i++) {
			final int distance = waypoints.get(i).manhattanTo(waypoints.get(i + 1));
			totalDirectCost += states.get(i).getEnergyUsage() * distance;
		}

		return (int) Math.ceil(totalDirectCost * safetyMultiplier);
	}

}
