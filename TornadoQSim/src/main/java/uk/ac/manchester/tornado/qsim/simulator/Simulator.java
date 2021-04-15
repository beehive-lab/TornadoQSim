package uk.ac.manchester.tornado.qsim.simulator;

import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.circuit.State;

/**
 * Represents a general quantum simulator.
 * @author Ales Kubicek
 */
public interface Simulator {
    /**
     * Simulates the supplied circuit and returns the full state representation of the quantum system.
     * @param circuit quantum circuit to be simulated.
     * @return full state of the simulated circuit.
     */
    public State simulateFullState(Circuit circuit);

    /**
     * Simulates the supplied circuit and returns the collapsed state of the quantum system.
     * @param circuit quantum circuit to be simulated.
     * @return collapsed state of the simulated circuit (bitstring).
     */
    public int simulateAndCollapse(Circuit circuit);
}
