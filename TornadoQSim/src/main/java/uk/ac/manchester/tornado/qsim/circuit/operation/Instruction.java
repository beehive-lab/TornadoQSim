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

import uk.ac.manchester.tornado.qsim.circuit.operation.enums.InstructionType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.OperationType;

import java.util.Objects;

/**
 * Represents a standard non-unitary operation (instruction) that acts on single
 * target qubit in the circuit.
 * 
 * @author Ales Kubicek
 */
public class Instruction implements Operation {
    private final InstructionType type;
    private final int target;

    /**
     * Constructs a quantum instruction.
     * 
     * @param type
     *            type of the standard quantum instruction.
     * @param target
     *            qubit to which the standard quatum instruction applies.
     */
    public Instruction(InstructionType type, int target) {
        if (target < 0)
            throw new IllegalArgumentException("Invalid target qubit supplied.");
        this.type = type;
        this.target = target;
    }

    /**
     * Gets the type of the standard quantum instruction.
     * 
     * @return instruction type.
     */
    public InstructionType type() {
        return type;
    }

    /**
     * Gets the target qubit.
     * 
     * @return target qubit.
     */
    public int targetQubit() {
        return target;
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
        return OperationType.Instruction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Instruction that = (Instruction) o;
        return target == that.target && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, target);
    }
}
