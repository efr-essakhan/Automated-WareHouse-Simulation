package aston.jpd.warehouse.model.warehouse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import aston.jpd.warehouse.model.entities.IEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

/**
 * Represents the floor plan of a single warehouse as a grid of cells. Serves as
 * container for the various entities, providing bidirectional mappings between
 * the entities and their positions.
 */
public class Warehouse {

	public static class IdInformation {
		private final Position position;
		private final IEntity entity;

		private IdInformation(Position p, IEntity e) {
			this.position = p;
			this.entity = e;
		}

		public Position getPosition() {
			return position;
		}

		public IEntity getEntity() {
			return entity;
		}
	}

	// This one is not intended to be accessed directly by clients, so it stays as a regular HashMap
	private final Map<Position, ObservableSet<IEntity>> entitiesByPosition = new HashMap<>();

	// This is a map of the unmodifiable wrappers that we keep, for listening
	private final Map<Position, ObservableSet<IEntity>> unmodifiableEntitiesByPosition = new HashMap<>();

	// Internal id->info map: we can change this one
	private final ObservableMap<String, IdInformation> infoByID = FXCollections.observableHashMap();

	// External view, which is unmodifiable but can be listened to
	private final ObservableMap<String, IdInformation> unmodifiableInfoByID = FXCollections.unmodifiableObservableMap(infoByID);

	private final int width, height;

	/**
	 * Creates a new warehouse.
	 * 
	 * @param width
	 *            Number of columns of the warehouse floor grid.
	 * @param height
	 *            Number of rows of the warehouse floor grid.
	 * @throws IllegalArgumentException
	 *             width or height are negative or zero.
	 */
	public Warehouse(int width, int height) {
		if (width <= 0) {
			throw new IllegalArgumentException("width must be > 0: " + width);
		} else if (height <= 0) {
			throw new IllegalArgumentException("height must be > 0: " + height);
		}

		this.width = width;
		this.height = height;
	}

	/**
	 * Returns the number of columns of the grid.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the number of rows of the grid.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the entities at location <code>p</code>.
	 *
	 * @throws IndexOutOfBoundsException
	 *             The provided position is outside the bounds of the grid.
	 * @return Unmodifiable view of the entities at the location, if there are any,
	 *         or the empty collection if there are not.
	 */
	public ObservableSet<IEntity> entitiesForProperty(Position p) {
		checkBounds(p);

		ObservableSet<IEntity> results = unmodifiableEntitiesByPosition.get(p);
		if (results == null) {
			initialisePosition(p);
			results = unmodifiableEntitiesByPosition.get(p);
		}

		return results;
	}

	/** Convenience version of {@link #entitiesForProperty(Position)}. */
	public ObservableSet<IEntity> entitiesForProperty(int column, int row) {
		return entitiesForProperty(new Position(column, row));
	}

	/**
	 * Returns a read-only, observable version of the information about all the identifiers in the warehouse.
	 */
	public ObservableMap<String, IdInformation> positionsProperty() {
		return unmodifiableInfoByID;
	}

	/**
	 * Returns the position of <code>e</code> within the grid, or <code>null</code>
	 * if it is nowhere in the grid.
	 */
	public Position getPosition(IEntity e) {
		IdInformation info = infoByID.get(e.getIdentifier());
		return info == null ? null : info.getPosition();
	}

	/**
	 * Returns the entity with identifier <code>id</code> within the warehouse, or <code>null</code> if it is nowhere in the grid.
	 */
	public IEntity getEntity(String identifier) {
		final IdInformation info = infoByID.get(identifier);
		return info == null ? null : info.getEntity();
	}

	/**
	 * Places an entity <code>e</code> at position <code>p</code>.
	 *
	 * @param e
	 *            Entity to be placed for the first time on the board.
	 * @param p
	 *            Position where the entity should be placed.
	 * @throws AlreadyPlacedException
	 *             The entity already has a position.
	 * @throws CollisionException
	 *             The position has an incompatible entity.
	 * @throws IndexOutOfBoundsException
	 *             The position is outside the bounds of the warehouse.
	 */
	public void placeEntity(IEntity e, Position p) throws AlreadyPlacedException, CollisionException {
		checkBounds(p);
		if (infoByID.containsKey(e.getIdentifier())) {
			throw new AlreadyPlacedException(this, e, p);
		}

		addEntityTo(e, p);
	}

	/**
	 * Convenience version of {@link #placeEntity(IEntity, Position)}.
	 */
	public void placeEntity(IEntity e, int column, int row)
			throws AlreadyPlacedException, CollisionException {
		placeEntity(e, new Position(column, row));
	}

	/**
	 * Removes an entity <code>e</code> from the grid.
	 * 
	 * @param e
	 *            Entity to be removed.
	 * @throws NoSuchElementException
	 *             The entity is not part of the grid.
	 */
	public void removeEntity(IEntity e) {
		final Position p = getCurrentPosition(e);
		removeEntityFrom(e, p);
	}

	/**
	 * Moves an entity <code>e</code> already on the grid to a new location.
	 * 
	 * @param e
	 *            Entity to be moved.
	 * @param destination
	 *            Destination position.
	 * @throws NoSuchElementException
	 *             The entity is not part of the grid.
	 * @throws IndexOutOfBoundsException
	 *             The target position is outside the bounds of the grid.
	 * @throws CollisionException
	 *             The position has an incompatible entity already on it.
	 */
	public void moveEntity(IEntity e, Position destination) throws CollisionException {
		checkBounds(destination);

		final Position source = getCurrentPosition(e);
		boolean removed = entitiesByPosition.get(source).remove(e);
		assert removed : "Entity should have been removed";
		addEntityTo(e, destination);
	}

	/**
	 * Convenience version of {@link #moveEntity(IEntity, Position)}.
	 */
	public void moveEntity(IEntity e, int destinationColumn, int destinationRow) throws CollisionException {
		moveEntity(e, new Position(destinationColumn, destinationRow));
	}

	/**
	 * Removes all the entities in this warehouse.
	 */
	public void clear() {
		for (ObservableSet<IEntity> value : entitiesByPosition.values()) {
			value.clear();
		}
		infoByID.clear();
	}

	private void initialisePosition(Position p) {
		ObservableSet<IEntity> modifiable = FXCollections.observableSet(new LinkedHashSet<>());
		entitiesByPosition.put(p, modifiable);
		ObservableSet<IEntity> unmodifiable = FXCollections.unmodifiableObservableSet(modifiable);
		unmodifiableEntitiesByPosition.put(p, unmodifiable);
	}

	/**
	 * Adds an entity to a certain position. Assumes we already checked that the
	 * entity was not already somewhere else.
	 */
	private void addEntityTo(IEntity e, Position p) throws CollisionException {
		ObservableSet<IEntity> allExisting = entitiesByPosition.get(p);
		if (allExisting == null) {
			initialisePosition(p);
			allExisting = entitiesByPosition.get(p);
		}
		for (IEntity existing : allExisting) {
			if (!existing.canCoexist(e)) {
				throw new CollisionException(this, existing, e);
			}
		}
		allExisting.add(e);

		infoByID.put(e.getIdentifier(), new IdInformation(p, e));
	}

	private void removeEntityFrom(IEntity e, final Position p) {
		boolean removedFromEntities = entitiesByPosition.get(p).remove(e);
		boolean removedFromPositions = infoByID.remove(e.getIdentifier()) != null;
		assert removedFromEntities : "An element should have been removed from the entities";
		assert removedFromPositions : "An element should have been removed from the positions";
	}

	/**
	 * Gets the current position of an entity, or throws an exception it is not part
	 * of the grid.
	 */
	private Position getCurrentPosition(IEntity e) {
		final IdInformation info = infoByID.get(e.getIdentifier());
		if (info == null) {
			throw new NoSuchElementException(String.format("Entity {} is not part of the grid", e.getIdentifier()));
		}
		return info.getPosition();
	}

	/**
	 * Throws an {@link IndexOutOfBoundsException} if the position is outside the
	 * bounds of the warehouse.
	 */
	private void checkBounds(Position p) {
		if (p.getColumn() >= width) {
			throw new IndexOutOfBoundsException("Column is outside bounds: " + p.getColumn());
		} else if (p.getRow() >= height) {
			throw new IndexOutOfBoundsException("Row is outside bounds: " + p.getRow());
		}
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("Warehouse [width=");
		builder.append(width);
		builder.append(", height=");
		builder.append(height);
		builder.append(", ");
		if (entitiesByPosition != null) {
			builder.append("entitiesByPosition=");
			builder.append(toString(entitiesByPosition.entrySet(), maxLen));
		}
		builder.append("]");
		return builder.toString();
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entitiesByPosition == null) ? 0 : entitiesByPosition.hashCode());
		result = prime * result + height;
		result = prime * result + width;
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
		Warehouse other = (Warehouse) obj;
		if (entitiesByPosition == null) {
			if (other.entitiesByPosition != null)
				return false;
		} else if (!entitiesByPosition.equals(other.entitiesByPosition))
			return false;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		return true;
	}

	/**
	 * Returns a list with all the entities of a certain type.
	 */
	@SuppressWarnings("unchecked")
	public <T extends IEntity> List<T> allOfType(Class<T> class1) {
		final List<T> results = new ArrayList<>();
		for (IdInformation value : infoByID.values()) {
			IEntity entity = value.getEntity();
			if (class1.isInstance(entity)) {
				results.add((T) value.getEntity());
			}
		}
		return results;
	}

}
