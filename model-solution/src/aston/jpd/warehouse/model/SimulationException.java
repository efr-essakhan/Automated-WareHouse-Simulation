package aston.jpd.warehouse.model;

/**
 * Exception that represents a critical failure in the simulation, which requires it to be stopped.
 */
public class SimulationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SimulationException(String message, Throwable cause) {
		super(message, cause);
	}

	public SimulationException(String message) {
		super(message);
	}

	public SimulationException(Throwable cause) {
		super(cause);
	}
	
}
