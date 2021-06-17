package aston.jpd.warehouse.model.entities.paths;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import aston.jpd.warehouse.model.entities.Robot;
import aston.jpd.warehouse.model.warehouse.Position;
import aston.jpd.warehouse.model.warehouse.Warehouse;

/**
 * Implements the "sample algorithm" from Wikipedia, which is really just a
 * dynamic programming algorithm computing minimal distance in tiles from the
 * destination.
 *
 * This is the most reliable one, and can handle obstacles well. Implementing
 * this algorithm might be a bit hard for a few students - this may benefit from
 * some design notes.
 */
public class DPPathFindingStrategy implements PathFindingStrategy {

	@Override
	public Position moveTowards(Robot r, Position destination) {
		final Warehouse warehouse = r.getWarehouse();
		final Map<Position, Integer> counters = generateTable(r, destination);

		// Stage 2: decide on next tile (reachable and adjacent with minimum distance)
		List<Position> adjacentCurrent = r.getPosition().adjacent(warehouse.getWidth(), warehouse.getHeight());
		Optional<Position> bestOption = adjacentCurrent.stream().filter(p -> r.canMoveInto(p)).sorted((a, b) -> {
			final int valueA = counters.getOrDefault(a, Integer.MAX_VALUE);
			final int valueB = counters.getOrDefault(b, Integer.MAX_VALUE);
			return Integer.compare(valueA, valueB);
		}).findFirst();

		// Stage 3: return best option, if any
		if (bestOption.isPresent()) {
			return bestOption.get();
		} else {
			return null;
		}
	}

	private Map<Position, Integer> generateTable(final Robot r, Position destination) {
		final Warehouse warehouse = r.getWarehouse();
		final LinkedList<Position> queue = new LinkedList<>();
		queue.add(destination);

		final Map<Position, Integer> counters = new HashMap<>();
		counters.put(destination, 0);

		// Stage 1: compute table
		while (!queue.isEmpty()) {
			final Position position = queue.removeFirst();
			final int value = counters.get(position);
			final List<Position> adjacent = position.adjacent(warehouse.getWidth(), warehouse.getHeight());

			for (Position pAdjacent : adjacent) {
				if (r.canMoveInto(pAdjacent)) {
					final int newValue = value + 1;
					final int existing = counters.getOrDefault(pAdjacent, Integer.MAX_VALUE);

					if (newValue < existing) {
						counters.put(pAdjacent, newValue);
						queue.add(pAdjacent);
					}
				}
			}
		}
		return counters;
	}

}
