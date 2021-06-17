package aston.jpd.warehouse.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import aston.jpd.warehouse.model.SimulationException;
import aston.jpd.warehouse.model.warehouse.AlreadyPlacedException;
import aston.jpd.warehouse.model.warehouse.CollisionException;
import aston.jpd.warehouse.model.warehouse.Position;
import aston.jpd.warehouse.model.warehouse.Warehouse;
import aston.jpd.warehouse.model.warehouse.Warehouse.IdInformation;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class WarehouseTest {

	static class DummyEntity extends Entity {
		private final Function<IEntity, Boolean> canCoexist;

		public DummyEntity(String id, Function<IEntity, Boolean> canCoexist) {
			super(id);
			this.canCoexist = canCoexist;
		}

		public DummyEntity(String id, boolean canCoexist) {
			this(id, (e) -> canCoexist);
		}

		public DummyEntity(String id) {
			this(id, true);
		}

		@Override
		public boolean canCoexist(IEntity other) {
			return canCoexist.apply(other);
		}

		@Override
		public void tick() throws SimulationException {
			// nothing to do
		}

		@Override
		public void accept(IEntityVisitor visitor) {
			// unused
		}
	}

	@Nested
	@DisplayName("Constructor tests")
	class ConstructorTests {
		@Test
		public void testNegativeWidth() {
			assertThrows(IllegalArgumentException.class, () -> new Warehouse(-1, 3));
		}

		@Test
		public void testNegativeHeight() {
			assertThrows(IllegalArgumentException.class, () -> new Warehouse(3, -1));
		}

		@Test
		public void testZeroWidth() {
			assertThrows(IllegalArgumentException.class, () -> new Warehouse(0, 3));
		}

		@Test
		public void testZeroHeight() {
			assertThrows(IllegalArgumentException.class, () -> new Warehouse(3, 0));
		}

		@Test
		public void testGoodSize() {
			Warehouse w = new Warehouse(3, 4);
			assertEquals(3, w.getWidth());
			assertEquals(4, w.getHeight());
		}
	}

	@Nested
	@DisplayName("Entity placement")
	class EntityPlacement {
		private Warehouse w;
		private DummyEntity eX1;

		@BeforeEach
		public void prepareWarehouse() {
			w = new Warehouse(5, 5);
			eX1 = new DummyEntity("x1");
		}

		/**
		 * Best case scenario, where we place things normally and move it around.
		 */
		@Test
		public void testOne() throws Exception {
			final Position p1 = new Position(2, 3);
			assertTrue(w.entitiesForProperty(p1).isEmpty());

			w.placeEntity(eX1, p1);
			assertEquals(Collections.singleton(eX1), w.entitiesForProperty(p1));
			assertEquals(p1, w.getPosition(eX1));

			final Position p2 = new Position(4, 3);
			w.moveEntity(eX1, p2);
			assertTrue(w.entitiesForProperty(p1).isEmpty());
			assertEquals(Collections.singleton(eX1), w.entitiesForProperty(p2));
			assertEquals(p2, w.getPosition(eX1));

			w.removeEntity(eX1);
			assertTrue(w.entitiesForProperty(p2).isEmpty());
			assertNull(w.getPosition(eX1));
		}

		@Test
		public void testPlaceAgain() throws Exception {
			w.placeEntity(eX1, 1, 1);
			try {
				w.placeEntity(eX1, 2, 2);
				fail("Should have reported it was already placed");
			} catch (AlreadyPlacedException ex) {
				assertSame(w, ex.getWarehouse());
				assertSame(eX1, ex.getEntity());
				assertEquals(new Position(2, 2), ex.getAttemptedPosition());
			}
		}

		@Test
		public void testPlaceOutOfBoundsW() {
			assertThrows(IndexOutOfBoundsException.class,
					() -> w.placeEntity(eX1, w.getWidth(), 1));
		}
		
		@Test
		public void testPlaceOutOfBoundsH() {
			assertThrows(IndexOutOfBoundsException.class,
					() -> w.placeEntity(eX1, 1, w.getHeight()));
		}

		@Test
		public void testPlaceMultipleCanCoexist() throws Exception {
			DummyEntity eX2 = new DummyEntity("x2");
			Position p = new Position(4, 4);
			w.placeEntity(eX1, p);
			w.placeEntity(eX2, p);

			assertEquals(new HashSet<IEntity>(Arrays.asList(eX1, eX2)), w.entitiesForProperty(p));
			assertEquals(p, w.getPosition(eX1));
			assertEquals(p, w.getPosition(eX2));

			w.moveEntity(eX2, 4, 3);
			assertEquals(Collections.singleton(eX1), w.entitiesForProperty(p));
			assertEquals(Collections.singleton(eX2), w.entitiesForProperty(4, 3));
		}

		@Test
		public void testPlaceMultipleCannotCoexist() throws Exception {
			DummyEntity eX2 = new DummyEntity("x2", false);
			Position p = new Position(3, 4);
			w.placeEntity(eX2, p);
			try {
				w.placeEntity(eX1, p);
				fail("Expected a CollisionException");
			} catch (CollisionException ex) {
				assertSame(w, ex.getWarehouse());
				assertSame(eX2, ex.getPreviousEntity());
				assertSame(eX1, ex.getNewEntity());
			}
		}

		@Test
		public void testRemoveNotInGrid() throws Exception {
			assertThrows(NoSuchElementException.class, () -> w.removeEntity(eX1));
		}
	}

	@Nested
	@DisplayName("Notifications")
	class Notifications {
		private Warehouse w;
		private MapChangeListener<String, IdInformation> positionListener;
		private SetChangeListener<IEntity> setListener;

		@SuppressWarnings("unchecked")
		@BeforeEach
		public void prepare() {
			w = new Warehouse(4, 4);
			positionListener = (MapChangeListener<String, IdInformation>) mock(MapChangeListener.class);
			setListener = (SetChangeListener<IEntity>) mock(SetChangeListener.class);
		}

		@Test
		public void notifications() throws Exception {
			ObservableMap<String, IdInformation> allPositions = w.positionsProperty();
			allPositions.addListener(positionListener);

			ObservableSet<IEntity> entitiesX1Y1 = w.entitiesForProperty(1, 1);
			ObservableSet<IEntity> entitiesX2Y3 = w.entitiesForProperty(2, 3);
			entitiesX1Y1.addListener(setListener);
			entitiesX2Y3.addListener(setListener);

			// We make sure that we always get the notification for "entities in cell"
			// first, then the one about the positions having changed, for consistency.

			final DummyEntity eX1 = new DummyEntity("x1");
			final DummyEntity eX2 = new DummyEntity("x2");
			final InOrder orderVerifier = inOrder(positionListener, setListener);

			w.placeEntity(eX1, 1, 1);
			orderVerifier.verify(setListener).onChanged(any());
			orderVerifier.verify(positionListener).onChanged(any());

			w.placeEntity(eX2, 1, 1);
			orderVerifier.verify(setListener).onChanged(any());
			orderVerifier.verify(positionListener).onChanged(any());

			w.moveEntity(eX1, 2, 3);
			orderVerifier.verify(setListener, times(2)).onChanged(any());
			orderVerifier.verify(positionListener).onChanged(any());

			w.removeEntity(eX2);
			orderVerifier.verify(setListener).onChanged(any());
			orderVerifier.verify(positionListener).onChanged(any());
		}
	}
}
