package aston.jpd.warehouse.model.entities.paths;

import aston.jpd.warehouse.model.entities.Robot;
import aston.jpd.warehouse.model.warehouse.Position;

/**
 * This strategy simply tries to go towards the target, but it only goes in the direction without obstacles.
 * It can result in deadlocks between two robots trying to go in opposite directions.
 */
public class AvoidCollisionsStrategy implements PathFindingStrategy {
	@Override
	public Position moveTowards(Robot r, Position destination) {
		final Position current = r.getPosition();
		final int deltaX = destination.getColumn() - current.getColumn();
		final int deltaY = destination.getRow() - current.getRow();

		final int incrementX = (int) Math.signum(deltaX);
		final int incrementY = (int) Math.signum(deltaY);
		final Position positionHorizontal = current.moveHorizontally(incrementX);
		final Position positionVertical = current.moveVertically(incrementY);

		final boolean canMoveHorizontal = r.canMoveInto(positionHorizontal);
		final boolean canMoveVertical = r.canMoveInto(positionVertical);

		if (deltaY != 0 && canMoveVertical) {
			return positionVertical;
		} else if (deltaX != 0 && canMoveHorizontal) {
			return positionHorizontal;
		}

		return null;
	}
}