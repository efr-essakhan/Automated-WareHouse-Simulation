package aston.jpd.warehouse.ui.exec;

import java.io.IOException;
import java.util.Arrays;

import aston.jpd.warehouse.model.Simulation;
import aston.jpd.warehouse.model.SimulationException;
import aston.jpd.warehouse.model.entities.Order;
import aston.jpd.warehouse.model.entities.PackingStation;
import aston.jpd.warehouse.model.entities.Robot;
import aston.jpd.warehouse.ui.Alerts;
import aston.jpd.warehouse.ui.OrderCell;
import aston.jpd.warehouse.ui.WarehouseGrid;
import aston.jpd.warehouse.ui.config.ConfigurationController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller for displaying the execution of the simulation.
 */
public class ExecutionController {

	private static final int MANY_TICKS_COUNT = 10;

	private Simulation simulation;

	public static void show(Stage primaryStage, Simulation sim) throws IOException {
		final FXMLLoader loader = new FXMLLoader(ExecutionController.class.getResource("Execution.fxml"));
		final Pane root = (Pane)loader.load();
		final ExecutionController controller = loader.getController();
		controller.setSimulation(sim);

		final Scene scene = new Scene(root, 750, 400);
		scene.getStylesheets().add(ExecutionController.class
			.getResource("execution.css")
			.toExternalForm());

		primaryStage.setTitle("Warehouse Simulation");
		primaryStage.setMinHeight(300);
		primaryStage.setMinWidth(700);
		primaryStage.setScene(scene);

		primaryStage.show();
	}

	@FXML ListView<Robot> robotsList;
	@FXML ListView<PackingStation> stationsList;
	@FXML ListView<Order> unassignedList, dispatchedList, assignedList;
	@FXML Button oneTickButton, manyTicksButton, goToEndButton;
	@FXML WarehouseGrid warehouseGrid;
	@FXML Label tickLabel;

	@FXML
	public void initialize() {
		for (ListView<Order> listView : Arrays.asList(unassignedList, dispatchedList, assignedList)) {
			listView.setCellFactory((view) -> new OrderCell());
		}
	}

	@FXML
	public void oneTickPressed() {
		try {
			simulation.tick();
		} catch (SimulationException e) {
			alertFailure(e);
		}
	}

	@FXML
	public void manyTicksPressed() {
		try {
			for (int i = 0; i < MANY_TICKS_COUNT; i++) {
				simulation.tick();
			}
		} catch (SimulationException e) {
			alertFailure(e);
		}
	}

	@FXML
	public void goToEndPressed() {
		try {
			final BooleanExpression canContinue = simulation.canContinue();
			while (canContinue.get()) {
				simulation.tick();
			}
		} catch (SimulationException e) {
			alertFailure(e);
		}
	}

	private void alertFailure(SimulationException e) {
		Alerts.alertError("Simulation failure", "Simulation failed and will now stop", e);
	}

	public Simulation getSimulation() {
		return simulation;
	}

	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;

		warehouseGrid.warehouseProperty().bind(simulation.warehouseProperty());

		tickLabel.textProperty().bind(
			Bindings.concat("Tick ", simulation.currentTickProperty())
		);

		final BooleanBinding mustStop = Bindings.not(simulation.canContinue());
		oneTickButton.disableProperty().bind(mustStop);
		manyTicksButton.disableProperty().bind(mustStop);
		goToEndButton.disableProperty().bind(mustStop);

		robotsList.setItems(simulation.robotsProperty());
		stationsList.setItems(simulation.packingStationsProperty());
		unassignedList.setItems(simulation.unassignedOrdersProperty());
		assignedList.setItems(simulation.assignedOrdersProperty());
		dispatchedList.setItems(simulation.dispatchedOrdersProperty());
	}

	@FXML public void returnPressed() {
		try {
			ConfigurationController.show((Stage) oneTickButton.getScene().getWindow());
		} catch (IOException e) {
			Alerts.alertError("Could not return to configuration",
				"There was an error while returning to the configuration screen", e);
		}
	}
}
