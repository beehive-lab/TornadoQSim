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

import uk.ac.manchester.tornado.qsim.circuit.operation.enums.FunctionType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.OperationType;

import java.util.Objects;

/**
 * Represents a unitary quantum function that is a composit of multiple standard
 * quantum gates. The function can be applied to adjacent qubits only and the
 * main purpose is to simplify and speed up the quantum simulation by storing /
 * directly computing the function's unitary matrix (via operation data
 * provider). The function can be identified by a name (fully custom unitary
 * matrix, registered via operation data provider) or by standard function type
 * (eg. QFT).
 * 
 * @author Ales Kubicek
 */
public class Function implements Operation {
    private final FunctionType type;
    private final String name;
    private final int fromQubit,toQubit;

    /**
     * Constructs a standard quantum function.
     * 
     * @param type
     *            type of the standard function.
     * @param fromQubit
     *            function range start qubit (inclusive).
     * @param toQubit
     *            function range end qubit (inclusive).
     */
    public Function(FunctionType type, int fromQubit, int toQubit) {
        if (fromQubit > toQubit || fromQubit < 0)
            throw new IllegalArgumentException("Invalid qubit range supplied.");
        if (type == FunctionType.Custom)
            throw new IllegalArgumentException("Custom name needed when creating custom function.");
        this.type = type;
        this.name = "";
        this.fromQubit = fromQubit;
        this.toQubit = toQubit;
    }

    /**
     * Constructs custom quantum function.
     * 
     * @param name
     *            name of the custom function.
     * @param fromQubit
     *            function range start qubit (inclusive).
     * @param toQubit
     *            function range end qubit (inclusive).
     */
    public Function(String name, int fromQubit, int toQubit) {
        if (fromQubit > toQubit || fromQubit < 0)
            throw new IllegalArgumentException("Invalid qubit range supplied.");
        this.type = FunctionType.Custom;
        this.name = name;
        this.fromQubit = fromQubit;
        this.toQubit = toQubit;
    }

    /**
     * Gets the type of the quantum function.
     * 
     * @return function type of the quantum function.
     */
    public FunctionType type() {
        return type;
    }

    /**
     * Gets the name of the custom quantum function.
     * 
     * @return custom function name.
     */
    public String name() {
        return name;
    }

    /**
     * Gets target qubits.
     * 
     * @return target qubits.
     */
    public int[] targetQubits() {
        int[] qubits = new int[size()];
        for (int i = 0; i < qubits.length; i++)
            qubits[i] = fromQubit + i;
        return qubits;
    }

    @Override
    public int size() {
        return (toQubit - fromQubit) + 1;
    }

    @Override
    public int[] involvedQubits() {
        return targetQubits();
    }

    @Override
    public OperationType operationType() {
        return type == FunctionType.Custom ? OperationType.CustomFunction : OperationType.Function;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Function function = (Function) o;
        return fromQubit == function.fromQubit && toQubit == function.toQubit && type == function.type && name.equals(function.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, fromQubit, toQubit);
    }
}
