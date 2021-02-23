package uk.ac.manchester.tornado.qsim.circuit.operation;

import uk.ac.manchester.tornado.qsim.circuit.Qubit;

/**
 * Reperesents any operation that can be applied to a quantum circuit at some step.
 * @author Ales Kubicek
 */
public interface Operation {
    /**
     * Gets all involved qubits in this operation.
     * @return involved qubits.
     */
    public Qubit[] involvedQubits();
}
