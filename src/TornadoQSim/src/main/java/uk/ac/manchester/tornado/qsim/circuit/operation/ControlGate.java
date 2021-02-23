package uk.ac.manchester.tornado.qsim.circuit.operation;

import uk.ac.manchester.tornado.qsim.circuit.Qubit;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;

import java.util.Objects;

/**
 * Represents a standard quantum logic gate that is conditionally controlled by another qubit in the circuit.
 * The control qubit does not need to be adjacent to the target qubit.
 * @author Ales Kubicek
 */
public class ControlGate implements Operation {
    private final GateType type;
    private final Qubit control;
    private final Qubit target;

    /**
     * Constructs a standard quantum controlled gate.
     * @param type type of the standard quantum gate (controlled by control qubit).
     * @param control qubit that conditionally controls the standard quantum gate.
     * @param target qubit to which the standard quatum gate applies.
     */
    protected ControlGate(GateType type, Qubit control, Qubit target) {
        if (control == null || target == null)
            throw new IllegalArgumentException("Both control and target qubits must be defined (not NULL).");
        if (control == target)
            throw new IllegalArgumentException("Control and target qubits must act on different qubits.");
        this.type = type;
        this.control = control;
        this.target = target;
    }

    /**
     * Gets the type of the standard quantum gate.
     * @return gate type of the quantum gate.
     */
    public GateType type() { return this.type; }

    /**
     * Gets the control qubit.
     * @return control qubit.
     */
    public Qubit controlQubit() { return  this.control; }

    /**
     * Gets the target qubit.
     * @return target qubit.
     */
    public Qubit targetQubit() { return this.target; }

    @Override
    public Qubit[] involvedQubits() {
        return new Qubit[] { this.control, this.target };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControlGate that = (ControlGate) o;
        return type == that.type && control.equals(that.control) && target.equals(that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, control, target);
    }
}
