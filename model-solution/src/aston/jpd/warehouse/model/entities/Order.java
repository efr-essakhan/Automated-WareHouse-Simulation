package aston.jpd.warehouse.model.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Java bean that represents a single order in the system, as the collection of
 * the shelves that need to be visited. The shelves cannot be changed once this
 * object is created. Orders have a packing time as well, which is the number of
 * ticks that must pass after the last item arrives before it can be dispatched.
 */
public class Order {

	private final Set<StorageShelf> shelves;
	private final int packingTicks;

	public Order(int packingTicks, StorageShelf... shelves) {
		this(packingTicks, new HashSet<>(Arrays.asList(shelves)));
	}

	public Order(int packingTicks, Set<StorageShelf> shelves) {
		this.shelves = Collections.unmodifiableSet(new HashSet<>(shelves));
		this.packingTicks = packingTicks;
	}

	public Set<StorageShelf> getShelves() {
		return shelves;
	}

	public int getPackingTicks() {
		return packingTicks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + packingTicks;
		result = prime * result + ((shelves == null) ? 0 : shelves.hashCode());
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
		Order other = (Order) obj;
		if (packingTicks != other.packingTicks)
			return false;
		if (shelves == null) {
			if (other.shelves != null)
				return false;
		} else if (!shelves.equals(other.shelves))
			return false;
		return true;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("Order [");
		if (shelves != null) {
			builder.append("shelves=");
			builder.append(toString(shelves, maxLen));
			builder.append(", ");
		}
		builder.append("packingTicks=");
		builder.append(packingTicks);
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
}
