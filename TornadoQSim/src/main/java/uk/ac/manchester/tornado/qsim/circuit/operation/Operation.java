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
package uk.ac.manchester.tornado.qsim.circuit.operation;

import uk.ac.manchester.tornado.qsim.circuit.operation.enums.OperationType;

/**
 * Reperesents any operation that can be applied to a quantum circuit at some
 * step.
 * 
 * @author Ales Kubicek
 */
public interface Operation {
    /**
     * Gets all involved qubits in this operation.
     * 
     * @return involved qubits.
     */
    public int[] involvedQubits();

    /**
     * Gets the size (number of qubits), that the operation occupies in a circuit
     * step.
     * 
     * @return operation size.
     */
    public int size();

    /**
     * Gets the operation type.
     * 
     * @return operation type.
     */
    public OperationType operationType();
}
