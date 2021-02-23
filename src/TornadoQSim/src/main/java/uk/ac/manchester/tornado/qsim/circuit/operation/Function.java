package uk.ac.manchester.tornado.qsim.circuit.operation;

import uk.ac.manchester.tornado.qsim.circuit.Qubit;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.FunctionType;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a unitary quantum function that is a composit of multiple standard quantum gates. The function can be
 * applied to adjacent qubits only (targets) and the main purpose is to simplify and speed up the quantum simulation
 * by storing / computing directly the function's unitary matrix (via operation data provider). The function can be
 * identified by name (fully custom unitary matrix, registered via operation data provider) or by standard function
 * types (eg. QFT).
 * @author Ales Kubicek
 */
public class Function implements Operation {
    private final FunctionType type;
    private final String name;
    private final Qubit[] targets;

    /**
     * Constructs standard quantum function.
     * @param type type of the standard function.
     * @param targets qubits to which the standard quatum function applies (must be adjacent).
     */
    protected Function(FunctionType type, Qubit... targets) {
        if (targets == null || targets.length < 1)
            throw new IllegalArgumentException("Target qubits must be defined (not NULL) and at least one.");
        if (!areQubitsAdjacent(targets))
            throw new IllegalArgumentException("Target qubits are not adjacent.");
        if (type == FunctionType.Custom)
            throw new IllegalArgumentException("Custom name needed when creating custom function.");
        this.type = type;
        this.name = "";
        this.targets = targets;
    }

    /**
     * Constructs custom quantum function.
     * @param name name of the custom function.
     * @param targets qubits to which the custom quatum function applies (must be adjacent).
     */
    protected Function(String name, Qubit... targets) {
        if (targets == null || targets.length < 1)
            throw new IllegalArgumentException("Target qubits must be defined (not NULL) and at least one.");
        if (!areQubitsAdjacent(targets))
            throw new IllegalArgumentException("Target qubits are not adjacent.");
        this.type = FunctionType.Custom;
        this.name = name;
        this.targets = targets;
    }

    /**
     * Gets the type of the quantum function.
     * @return function type of the quantum function.
     */
    public FunctionType type() { return this.type; }

    /**
     * Gets the name of the custom quantum function.
     * @return custom function name.
     */
    public String name() { return this.name; }

    /**
     * Gets the target qubits.
     * @return target qubits.
     */
    public Qubit[] targetQubits() { return this.targets; }

    /**
     * Gets the size of this quantum function.
     * @return function size.
     */
    public int size() { return this.targets.length; }

    @Override
    public Qubit[] involvedQubits() {
        return this.targets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Function function = (Function) o;
        return type == function.type && name.equals(function.name) && Arrays.equals(targets, function.targets);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type, name);
        result = 31 * result + Arrays.hashCode(targets);
        return result;
    }

    private boolean areQubitsAdjacent(Qubit[] qubits) {
        int startId = qubits[0].id();
        int endId = qubits[qubits.length - 1].id();
        boolean topDown = startId - endId < 0;
        for (int i = 1; i < qubits.length; i++)
            if (topDown && startId + i != qubits[i].id())
                return false;
            else if (!topDown && startId - i != qubits[i].id())
                return false;
        return true;
    }
}
