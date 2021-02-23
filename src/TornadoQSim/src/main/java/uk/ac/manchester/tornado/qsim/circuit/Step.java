package uk.ac.manchester.tornado.qsim.circuit;

import uk.ac.manchester.tornado.qsim.circuit.operation.Operation;

import java.util.*;

public class Step {
    private final HashMap<Integer, Operation> operations;

    public Step(int noQubits) {
        this.operations = new HashMap<Integer, Operation>();
        for (int qubit = 0; qubit < noQubits; qubit++)
            this.operations.put(qubit, null);
    }

    public Operation getOperation(int qubit) {
        return this.operations.get(qubit);
    }

    public boolean isQubitFree(int qubit) { return this.operations.get(qubit) == null; }

    public boolean canAddOperation(Operation operation) {
        // TODO: implement
        return false;
    }

    public void addOperation(Operation operation) {
        // TODO: implement
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Step step = (Step) o;
        return operations.equals(step.operations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operations);
    }
}
