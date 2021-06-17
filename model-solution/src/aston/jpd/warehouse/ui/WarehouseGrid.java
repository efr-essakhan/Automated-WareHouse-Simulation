package aston.jpd.warehouse.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import aston.jpd.warehouse.model.entities.ChargingPod;
import aston.jpd.warehouse.model.entities.IEntity;
import aston.jpd.warehouse.model.entities.IEntityVisitor;
import aston.jpd.warehouse.model.entities.PackingStation;
import aston.jpd.warehouse.model.entities.Robot;
import aston.jpd.warehouse.model.entities.StorageShelf;
import aston.jpd.warehouse.model.warehouse.Position;
import aston.jpd.warehouse.model.warehouse.Warehouse;
import aston.jpd.warehouse.model.warehouse.Warehouse.IdInformation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 * Reusable UI component that displays the contents of a warehouse as a grid of cells,
 * and allows for registering listeners on the click of certain cells.
 */
public class WarehouseGrid extends GridPane implements ChangeListener<Warehouse>, MapChangeListener<String, IdInformation> {

	private final class PositionChangeListener implements SetChangeListener<IEntity> {
		private final Position position;

		private PositionChangeListener(Position position) {
			this.position = position;
		}

		@Override
		public void onChanged(Change<? extends IEntity> change) {
			redraw(position);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((position == null) ? 0 : position.hashCode());
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
			PositionChangeListener other = (PositionChangeListener) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (position == null) {
				if (other.position != null)
					return false;
			} else if (!position.equals(other.position))
				return false;
			return true;
		}

		private WarehouseGrid getOuterType() {
			return WarehouseGrid.this;
		}
	}

	/**
	 * Adds the appropriate entities to each cell. Uses preferred dimensions rather
	 * than current ones: during full redraw, the containers may be at zero
	 * height/width.
	 */
	private static class RedrawVisitor implements IEntityVisitor {
		private final Pane container;

		public RedrawVisitor(Pane container) {
			this.container = container;
		}

		@Override
		public void visit(ChargingPod chargingPod) {
			final Circle circle = new Circle(container.getPrefWidth() * 0.35, Color.AQUA);
			container.getChildren().add(circle);
			Tooltip.install(circle, new Tooltip(chargingPod.getIdentifier()));
		}

		@Override
		public void visit(PackingStation packingStation) {
			final Rectangle e = new Rectangle(container.getPrefWidth() * 0.7, container.getPrefHeight() * 0.7, Color.DARKGREEN);
			container.getChildren().add(e);
			Tooltip.install(e, new Tooltip(packingStation.getIdentifier()));
		}

		@Override
		public void visit(Robot robot) {
			final double w = container.getPrefWidth();
			final double h = container.getPrefHeight();

			final Polygon triangle = new Polygon();
			triangle.getPoints().addAll(
				w * 0.3, h * 0.7,
				w * 0.7, h * 0.7,
				w * 0.5, h * 0.3
			);
			triangle.setFill(Color.RED);
			
			container.getChildren().add(triangle);
			Tooltip.install(triangle, new Tooltip(robot.getIdentifier()));
		}

		@Override
		public void visit(StorageShelf shelf) {
			final Rectangle e = new Rectangle(
				container.getPrefWidth() * 0.7,
				container.getPrefHeight() * 0.7, Color.BROWN);
			container.getChildren().add(e);
			Tooltip.install(e, new Tooltip(shelf.getIdentifier()));
		}
	}

	private ObjectProperty<Warehouse> warehouse = new SimpleObjectProperty<>();
	private Set<WarehouseGridListener> listeners = new HashSet<>();
	private Map<Position, PositionChangeListener> positionListeners = new HashMap<>();

	public WarehouseGrid() {
		warehouse.addListener(this);
	}

	@Override
	public void changed(ObservableValue<? extends Warehouse> observable, Warehouse oldValue, Warehouse newValue) {
		if (oldValue != null) {
			oldValue.positionsProperty().removeListener(this);

			for (Entry<Position, PositionChangeListener> entry : positionListeners.entrySet()) {
				oldValue.entitiesForProperty(entry.getKey()).removeListener(entry.getValue());
			}
			positionListeners.clear();
		}
		if (newValue != null) {
			redraw(newValue);

			newValue.positionsProperty().addListener(this);
			for (IdInformation info : newValue.positionsProperty().values()) {
				startListeningTo(newValue, info.getPosition());
			}
		}
	}

	private void startListeningTo(Warehouse newValue, final Position position) {
		if (!positionListeners.containsKey(position)) {
			final ObservableSet<IEntity> entities = newValue.entitiesForProperty(position);
			final PositionChangeListener listener = new PositionChangeListener(position);
			entities.addListener(listener);
			positionListeners.put(position, listener);
		}
	}

	@Override
	public void onChanged(MapChangeListener.Change<? extends String, ? extends IdInformation> change) {
		IdInformation added = change.getValueAdded();
		if (added != null) {
			final Position position = added.getPosition();
			redraw(position);
			startListeningTo(warehouse.get(), position);
		}

		IdInformation removed = change.getValueRemoved();
		if (removed != null) {
			redraw(removed.getPosition());

			ObservableSet<IEntity> entities = warehouse.get().entitiesForProperty(removed.getPosition());
			PositionChangeListener oldListener = positionListeners.remove(removed.getPosition());
			entities.removeListener(oldListener);
		}
	}

	public ObjectProperty<Warehouse> warehouseProperty() {
		return warehouse;
	}

	public void addListener(WarehouseGridListener l) {
		this.listeners.add(l);
	}

	public void removeListener(WarehouseGridListener l) {
		this.listeners.remove(l);
	}

	private void redraw(Warehouse w) {
		getChildren().clear();
		for (int iRow = 0; iRow < w.getHeight(); iRow++) {
			final int currentRow = iRow;
			for (int iCol = 0; iCol < w.getWidth(); iCol++) {
				StackPane bCell = new StackPane();
				bCell.setPrefWidth(30);
				bCell.setPrefHeight(30);
				bCell.setBorder(new Border(new BorderStroke(null, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

				final int currentCol = iCol;
				bCell.setOnMouseClicked((e) -> {
					fireClick(new Position(currentCol, currentRow));
				});

				add(bCell, iCol, iRow);
				redraw(new Position(iCol, iRow));
			}
		}
	}

	private void redraw(Position added) {
		final Warehouse w = warehouse.get();
		final Pane cellPane = (Pane) this.getChildren().get(added.getRow() * w.getWidth() + added.getColumn());
		cellPane.getChildren().clear();

		final ObservableSet<IEntity> entities = w.entitiesForProperty(added);
		final IEntityVisitor visitor = new RedrawVisitor(cellPane);
		for (IEntity e : entities) {
			e.accept(visitor);
		}
	}

	private void fireClick(Position p) {
		for (WarehouseGridListener listener : listeners) {
			listener.clicked(this, p);
		}
	}
}
