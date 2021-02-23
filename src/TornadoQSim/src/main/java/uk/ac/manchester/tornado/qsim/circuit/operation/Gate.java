package uk.ac.manchester.tornado.qsim.circuit.operation;

import uk.ac.manchester.tornado.qsim.circuit.Qubit;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;

import java.util.Objects;

/**
 * Represents a standard quantum logic gate that acts on single target qubit in the circuit.
 * @author Ales Kubicek
 */
public class Gate implements Operation {
    private final GateType type;
    private final Qubit target;

    /**
     * Constructs a standard quantum logic gate.
     * @param type type of the standard quantum gate.
     * @param target qubit to which the standard quatum gate applies.
     */
    public Gate(GateType type, Qubit target) {
        if (target == null)
            throw new IllegalArgumentException("Target qubit must be defined (not NULL).");
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
    public Qubit targetQubit() { return this.target; }

    @Override
    public Qubit[] involvedQubits() {
        return new Qubit[] { this.target };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gate gate = (Gate) o;
        return type == gate.type && target.equals(gate.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, target);
    }
}
