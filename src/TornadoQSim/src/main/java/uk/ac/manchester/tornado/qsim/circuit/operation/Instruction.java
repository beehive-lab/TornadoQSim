package uk.ac.manchester.tornado.qsim.circuit.operation;

import uk.ac.manchester.tornado.qsim.circuit.Qubit;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.InstructionType;

import java.util.Objects;

/**
 * Represents a standard non-unitary operation (instruction) that acts on single target qubit in the circuit.
 * @author Ales Kubicek
 */
public class Instruction implements Operation {
    private final InstructionType type;
    private final Qubit target;

    /**
     * Constructs a quantum instruction.
     * @param type type of the standard quantum instruction.
     * @param target qubit to which the standard quatum instruction applies.
     */
    public Instruction(InstructionType type, Qubit target) {
        if (target == null)
            throw new IllegalArgumentException("Target qubit must be defined (not NULL).");
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
    public Qubit targetQubit() { return this.target; }

    @Override
    public Qubit[] involvedQubits() {
        return new Qubit[] { this.target };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instruction that = (Instruction) o;
        return type == that.type && target.equals(that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, target);
    }
}
