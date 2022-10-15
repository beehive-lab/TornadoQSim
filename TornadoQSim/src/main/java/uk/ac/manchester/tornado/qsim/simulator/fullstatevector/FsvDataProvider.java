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
package uk.ac.manchester.tornado.qsim.simulator.fullstatevector;

import uk.ac.manchester.tornado.qsim.circuit.Step;
import uk.ac.manchester.tornado.qsim.circuit.operation.*;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;

import java.util.LinkedList;
import java.util.List;

/**
 * Provides operation data for full state vector simulator.
 * 
 * @author Ales Kubicek
 */
class FsvDataProvider {

    /**
     * Constructs unitary data provider.
     */
    protected FsvDataProvider() {
    }

    /**
     * Gets unitary matrix for the supplied operation.
     * 
     * @param operation
     *            quantum operation.
     * @return unitary matrix representing the quantum operation.
     */
    protected ComplexTensor getOperationData(Operation operation) {
        OperationDataProvider provider = OperationDataProvider.getInstance();
        switch (operation.operationType()) {
            case Gate:
                return provider.getData((Gate) operation);
            case ControlGate:
                return provider.getData(((ControlGate) operation).gate());
            default:
                throw new UnsupportedOperationException("Operation type '" + operation.operationType() + "' is not supported in a full state vector simulator.");
        }
    }

    /**
     * Gets list of all unitary operations in the supplied quantum step.
     * 
     * @param noQubits
     *            number of qubits in the supplied step.
     * @param step
     *            single step in a quantum circuit.
     * @return list of all step unitary operations.
     */
    protected List<Operation> getStepOperations(int noQubits, Step step) {
        List<Operation> operationData = new LinkedList<>();

        int qubit = 0;
        while (qubit < noQubits) {
            if (step.isQubitFree(qubit)) {
                qubit++;
            } else {
                Operation operation = step.getOperation(qubit);
                operationData.add(operation);
                qubit += operation.size();
            }
        }
        return operationData;
    }
}
