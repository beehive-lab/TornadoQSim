package uk.ac.manchester.tornado.qsim.circuit.operation;

import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.OperationType;

import java.util.Objects;

/**
 * Represents a standard quantum logic gate that is conditionally controlled by another qubit in the circuit.
 * The control qubit does not need to be adjacent to the target qubit.
 * @author Ales Kubicek
 */
public class ControlGate implements Operation {
    private final Gate gate;
    private final int control;
    private final int target;

    /**
     * Constructs a standard quantum controlled gate.
     * @param gate standard quantum gate (controlled by the control qubit).
     * @param control qubit that conditionally controls the standard quantum gate.
     * @param target qubit to which the standard quatum gate applies.
     */
    public ControlGate(Gate gate, int control, int target) {
        if (gate == null)
            throw new IllegalArgumentException("Ivalid gate supplied (NULL).");
        if (control < 0 || target < 0)
            throw new IllegalArgumentException("Invalid control or target qubit supplied.");
        if (control == target)
            throw new IllegalArgumentException("Control and target qubits must act on different qubits.");
        this.gate = gate;
        this.control = control;
        this.target = target;
    }

    /**
     * Gets the the standard quantum gate.
     * @return quantum gate controlled by the control qubit.
     */
    public Gate gate() { return gate; }

    /**
     * Gets the control qubit.
     * @return control qubit.
     */
    public int controlQubit() { return  control; }

    /**
     * Gets the target qubit.
     * @return target qubit.
     */
    public int targetQubit() { return target; }

    @Override
    public int[] involvedQubits() {
        int[] qubits = new int[size()];
        int fromQubit = control < target ? control : target;
        for (int i = 0; i < qubits.length; i++)
            qubits[i] = fromQubit + i;
        return qubits;
    }

    /**
     * {@inheritdoc}
     * This includes the qubits between the control and target qubits.
     */
    @Override
    public int size() {
        return Math.abs(target - control) + 1;
    }

    @Override
    public OperationType operationType() { return OperationType.ControlGate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControlGate that = (ControlGate) o;
        return control == that.control && target == that.target && gate.equals(that.gate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gate, control, target);
    }
}
