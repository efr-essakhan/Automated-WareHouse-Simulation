package aston.jpd.warehouse.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import aston.jpd.warehouse.model.Simulation;
import aston.jpd.warehouse.model.entities.ChargingPod;
import aston.jpd.warehouse.model.entities.IEntity;
import aston.jpd.warehouse.model.entities.Order;
import aston.jpd.warehouse.model.entities.PackingStation;
import aston.jpd.warehouse.model.entities.Robot;
import aston.jpd.warehouse.model.entities.StorageShelf;
import aston.jpd.warehouse.model.warehouse.AlreadyPlacedException;
import aston.jpd.warehouse.model.warehouse.CollisionException;
import aston.jpd.warehouse.model.warehouse.Position;
import aston.jpd.warehouse.model.warehouse.Warehouse;

/**
 * <p>
 * Loads a simulation configuration from a file. These files are in plain text,
 * and follow a format like this, where W/H/C/S/X/Y are integers and ID_* are
 * alphanumeric identifiers, starting with a letter:
 * </p>
 *
 * <pre>
 * format 1
 * width W
 * height H
 * capacity C
 * chargeSpeed S
 * (podRobot ID_POD ID_ROBOT X Y | shelf ID_SHELF X Y | station ID_STATION X Y)*
 * (order PACKING_TIME ID_SHELF+)*
 * </pre>
 *
 * <p>
 * The first line just mentions the version of the file format (unnecessary in
 * this example, but generally in file formats it is good to support some
 * versioning). The next 4 lines configure the basic parameters: width and
 * height of the warehouse grid, and the capacity and charging speed of the
 * batteries inside the robots. The current implementation allows these lines to
 * come in any order.
 * </p>
 *
 * <p>
 * These are followed by a sequence of lines of three types:
 * </p>
 * 
 * <ul>
 * <li><code>podRobot</code> declares that a certain pair of robot + charging
 * pod is at column X and row Y.</li>
 * <li><code>shelf</code> declares that a shelf is at thoose coordinates.</li>
 * <li><code>station</code> does the same for a station.</li>
 * </ul>
 * 
 * <p>
 * Next, we have a sequence of <code>order</code> lines, which have the packing
 * time and the identifier of the shelves.
 * </p>
 *
 * <p>
 * This all-encompassing format is intended to support benchmarking of smarter
 * solutions across the various solutions submitted by the students.
 * </p>
 */
public class SimulationReader {

	public class SimulationReaderException extends Exception {
		private static final long serialVersionUID = 1L;
		private final int lineNumber;

		public SimulationReaderException(String message, Throwable cause) {
			super(message, cause);
			this.lineNumber = SimulationReader.this.lineNumber;
		}

		public SimulationReaderException(String message) {
			super(message);
			this.lineNumber = SimulationReader.this.lineNumber;
		}

		public SimulationReaderException(Throwable cause) {
			super(cause);
			this.lineNumber = SimulationReader.this.lineNumber;
		}

		public int getLineNumber() {
			return lineNumber;
		}

		@Override
		public String getMessage() {
			return "Line " + lineNumber + ": " + super.getMessage();
		}
	}

	static final int FORMAT_VERSION = 1;

	static final String CMD_FORMAT = "format";
	static final String CMD_ORDER = "order";
	static final String CMD_STATION = "station";
	static final String CMD_SHELF = "shelf";
	static final String CMD_PODROBOT = "podRobot";
	static final String CMD_CHARGESPEED = "chargeSpeed";
	static final String CMD_CAPACITY = "capacity";
	static final String CMD_HEIGHT = "height";
	static final String CMD_WIDTH = "width";

	private int width, height, capacity, chargeSpeed;
	private Simulation simulation;

	private interface CommandHandler {
		void readCommand(String[] parts) throws Throwable;
	}

	private Map<String, CommandHandler> commandHandlers = new HashMap<>();
	private int lineNumber = 1;

	public SimulationReader() {
		commandHandlers.put(CMD_WIDTH, createIntegerCommandHandler(CMD_WIDTH, (x) -> width = x));
		commandHandlers.put(CMD_HEIGHT, createIntegerCommandHandler(CMD_HEIGHT, (x) -> height = x));
		commandHandlers.put(CMD_CAPACITY, createIntegerCommandHandler(CMD_CAPACITY, (x) -> capacity = x));
		commandHandlers.put(CMD_CHARGESPEED, createIntegerCommandHandler(CMD_CHARGESPEED, (x) -> chargeSpeed = x));

		commandHandlers.put(CMD_PODROBOT, (parts) -> {
			if (parts.length != 5) {
				throw new SimulationReaderException(String.format(
						"%s expects ID_ROBOT ID_POD X Y, received '%s' instead", CMD_PODROBOT, Arrays.toString(parts)));
			}

			try {
				final String idPod = parts[1];
				final String idRobot = parts[2];
				final int column = Integer.valueOf(parts[3]);
				final int row = Integer.valueOf(parts[4]);
				final Position p = new Position(column, row);

				final Robot robot = new Robot(idRobot);
				robot.setMaximumCharge(capacity);
				robot.setSimulation(simulation);

				final ChargingPod chargingPod = new ChargingPod(idPod, robot);
				chargingPod.setChargingSpeed(chargeSpeed);
				chargingPod.setSimulation(simulation);

				final Warehouse warehouse = getWarehouse();
				warehouse.placeEntity(chargingPod, p);
				warehouse.placeEntity(robot, p);
			} catch (NumberFormatException ex) {
				throw new SimulationReaderException("Non-integer value received for X or Y", ex);
			}
		});

		commandHandlers.put(CMD_SHELF, createSingleEntityHandler(CMD_SHELF, StorageShelf::new));
		commandHandlers.put(CMD_STATION, createSingleEntityHandler(CMD_STATION, PackingStation::new));

		commandHandlers.put(CMD_ORDER, (parts) -> {
			if (parts.length < 3) {
				throw new SimulationReaderException(String.format(
						"%s expects PACKING_TIME ID_SHELF+, received '%s' instead", CMD_ORDER, Arrays.toString(parts)));
			}

			try {
				final int packingTime = Integer.valueOf(parts[1]);

				final Warehouse warehouse = getWarehouse();
				final Set<StorageShelf> shelves = new HashSet<>();
				for (int i = 2; i < parts.length; i++) {
					final String shelfID = parts[i];
					final IEntity entity = warehouse.getEntity(shelfID);
					if (entity == null) {
						throw new SimulationReaderException(
								String.format("Unknown shelf with ID '%s'", shelfID));
					} else if (entity instanceof StorageShelf) {
						StorageShelf shelf = (StorageShelf) entity;
						shelves.add(shelf);
					} else {
						throw new SimulationReaderException(
								String.format("Entity with ID '%s' is not a shelf", shelfID));
					}
				}

				Order order = new Order(packingTime, shelves);
				simulation.placeOrder(order);
			} catch (NumberFormatException ex) {
				throw new SimulationReaderException(
					String.format("Non-integer value '%s' received for PACKING_TIME", parts[1]), ex);
			}
		});
	}

	/**
	 * Retrieves the warehouse created through the file currently under load, or
	 * throws an exception if we do not have all the necessary information yet.
	 */
	private Warehouse getWarehouse() throws SimulationReaderException {
		if (width == 0) {
			throw new SimulationReaderException("Warehouse width has not been initialised yet");
		}
		if (height == 0) {
			throw new SimulationReaderException("Warehouse height has not been initialised yet");
		}

		return simulation.warehouseProperty().get();
	}

	/**
	 * Combines functional programming and generics to share the same code for the
	 * <code>shelf</code> and <code>station</code> commands.
	 */
	private <T extends IEntity> CommandHandler createSingleEntityHandler(String commandName,
			Function<String, T> entityCreator) {
		return (parts) -> {
			if (parts.length != 4) {
				throw new SimulationReaderException(String.format("%s expects ID X Y, received '%s' instead",
						commandName, Arrays.toString(parts)));
			}

			try {
				final String idShelf = parts[1];
				final int column = Integer.valueOf(parts[2]);
				final int row = Integer.valueOf(parts[3]);
				final T entity = entityCreator.apply(idShelf);
				entity.setSimulation(simulation);
				getWarehouse().placeEntity(entity, column, row);
			} catch (NumberFormatException ex) {
				throw new IOException(String.format("Line %d: non-integer value received for X or Y", lineNumber), ex);
			}
		};
	}

	private CommandHandler createIntegerCommandHandler(String commandName, Consumer<Integer> fieldUpdater) {
		return (parts) -> {
			if (parts.length != 2) {
				throw new SimulationReaderException(String.format("%s expects INTEGER, received '%s' instead",
						commandName, Arrays.toString(parts)));
			}

			try {
				final int value = Integer.valueOf(parts[1]);
				fieldUpdater.accept(value);
			} catch (NumberFormatException ex) {
				throw new SimulationReaderException(String.format("Expected a number, read '%s'", parts[1]), ex);
			}
		};
	}

	/**
	 * Reads a {@link assignedOrdersProperty} from the characters in the
	 * {@link Reader} <code>r</code>. The reader does not need to be buffered: this
	 * class will do that by itself.
	 *
	 * @throws IOException
	 *             Error while reading the file.
	 * @throws AlreadyPlacedException
	 *             The file tried to place the same entity in several positions.
	 * @throws CollisionException
	 *             The file tried to place incompatible entities in the same
	 *             position.
	 * @throws SimulationReaderException 
	 */
	public Simulation load(Reader r) throws IOException, SimulationReaderException {
		try (BufferedReader br = new BufferedReader(r)) {
			checkFormatVersion(br.readLine());
			lineNumber++;
			simulation = new Simulation();

			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = splitByWhitespace(line);

				if (parts.length > 0) {
					CommandHandler handler = commandHandlers.get(parts[0]);
					if (handler == null) {
						throw new SimulationReaderException(String.format(
							"Unknown command '%s'. Known commands are: %s.", parts[0], commandHandlers.keySet()));
					}

					try {
						handler.readCommand(parts);
					} catch (SimulationReaderException ex) {
						throw ex;
					} catch (Throwable t) {
						throw new SimulationReaderException(t);
					}
				}
				if (width > 0 && height > 0 && simulation.warehouseProperty().get() == null) {
					simulation.warehouseProperty().set(new Warehouse(width, height));
				}

				lineNumber++;
			}

			return simulation;
		}
	}

	private void checkFormatVersion(String firstLine) throws IOException, SimulationReaderException {
		if (firstLine == null) {
			throw new SimulationReaderException("Empty file, expected line with format version declaration");
		}
		final String[] firstLineParts = splitByWhitespace(firstLine);
		if (firstLineParts.length != 2 || !CMD_FORMAT.equals(firstLineParts[0])) {
			throw new SimulationReaderException("First line should be the format version declaration");
		}

		try {
			final int version = Integer.valueOf(firstLineParts[1]);
			if (version != 1) {
				throw new UnsupportedOperationException(
						String.format("Cannot read version '%d' of the warehouse file format", version));
			}
		} catch (NumberFormatException ex) {
			throw new SimulationReaderException(String.format("Expected a number for the format version, read '%s'", firstLineParts[1]));
		}
	}

	private String[] splitByWhitespace(String formatLine) {
		return formatLine.split("\\s+");
	}

}
