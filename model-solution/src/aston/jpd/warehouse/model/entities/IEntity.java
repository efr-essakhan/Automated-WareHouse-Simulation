package aston.jpd.warehouse.model.entities;

import aston.jpd.warehouse.model.Simulation;
import aston.jpd.warehouse.model.SimulationException;

/**
 * Base interface for an entity that can be placed on a cell of the shop floor.
 */
public interface IEntity {

	/**
	 * Returns a unique identifier for this entity. Useful to ensure that a single
	 * entity cannot by mistake be at two places on the board at the same time.
	 */
	String getIdentifier();

	/**
	 * Returns <code>true</code> if this entity can be placed at the same position
	 * as the <code>other</code> entity, or <code>false</code> otherwise.
	 */
	boolean canCoexist(IEntity other);

	/**
	 * Advances the simulation of this entity for one tick.
	 *
	 * @throws SimulationException
	 *             The simulation has suffered a critical failure and must be
	 *             stopped: this may be a power failure, a crash for the robots, or
	 *             some other internal problem.
	 */
	void tick() throws SimulationException;

	/**
	 * Allows a visitor to enter this entity.
	 */
	void accept(IEntityVisitor visitor);

	/**
	 * Tells the entity about the simulation it is currently part of.
	 */
	void setSimulation(Simulation sim);
}
