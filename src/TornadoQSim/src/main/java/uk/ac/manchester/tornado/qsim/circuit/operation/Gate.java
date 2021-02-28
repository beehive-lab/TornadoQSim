package uk.ac.manchester.tornado.qsim.circuit.operation;

import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.OperationType;

import java.util.Objects;

/**
 * Represents a standard quantum logic gate that acts on a single target qubit in the circuit.
 * @author Ales Kubicek
 */
public class Gate implements Operation {
    private final GateType type;
    private final int target;

    /**
     * Constructs a standard quantum logic gate.
     * @param type type of the standard quantum gate.
     * @param target qubit to which the standard quatum gate applies.
     */
    public Gate(GateType type, int target) {
        if (target < 0)
            throw new IllegalArgumentException("Invalid target qubit supplied.");
        this.type = type;
        this.target = target;
    }

    /**
     * Gets the type of the standard quantum gate.
     * @return gate type of the quantum gate.
     */
    public GateType type() { return type; }

    /**
     * Gets the target qubit.
     * @return target qubit.
     */
    public int targetQubit() { return target; }

    @Override
    public int[] involvedQubits() {
        return new int[] { target };
    }

    @Override
    public int size() { return 1; }

    @Override
    public OperationType operationType() { return OperationType.Gate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gate gate = (Gate) o;
        return target == gate.target && type == gate.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, target);
    }
}
