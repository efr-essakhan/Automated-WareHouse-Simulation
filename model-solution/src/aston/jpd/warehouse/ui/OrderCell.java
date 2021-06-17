package aston.jpd.warehouse.ui;

import aston.jpd.warehouse.model.entities.Order;
import aston.jpd.warehouse.model.entities.StorageShelf;
import javafx.scene.control.ListCell;

/**
 * Custom cell for displaying orders.
 */
public class OrderCell extends ListCell<Order> {

	@Override
	protected void updateItem(Order item, boolean empty) {
		super.updateItem(item, empty);

		if (item != null) {
			final StringBuilder sb = new StringBuilder();

			boolean first = true;
			sb.append("[");
			for (StorageShelf shelf : item.getShelves()) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(shelf.getIdentifier());
			}
			sb.append("] (");
			sb.append(item.getPackingTicks());
			sb.append(")");

			setText(sb.toString());
		}
	}

}
