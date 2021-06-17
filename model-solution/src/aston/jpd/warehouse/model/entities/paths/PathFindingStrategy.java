package aston.jpd.warehouse.model.entities.paths;

import aston.jpd.warehouse.model.entities.Robot;
import aston.jpd.warehouse.model.warehouse.Position;

/**
 * Interface for a pathfinding strategy. Just to be able to show students various ideas.
 */
public interface PathFindingStrategy {
	Position moveTowards(Robot r, Position destination);
}