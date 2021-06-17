package aston.jpd.warehouse.ui;

import java.io.File;
import java.io.FileReader;

import aston.jpd.warehouse.io.SimulationReader;
import aston.jpd.warehouse.model.Simulation;
import aston.jpd.warehouse.ui.config.ConfigurationController;
import aston.jpd.warehouse.ui.exec.ExecutionController;
import javafx.application.Application;
import javafx.stage.Stage;

public class GUILauncher extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		String configFile = getParameters().getNamed().get("config");
		if (configFile == null) {
			ConfigurationController.show(primaryStage);
		} else {
			try (FileReader fR = new FileReader(new File(configFile))) {
				 Simulation sim = new SimulationReader().load(fR);
				 ExecutionController.show(primaryStage, sim);
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
