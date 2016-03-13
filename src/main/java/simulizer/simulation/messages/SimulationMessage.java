package simulizer.simulation.messages;

/**
 * A message regarding the simulation state
 */
public class SimulationMessage extends Message {
	public enum Detail {
		PROGRAM_LOADED,
		SIMULATION_STARTED,
		SIMULATION_STOPPED, // only sent if stopProgram is called AND the simulation was running
		SIMULATION_INTERRUPTED
	}

	public Detail detail;

	public SimulationMessage(Detail detail) {
		this.detail = detail;
	}
}