package aston.jpd.warehouse.ui;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;

/**
 * Utility methods for showing alert messages to the user.
 */
public final class Alerts {

	private Alerts() {
		// not used
	}

	/**
	 * Shows an error alert, with a custom title and header text and the full
	 * exception message and stack trace.
	 */
	public static void alertError(String title, String header, Throwable t) {
		final Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);

		StringWriter sW = new StringWriter();
		PrintWriter pW = new PrintWriter(sW);
		pW.println(t.getMessage());
		pW.println();
		//t.printStackTrace(pW);

		TextArea errorText = new TextArea(sW.toString());
		alert.getDialogPane().setContent(errorText);
		alert.showAndWait();
	}
}
