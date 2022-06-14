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
 * Represents a standard quantum logic gate that acts on a single target qubit
 * in the circuit.
 * 
 * @author Ales Kubicek
 */
public class Gate implements Operation {
    private final GateType type;
    private final int target;
    private final float phi;

    /**
     * Constructs a standard quantum logic gate.
     * 
     * @param type
     *            type of the standard quantum gate.
     * @param target
     *            qubit to which the standard quatum gate applies.
     */
    public Gate(GateType type, int target) {
        if (target < 0)
            throw new IllegalArgumentException("Invalid target qubit supplied.");
        if (type == GateType.R)
            throw new UnsupportedOperationException("Use constructor with phase parameter for 'R' gate.");
        this.type = type;
        this.target = target;
        this.phi = 0;
    }

    /**
     * Constructs a phase shift quantum logic gate.
     * 
     * @param type
     *            type of the standard quantum gate.
     * @param target
     *            qubit to which the standard quatum gate applies.
     * @param phi
     *            phase shift in radians.
     */
    public Gate(GateType type, int target, float phi) {
        if (target < 0)
            throw new IllegalArgumentException("Invalid target qubit supplied.");
        if (type != GateType.R)
            throw new UnsupportedOperationException("Use constructor without phase parameter for this type of gate.");
        this.type = type;
        this.target = target;
        this.phi = phi;
    }

    /**
     * Gets the type of the standard quantum gate.
     * 
     * @return gate type of the quantum gate.
     */
    public GateType type() {
        return type;
    }

    /**
     * Gets the target qubit.
     * 
     * @return target qubit.
     */
    public int[] targetQubit() {
        return new int[] { target };
    }

    /**
     * Gets the phase shift in radians. Only for 'R' quantum gate.
     * 
     * @return phase shift in radians.
     */
    public float phi() {
        if (type != GateType.R)
            throw new UnsupportedOperationException("Invalid operation for gates other than 'R'.");
        return phi;
    }

    @Override
    public int[] involvedQubits() {
        return new int[] { target };
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public OperationType operationType() {
        return OperationType.Gate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Gate gate = (Gate) o;
        return target == gate.target && Float.compare(gate.phi, phi) == 0 && type == gate.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, target, phi);
    }
}
