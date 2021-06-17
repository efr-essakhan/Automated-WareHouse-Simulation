package aston.jpd.warehouse.model.warehouse;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single immutable position within the warehouse floor.
 */
public class Position {

	/** Column index, with 0 as the leftmost (westernmost) position. */
	private final int column;

	/** Row index, with 0 as the topmost (northernmost) position. */
	private final int row;

	/**
	 * Creates a new position.
	 * 
	 * @param column
	 *            Column index (0 is leftmost column).
	 * @param row
	 *            Row index (0 is topmost column).
	 * @throws IllegalArgumentException
	 *             x or y are negative.
	 */
	public Position(int column, int row) {
		if (column < 0) {
			throw new IllegalArgumentException("Column should not be negative: " + column);
		} else if (row < 0) {
			throw new IllegalArgumentException("Row should not be negative: " + row);
		}

		this.column = column;
		this.row = row;
	}

	/**
	 * Returns the column index of the position, with 0 as the leftmost one.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Returns the row index of the position, with 0 as the topmost one.
	 */
	public int getRow() {
		return row;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 17;
		result = prime * result + column;
		result = prime * result + row;
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
		Position other = (Position) obj;
		if (column != other.column)
			return false;
		if (row != other.row)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("(%d, %d)", column, row);
	}

	/**
	 * Returns the Manhattan distance to the target position, in squares.
	 */
	public int manhattanTo(final Position b) {
		return Math.abs(getColumn() - b.getColumn()) + Math.abs(getRow() - b.getRow());
	}

	/**
	 * Returns a position that is a certain number of columns/rows from here.
	 */
	public Position move(int colShift, int rowShift) {
		return new Position(column + colShift, row + rowShift);
	}

	/**
	 * Returns a position that is a certain number of rows from here.
	 */
	public Position moveVertically(int rowShift) {
		return move(0, rowShift);
	}

	/**
	 * Returns a position that is a certain number of columns from here.
	 */
	public Position moveHorizontally(int colShift) {
		return move(colShift, 0);
	}

	/**
	 * Returns a list of all valid adjacent positions, given a certain width and height.
	 */
	public List<Position> adjacent(int width, int height) {
		final List<Position> results = new ArrayList<>();
		if (column > 0) {
			results.add(moveHorizontally(-1));
		}
		if (column + 1 < width) {
			results.add(moveHorizontally(1));
		}
		if (row > 0) {
			results.add(moveVertically(-1));
		}
		if (row + 1 < height) {
			results.add(moveVertically(1));
		}
		return results;
	}
}
