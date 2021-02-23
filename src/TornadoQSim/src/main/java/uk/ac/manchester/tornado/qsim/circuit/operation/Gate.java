package uk.ac.manchester.tornado.qsim.circuit.operation;

import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;

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
        this.type = type;
        this.target = target;
    }

    /**
     * Gets the type of the standard quantum gate.
     * @return gate type of the quantum gate.
     */
    public GateType type() { return this.type; }

    /**
     * Gets the target qubit.
     * @return target qubit.
     */
    public int targetQubit() { return this.target; }

    @Override
    public int[] involvedQubits() {
        return new int[] { this.target };
    }

    @Override
    public int size() { return 1; }

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
