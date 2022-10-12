/*
 * This file is part of TornadoQSim:
 * A Java-based quantum computing framework accelerated with TornadoVM.
 *
 * URL: https://github.com/beehive-lab/TornadoQSim
 *
 * Copyright (c) 2021-2022, APT Group, Department of Computer Science,
 * The University of Manchester. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.manchester.tornado.qsim.circuit;

import uk.ac.manchester.tornado.qsim.circuit.operation.Operation;

import java.util.HashMap;
import java.util.Objects;

/**
 * Represents a single step in a quantum circuit. It ensures that only such
 * quantum operations that can fit into the step are allowed to be added.
 * 
 * @author Ales Kubicek
 */
public class Step {
    private final HashMap<Integer, Operation> qubitOperations;
    private int operationCount;

    /**
     * Constructs a quantum circuit step with the supplied number of qubits.
     * 
     * @param noQubits
     *            number of qubits in the quantum circuit step.
     */
    public Step(int noQubits) {
        if (noQubits < 1)
            throw new IllegalArgumentException("Number of qubits in a step must be greater than 0.");
        qubitOperations = new HashMap<>();
        for (int qubit = 0; qubit < noQubits; qubit++)
            qubitOperations.put(qubit, null);
    }

    /**
     * Gets the number of operations present in this quantum circuit step.
     * 
     * @return total number of operations in this step.
     */
    public int getOperationCount() {
        return operationCount;
    }

    /**
     * Gets an operation that occupies the queried qubit. NULL is returned if no
     * operation occupies the queried qubit.
     * 
     * @param qubit
     *            queried qubit.
     * @return quantum operation (can be NULL)
     */
    public Operation getOperation(int qubit) {
        if (!isValidQubit(qubit))
            throw new IllegalArgumentException("Invalid qubit supplied.");
        return qubitOperations.get(qubit);
    }

    /**
     * Checks whether the queried qubit is free (no operation occupies this qubit).
     * 
     * @param qubit
     *            queried qubit.
     * @return true, if no operation occupies this qubit.
     */
    public boolean isQubitFree(int qubit) {
        if (!isValidQubit(qubit))
            throw new IllegalArgumentException("Invalid qubit supplied.");
        return qubitOperations.get(qubit) == null;
    }

    /**
     * Checks whether supplied operation can be safely added to this step.
     * 
     * @param operation
     *            quantum operation to be added.
     * @return true, if operation can be safely added.
     */
    public boolean canAddOperation(Operation operation) {
        if (operation == null)
            throw new IllegalArgumentException("Invalid operation supplied (NULL).");
        for (int qubit : operation.involvedQubits()) {
            if (!qubitOperations.containsKey(qubit))
                throw new IllegalArgumentException("Supplied operation addresses qubits outside of step's boundries.");
            if (qubitOperations.get(qubit) != null)
                return false;
        }
        return true;
    }

    /**
     * Adds the supplied operation to this quantum step. A runtime exception is
     * thrown if this action cannot be performed.
     * 
     * @param operation
     *            quantum operation to be added.
     */
    public void addOperation(Operation operation) {
        if (!canAddOperation(operation))
            throw new IllegalArgumentException("Operation cannot be added to this step (qubits already occupied).");
        for (int qubit : operation.involvedQubits())
            qubitOperations.put(qubit, operation);
        operationCount++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Step step = (Step) o;
        return qubitOperations.equals(step.qubitOperations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qubitOperations);
    }

    private boolean isValidQubit(int qubit) {
        return qubit >= 0 && qubit < qubitOperations.size();
    }
}
