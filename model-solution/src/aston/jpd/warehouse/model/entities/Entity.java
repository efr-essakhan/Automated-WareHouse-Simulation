package aston.jpd.warehouse.model.entities;

import java.util.Set;

import aston.jpd.warehouse.model.Simulation;
import aston.jpd.warehouse.model.warehouse.Position;

/**
 * Base abstract superclass for all entities.
 */
public abstract class Entity implements IEntity {

	protected Simulation simulation;
	private final String id;

	public Entity(String id) {
		this.id = id;
	}

	/**
	 * Returns the simulation that this entity is part of.
	 */
	public Simulation getSimulation() {
		return simulation;
	}

	@Override
	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}

	@Override
	public String getIdentifier() {
		return id;
	}

	/**
	 * Returns the position of this entity in the warehouse.
	 */
	public Position getPosition() {
		return simulation.warehouseProperty().get().getPosition(this);
	}

	/**
	 * Returns the entities in the same location as this one (including itself).
	 */
	public Set<IEntity> getEntitiesInSamePosition() {
		return simulation.warehouseProperty().get().entitiesForProperty(getPosition());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}