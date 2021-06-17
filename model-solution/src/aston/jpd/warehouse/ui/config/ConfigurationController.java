package aston.jpd.warehouse.ui.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import aston.jpd.warehouse.io.SimulationReader;
import aston.jpd.warehouse.io.SimulationWriter;
import aston.jpd.warehouse.model.Simulation;
import aston.jpd.warehouse.model.entities.Order;
import aston.jpd.warehouse.model.warehouse.Warehouse;
import aston.jpd.warehouse.ui.Alerts;
import aston.jpd.warehouse.ui.OrderCell;
import aston.jpd.warehouse.ui.WarehouseGrid;
import aston.jpd.warehouse.ui.exec.ExecutionController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

public class ConfigurationController {

	private static final String ACTIVE_STATE_CSSCLASS = "activeState";
	private static final String INACTIVE_STATE_CSSCLASS = "inactiveState";

	public static void show(Stage primaryStage) throws IOException {
		final Pane root = (Pane)FXMLLoader.load(
				ConfigurationController.class.getResource("Configuration.fxml"));

		Scene scene = new Scene(root, 850, 500);
		scene.getStylesheets().add(ConfigurationController.class
			.getResource("configuration.css")
			.toExternalForm());

		primaryStage.setTitle("Warehouse Simulation");
		primaryStage.setMinHeight(625);
		primaryStage.setMinWidth(800);
		primaryStage.setScene(scene);

		primaryStage.show();
	}

	@FXML private TextField seedText;
	@FXML private Slider chargingSpeedSlider;
	@FXML private Slider capacitySlider;
	@FXML private Slider heightSlider;
	@FXML private Slider widthSlider;
	@FXML private WarehouseGrid warehouseGrid;
	@FXML private Button startButton, generateButton;
	@FXML private Button robotToolbarButton, shelfToolbarButton, stationToolbarButton, deleteToolbarButton;
	@FXML private ListView<Order> ordersList;

	private ConfigurationModel model = new ConfigurationModel();

	@FXML
	public void initialize() {
		// Bindings between UI and data model.
		//
		// IMPORTANT: UI property to the left, model property to the right.
		// This makes it so the UI property is set to the value of the model
		// property, not the other way around.
		seedText.textProperty().bindBidirectional(model.seedProperty(), new NumberStringConverter("0"));
		capacitySlider.valueProperty().bindBidirectional(model.capacityProperty());
		chargingSpeedSlider.valueProperty().bindBidirectional(model.chargeSpeedProperty());
		heightSlider.valueProperty().bindBidirectional(model.warehouseHeightProperty());
		widthSlider.valueProperty().bindBidirectional(model.warehouseWidthProperty());

		ordersList.setItems(model.ordersProperty());
		ordersList.setCellFactory((view) -> {
			return new OrderCell();
		});

		warehouseGrid.warehouseProperty().bindBidirectional(model.warehouseProperty());
		warehouseGrid.addListener((grid, position) -> {
			Warehouse w = model.warehouseProperty().get();
			model.toolbarStateProperty().get().placeEntities(w, position);
		});

		bindButtonClass(robotToolbarButton, ToolbarState.ROBOT);
		bindButtonClass(shelfToolbarButton, ToolbarState.SHELF);
		bindButtonClass(stationToolbarButton, ToolbarState.STATION);
		bindButtonClass(deleteToolbarButton, ToolbarState.DELETE);
		model.toolbarStateProperty().set(ToolbarState.ROBOT);
	}

	private void bindButtonClass(final Node target, final ToolbarState targetState) {
		model.toolbarStateProperty().addListener((change, oldValue, newValue) -> {
			target.getStyleClass().remove(INACTIVE_STATE_CSSCLASS);
			target.getStyleClass().remove(ACTIVE_STATE_CSSCLASS);
			if (newValue == targetState) {
				target.getStyleClass().add(ACTIVE_STATE_CSSCLASS);
			} else {
				target.getStyleClass().add(INACTIVE_STATE_CSSCLASS);
			}
		});
	}

	@FXML
	public void startPressed(ActionEvent e) {
		final Node n = (Node) e.getSource();
		final Stage window = (Stage) n.getScene().getWindow();
		
		try {
			ExecutionController.show(window, model.createSimulation());
		} catch (IOException e1) {
			Alerts.alertError("Could not change scene", "Error while changing scene", e1);
		}
	}

	@FXML
	public void generatePressed() {
		model.regenerateOrders();
	}

	@FXML public void toolbarButtonPressed(ActionEvent event) {
		ToolbarState newState;
		if (event.getSource() == robotToolbarButton) {
			newState = ToolbarState.ROBOT;
		} else if (event.getSource() == shelfToolbarButton) {
			newState = ToolbarState.SHELF;
		} else if (event.getSource() == stationToolbarButton) {
			newState = ToolbarState.STATION;
		} else {
			newState = ToolbarState.DELETE;
		}

		model.toolbarStateProperty().set(newState);
	}

	@FXML public void clearWarehousePressed() {
		model.warehouseProperty().get().clear();
	}

	@FXML public void loadWarehousePressed() {
		final FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("Simulation configuration", "*.sim"));
		final File result = chooser.showOpenDialog(generateButton.getScene().getWindow());
		if (result != null) {
			try (FileReader fReader = new FileReader(result)) {
				final Simulation sim = new SimulationReader().load(fReader);
				model.loadSimulation(sim);
			} catch (Throwable e) {
				Alerts.alertError("Error loading simulation",
					"An error occurred while loading the simulation file", e);
			}
		}
	}

	@FXML public void saveWarehousePressed() {
		final FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("Simulation configuration", "*.sim"));
		File result = chooser.showSaveDialog(generateButton.getScene().getWindow());
		if (result != null) {
			// Adds the extension if missing
			if (!result.getName().endsWith(".sim")) {
				result = new File(result.getAbsolutePath() + ".sim");
			}
			Simulation sim = model.createSimulation();

			try {
				try (FileWriter fW = new FileWriter(result)) {
					new SimulationWriter().write(fW, sim);
				}
			} catch (IOException e) {
				Alerts.alertError("Error saving simulation",
					"An error occurred while saving the simulation file", e);
			}
		}
	}

}
