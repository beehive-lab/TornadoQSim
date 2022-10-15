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

import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.OperationType;

import java.util.Objects;

/**
 * Represents a standard quantum logic gate that is conditionally controlled by
 * another qubit in the circuit. The control qubit does not need to be adjacent
 * to the target qubit.
 * 
 * @author Ales Kubicek
 */
public class ControlGate implements Operation {
    private final Gate gate;
    private final int[] control;
    private final int[] target;

    /**
     * Constructs a standard quantum controlled gate.
     * 
     * @param gate
     *            standard quantum gate (controlled by the control qubit).
     * @param control
     *            qubit that conditionally controls the standard quantum gate.
     * @param target
     *            qubit to which the standard quatum gate applies.
     */
    public ControlGate(Gate gate, int control, int target) {
        if (gate == null)
            throw new IllegalArgumentException("Ivalid gate supplied (NULL).");
        if (control < 0 || target < 0)
            throw new IllegalArgumentException("Invalid control or target qubit supplied.");
        if (control == target)
            throw new IllegalArgumentException("Control and target qubits must act on different qubits.");
        this.gate = gate;
        this.control = new int[1];
        this.control[0] = control;
        this.target = new int[1];
        this.target[0] = target;
    }

    /**
     * Gets the the standard quantum gate.
     * 
     * @return quantum gate controlled by the control qubit.
     */
    public Gate gate() {
        return gate;
    }

    /**
     * Gets the control qubit.
     * 
     * @return control qubit.
     */
    public int[] controlQubit() {
        return control;
    }

    /**
     * Gets the target qubit.
     * 
     * @return target qubit.
     */
    public int[] targetQubit() {
        return target;
    }

    @Override
    public int[] involvedQubits() {
        int[] qubits = new int[size()];
        int fromQubit = control[0] < target[0] ? control[0] : target[0];
        for (int i = 0; i < qubits.length; i++)
            qubits[i] = fromQubit + i;
        return qubits;
    }

    /**
     * {@inheritdoc} This includes the qubits between the control and target qubits.
     */
    @Override
    public int size() {
        return Math.abs(target[0] - control[0]) + 1;
    }

    @Override
    public OperationType operationType() {
        return OperationType.ControlGate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ControlGate that = (ControlGate) o;
        return control[0] == that.control[0] && target[0] == that.target[0] && gate.equals(that.gate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gate, control[0], target[0]);
    }
}
