package uk.ac.manchester.tornado.qsim.circuit.operation;

import uk.ac.manchester.tornado.qsim.circuit.operation.enums.OperationType;

/**
 * Reperesents any operation that can be applied to a quantum circuit at some step.
 * @author Ales Kubicek
 */
public interface Operation {
    /**
     * Gets all involved qubits in this operation.
     * @return involved qubits.
     */
    public int[] involvedQubits();

    /**
     * Gets the size (number of qubits), that the operation occupies in a circuit step.
     * @return operation size.
     */
    public int size();

    /**
     * Gets the operation type.
     * @return operation type.
     */
    public OperationType operationType();
}
