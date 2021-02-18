package uk.ac.manchester.tornado.qsim.circuit;

import uk.ac.manchester.tornado.qsim.circuit.operation.Operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Step {
    private final ArrayList<Operation> operations;
    private final HashSet<Qubit> freeQubits;

    public Step(Qubit[] qubits) {
        this.operations = new ArrayList<Operation>();
        this.freeQubits = new HashSet<Qubit>(Arrays.asList(qubits));
    }

    public List<Operation> getOperations() {
        return this.operations;
    }

    public boolean canAddOperation(Operation operation) {
        return areFreeQubits(operation.involvedQubits());
    }

    public void addOperation(Operation operation) {
        Qubit[] involvedQubits = operation.involvedQubits();

        if (!areFreeQubits(involvedQubits))
            throw new IllegalArgumentException("Operation '" + operation + "' cannot be added to this step - "
                    + "acts on qubits that are already involved in different operation");

        for (Qubit qubit : involvedQubits)
            this.freeQubits.remove(qubit);
        this.operations.add(operation);
    }

    private boolean areFreeQubits(Qubit[] qubits) {
        for (Qubit qubit : qubits)
            if (!this.freeQubits.contains(qubit))
                return false;
        return true;
    }
}
