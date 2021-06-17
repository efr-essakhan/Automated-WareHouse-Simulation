package aston.jpd.warehouse.ui;

import aston.jpd.warehouse.model.warehouse.Position;

/**
 * Interface for listeners of warehouse grids.
 */
public interface WarehouseGridListener {

	/**
	 * The user clicked on a cell at a certain position <code>p</code> of the
	 * <code>grid</code>.
	 */
	void clicked(WarehouseGrid grid, Position p);

}
