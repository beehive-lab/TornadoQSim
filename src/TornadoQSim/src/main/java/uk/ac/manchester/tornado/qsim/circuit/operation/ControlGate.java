package uk.ac.manchester.tornado.qsim.circuit.operation;

import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;

import java.util.Objects;

/**
 * Represents a standard quantum logic gate that is conditionally controlled by another qubit in the circuit.
 * The control qubit does not need to be adjacent to the target qubit.
 * @author Ales Kubicek
 */
public class ControlGate implements Operation {
    private final GateType type;
    private final int control;
    private final int target;

    /**
     * Constructs a standard quantum controlled gate.
     * @param type type of the standard quantum gate (controlled by the control qubit).
     * @param control qubit that conditionally controls the standard quantum gate.
     * @param target qubit to which the standard quatum gate applies.
     */
    protected ControlGate(GateType type, int control, int target) {
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
    public int controlQubit() { return  this.control; }

    /**
     * Gets the target qubit.
     * @return target qubit.
     */
    public int targetQubit() { return this.target; }

    @Override
    public int[] involvedQubits() {
        return new int[] { this.control, this.target };
    }

    /**
     * {@inheritdoc}
     * This includes the qubits between the control and target qubits.
     */
    @Override
    public int size() {
        return Math.abs(this.target - this.control) + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControlGate that = (ControlGate) o;
        return control == that.control && target == that.target && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, control, target);
    }
}
