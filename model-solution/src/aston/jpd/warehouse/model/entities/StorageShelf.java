package aston.jpd.warehouse.model.entities;

import aston.jpd.warehouse.model.SimulationException;

/**
 * Storage shelf entity. Entirely passive: it is pretty much just a goal marker
 * for the robots.
 */
public class StorageShelf extends Entity {

	public StorageShelf(String id) {
		super(id);
	}

	@Override
	public boolean canCoexist(IEntity other) {
		// It can have another robot in the same space, and that's it
		return other instanceof Robot;
	}

	@Override
	public void tick() throws SimulationException {
		// nothing to do
	}

	@Override
	public void accept(IEntityVisitor visitor) {
		visitor.visit(this);
	}

}
