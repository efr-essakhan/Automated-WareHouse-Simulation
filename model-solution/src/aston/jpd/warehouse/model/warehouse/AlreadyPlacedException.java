package aston.jpd.warehouse.model.warehouse;

import aston.jpd.warehouse.model.entities.IEntity;

/**
 * Exception thrown when trying to place for the first time an entity that
 * already has a position.
 */
public class AlreadyPlacedException extends Exception {

	private static final long serialVersionUID = -4834085843264459821L;

	private final Warehouse warehouse;
	private final IEntity entity;
	private final Position attemptedPosition;

	public AlreadyPlacedException(Warehouse w, IEntity e, Position attempted) {
		super(String.format("Attempted to place entity %s again at %s", e.getIdentifier(), attempted));

		this.warehouse = w;
		this.entity = e;
		this.attemptedPosition = attempted;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public IEntity getEntity() {
		return entity;
	}

	public Position getAttemptedPosition() {
		return attemptedPosition;
	}

}
