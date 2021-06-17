package aston.jpd.warehouse.model.entities.estimation;

import java.util.List;

import aston.jpd.warehouse.model.entities.Robot;
import aston.jpd.warehouse.model.warehouse.Position;

/**
 * Estimates the cost of following a certain route.
 */
public interface CostEstimationStrategy {

	/**
	 * Returns the estimate of the cost of following a certain path, in power units.
	 */
	int estimate(Robot r, List<Position> waypoints, List<Robot.State> states);

}
