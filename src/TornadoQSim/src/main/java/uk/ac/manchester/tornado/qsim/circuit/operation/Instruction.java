package uk.ac.manchester.tornado.qsim.circuit.operation;

import uk.ac.manchester.tornado.qsim.circuit.operation.enums.InstructionType;

import java.util.Objects;

/**
 * Represents a standard non-unitary operation (instruction) that acts on single target qubit in the circuit.
 * @author Ales Kubicek
 */
public class Instruction implements Operation {
    private final InstructionType type;
    private final int target;

    /**
     * Constructs a quantum instruction.
     * @param type type of the standard quantum instruction.
     * @param target qubit to which the standard quatum instruction applies.
     */
    public Instruction(InstructionType type, int target) {
        if (target < 0)
            throw new IllegalArgumentException("Invalid target qubit supplied.");
        this.type = type;
        this.target = target;
    }

    /**
     * Gets the type of the standard quantum instruction.
     * @return instruction type.
     */
    public InstructionType type() { return this.type; }

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
        Instruction that = (Instruction) o;
        return target == that.target && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, target);
    }
}
