package aston.jpd.warehouse.model.warehouse;

import aston.jpd.warehouse.model.entities.IEntity;

/**
 * Exception thrown when attempting to place an {@link IEntity} at a location
 * with another incompatible (with regards to
 * {@link IEntity#canCoexist(IEntity)}) entity.
 */
public class CollisionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Warehouse warehouse;
	private final IEntity previousEntity;
	private final IEntity newEntity;

	public CollisionException(Warehouse w, IEntity previousEntity, IEntity newEntity) {
		super(String.format("Tried to place entity %s where incompatible entity %s was",
				newEntity.getIdentifier(), previousEntity.getIdentifier()));
		this.warehouse = w;
		this.previousEntity = previousEntity;
		this.newEntity = newEntity;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public IEntity getPreviousEntity() {
		return previousEntity;
	}

	public IEntity getNewEntity() {
		return newEntity;
	}

}
