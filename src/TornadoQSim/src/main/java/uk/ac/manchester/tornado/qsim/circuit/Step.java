package uk.ac.manchester.tornado.qsim.circuit;

import uk.ac.manchester.tornado.qsim.circuit.operation.Operation;

import java.util.HashMap;
import java.util.Objects;

/**
 * Represents a single step in a quantum circuit. It ensures that only such quantum operations that can fit into
 * the step are allowed to be added.
 * @author Ales Kubicek
 */
public class Step {
    private final HashMap<Integer, Operation> qubitOperations;

    /**
     * Constructs a quantum circuit step with the supplied number of qubits.
     * @param noQubits number of qubits in the quantum circuit step.
     */
    public Step(int noQubits) {
        if (noQubits < 1)
            throw new IllegalArgumentException("Number of qubits in a step must be greater than 0.");
        this.qubitOperations = new HashMap<Integer, Operation>();
        for (int qubit = 0; qubit < noQubits; qubit++)
            this.qubitOperations.put(qubit, null);
    }

    /**
     * Gets an operation that occupies the queried qubit. NULL is returned if no operation occupies the queried qubit.
     * @param qubit queried qubit.
     * @return quantum operation (can be NULL)
     */
    public Operation getOperation(int qubit) {
        if (!isValidQubit(qubit))
            throw new IllegalArgumentException("Invalid qubit supplied.");
        return this.qubitOperations.get(qubit);
    }

    /**
     * Checks whether the queried qubit is free (no operation occupies this qubit).
     * @param qubit queried qubit.
     * @return true, if no operation occupies this qubit.
     */
    public boolean isQubitFree(int qubit) {
        if (!isValidQubit(qubit))
            throw new IllegalArgumentException("Invalid qubit supplied.");
        return this.qubitOperations.get(qubit) == null;
    }

    /**
     * Checks whether supplied operation can be safely added to this step.
     * @param operation quantum operation to be added.
     * @return true, if operation can be safely added.
     */
    public boolean canAddOperation(Operation operation) {
        if (operation == null)
            throw new IllegalArgumentException("Invalid operation supplied (NULL).");
        for (int qubit : operation.involvedQubits()) {
            if (!qubitOperations.containsKey(qubit))
                throw new IllegalArgumentException("Supplied operation addresses qubits outside of step's boundries.");
            if (qubitOperations.get(qubit) != null)
                return false;
        }
        return true;
    }

    /**
     * Adds the supplied operation to this quantum step. A runtime exception is thrown if this action cannot be
     * performed.
     * @param operation quantum operation to be added.
     */
    public void addOperation(Operation operation) {
        if (!canAddOperation(operation))
            throw new IllegalArgumentException("Operation cannot be added to this step (qubits already occupied).");
        for (int qubit : operation.involvedQubits())
            qubitOperations.put(qubit, operation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Step step = (Step) o;
        return qubitOperations.equals(step.qubitOperations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qubitOperations);
    }

    private boolean isValidQubit(int qubit) { return qubit >= 0 && qubit < this.qubitOperations.size(); }
}
