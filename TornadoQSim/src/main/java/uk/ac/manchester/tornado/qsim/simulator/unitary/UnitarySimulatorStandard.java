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
package uk.ac.manchester.tornado.qsim.simulator.unitary;

import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.circuit.State;
import uk.ac.manchester.tornado.qsim.circuit.Step;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;

import java.util.List;
import java.util.ListIterator;

/**
 * Represents a quantum circuit simulator that composes a unitary matrix for
 * each step of the circuit in order to simulated the final state. The
 * simulation process is not accelerated on any heterogeneous hardware. This
 * simulation process follows the standard mathematical model of quantum
 * computation.
 * 
 * @author Ales Kubicek
 */
public class UnitarySimulatorStandard implements Simulator {
    private final UnitaryDataProvider dataProvider;

    /**
     * Constructs a unitary matrix simulator.
     */
    public UnitarySimulatorStandard() {
        dataProvider = new UnitaryDataProvider(false);
    }

    @Override
    public State simulateFullState(Circuit circuit) {
        if (circuit == null)
            throw new IllegalArgumentException("Invalid circuit supplied (NULL).");

        List<Step> steps = circuit.getSteps();
        ListIterator<Step> iterator = steps.listIterator(steps.size());

        ComplexTensor unitaryA,unitaryB;

        unitaryA = prepareStepUnitary(circuit.qubitCount(), iterator.previous());
        while (iterator.hasPrevious()) {
            unitaryB = prepareStepUnitary(circuit.qubitCount(), iterator.previous());
            unitaryA = matrixMultiplication(unitaryA, unitaryB);
        }

        State resultState = new State(circuit.qubitCount());
        resultState.setStateVector(matrixVectorMultiplication(unitaryA, resultState.getStateVector()));

        return resultState;
    }

    @Override
    public int simulateAndCollapse(Circuit circuit) {
        return simulateFullState(circuit).collapse();
    }

    private ComplexTensor prepareStepUnitary(int noQubits, Step step) {
        List<ComplexTensor> stepOperationData = dataProvider.getStepOperationData(noQubits, step);
        ListIterator<ComplexTensor> iterator = stepOperationData.listIterator(stepOperationData.size());
        ComplexTensor stepUnitary = iterator.previous();
        while (iterator.hasPrevious())
            stepUnitary = kroneckerProduct(stepUnitary, iterator.previous());
        return stepUnitary;
    }

    private ComplexTensor matrixMultiplication(ComplexTensor a, ComplexTensor b) {
        int resultRows = a.shape()[0];
        int resultCols = b.shape()[1];
        ComplexTensor result = new ComplexTensor(resultRows, resultCols);
        UnitaryOperand.matrixProduct(a.getRawRealData(), a.getRawImagData(), a.shape()[0], a.shape()[1], b.getRawRealData(), b.getRawImagData(), b.shape()[1], result.getRawRealData(),
                result.getRawImagData());
        return result;
    }

    private ComplexTensor kroneckerProduct(ComplexTensor a, ComplexTensor b) {
        int resultRows = a.shape()[0] * b.shape()[0];
        int resultCols = a.shape()[1] * b.shape()[1];
        ComplexTensor result = new ComplexTensor(resultRows, resultCols);
        UnitaryOperand.kroneckerProduct(a.getRawRealData(), a.getRawImagData(), a.shape()[0], a.shape()[1], b.getRawRealData(), b.getRawImagData(), b.shape()[0], b.shape()[1], result.getRawRealData(),
                result.getRawImagData());
        return result;
    }

    private ComplexTensor matrixVectorMultiplication(ComplexTensor matrix, ComplexTensor vector) {
        ComplexTensor result = new ComplexTensor(vector.size());
        UnitaryOperand.matrixVectorProduct(matrix.getRawRealData(), matrix.getRawImagData(), matrix.shape()[0], matrix.shape()[1], vector.getRawRealData(), vector.getRawImagData(),
                result.getRawRealData(), result.getRawImagData());
        return result;
    }

}
