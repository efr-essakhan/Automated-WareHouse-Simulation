package aston.jpd.warehouse.ui.config;

import java.util.HashSet;
import java.util.Set;

import aston.jpd.warehouse.model.entities.ChargingPod;
import aston.jpd.warehouse.model.entities.IEntity;
import aston.jpd.warehouse.model.entities.PackingStation;
import aston.jpd.warehouse.model.entities.Robot;
import aston.jpd.warehouse.model.entities.StorageShelf;
import aston.jpd.warehouse.model.warehouse.Position;
import aston.jpd.warehouse.model.warehouse.Warehouse;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * States for the entity creation toolbar.
 */
enum ToolbarState {
	ROBOT {
		@Override
		public void placeEntities(Warehouse w, Position position) {
			if (w.entitiesForProperty(position).isEmpty()) {
				final Robot r = new Robot(next(w, "r"));
				final ChargingPod c = new ChargingPod(next(w, "c"), r);

				// We want the robot to be on top of the charging pod.
				try {
					w.placeEntity(c, position);
					w.placeEntity(r, position);
				} catch (Exception e) {
					alert(e);
				}
			}
		}
	}, STATION {
		@Override
		public void placeEntities(Warehouse w, Position p) {
			if (w.entitiesForProperty(p).isEmpty()) {
				final PackingStation ps = new PackingStation(next(w, "ps"));
				try {
					w.placeEntity(ps, p);
				} catch (Exception e) {
					alert(e);
				}
			}
		}
	}, SHELF {
		@Override
		public void placeEntities(Warehouse w, Position p) {
			if (w.entitiesForProperty(p).isEmpty()) {
				final StorageShelf ss = new StorageShelf(next(w, "ss"));
				try {
					w.placeEntity(ss, p);
				} catch (Exception e) {
					alert(e);
				}
			}
		}
	}, DELETE {
		@Override
		public void placeEntities(Warehouse w, Position p) {
			// We do a defensive copy so we won't get a ConcurrentModificationException
			Set<IEntity> entities = new HashSet<>(w.entitiesForProperty(p));

			for (IEntity e : entities) {
				w.removeEntity(e);
			}
		}
	};

	public abstract void placeEntities(Warehouse w, Position p);

	protected String next(Warehouse w, String prefix) {
		int i = 0;
		while (w.getEntity(prefix + i) != null) {
			i++;
		}
		return prefix + i;
	}

	protected void alert(Exception e) {
		e.printStackTrace();

		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Exception while placing entity");
		alert.setHeaderText("Could not place entity");
		alert.setContentText(e.getMessage());
		alert.showAndWait();
	}
	
}