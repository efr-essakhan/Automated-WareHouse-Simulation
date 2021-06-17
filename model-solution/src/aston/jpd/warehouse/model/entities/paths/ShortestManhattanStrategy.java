package aston.jpd.warehouse.model.entities.paths;

import java.util.List;
import java.util.Optional;

import aston.jpd.warehouse.model.entities.Robot;
import aston.jpd.warehouse.model.warehouse.Position;
import aston.jpd.warehouse.model.warehouse.Warehouse;

/**
 * This strategy picks the shortest directions towards the target that is not
 * taken. This seems to work fine if we allow robots to go through other
 * charging pods and packing stations, but it is inefficient - requires a large
 * safety margin.
 */
public class ShortestManhattanStrategy implements PathFindingStrategy {
	@Override
	public Position moveTowards(Robot r, Position destination) {
		final Warehouse warehouse = r.getWarehouse();
		final List<Position> candidates = r.getPosition().adjacent(warehouse.getWidth(), warehouse.getHeight());
		Optional<Position> bestOption = candidates.stream().filter(e -> r.canMoveInto(e)).sorted((a, b) -> {
			final int fromA = a.manhattanTo(destination);
			final int fromB = b.manhattanTo(destination);
			return Integer.compare(fromA, fromB);
		}).findFirst();

		if (bestOption.isPresent()) {
			return bestOption.get();
		} else {
			return null;
		}
	}
}