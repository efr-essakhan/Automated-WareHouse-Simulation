package aston.jpd.warehouse.model.entities.paths;

import aston.jpd.warehouse.model.entities.Robot;
import aston.jpd.warehouse.model.warehouse.Position;

/**
 * This strategy simply tries to go towards the target, without checking if there are any obstacles.
 * This results in deadlocks and collisions.
 */
public class BlindStrategy implements PathFindingStrategy {
	@Override
	public Position moveTowards(Robot r, Position destination) {
		final Position current = r.getPosition();
		final int deltaX = destination.getColumn() - current.getColumn();
		final int deltaY = destination.getRow() - current.getRow();

		final int incrementX = (int) Math.signum(deltaX);
		final int incrementY = (int) Math.signum(deltaY);
		final Position positionHorizontal = current.moveHorizontally(incrementX);
		final Position positionVertical = current.moveVertically(incrementY);

		if (deltaY != 0) {
			return positionVertical;
		} else if (deltaX != 0) {
			return positionHorizontal;
		}

		return null;
	}
}