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

import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.circuit.State;
import uk.ac.manchester.tornado.qsim.circuit.Step;
import uk.ac.manchester.tornado.qsim.circuit.operation.ControlGate;
import uk.ac.manchester.tornado.qsim.circuit.operation.Function;
import uk.ac.manchester.tornado.qsim.circuit.operation.Gate;
import uk.ac.manchester.tornado.qsim.circuit.operation.Operation;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;

import java.util.List;

/**
 * Represents a quantum circuit simulator that applies each quantum gate by
 * iterating over the full state vector. The simulation process is not
 * accelerated on any heterogeneous hardware. This simulation process follows
 * the full state vector / wavefunction simulation model of quantum computation.
 * 
 * @author Ales Kubicek
 */
public class FsvSimulatorStandard implements Simulator {
    private final FsvDataProvider dataProvider;

    /**
     * Constructs a full state vector simulator.
     */
    public FsvSimulatorStandard() {
        dataProvider = new FsvDataProvider();
    }

    @Override
    public State simulateFullState(Circuit circuit) {
        if (circuit == null)
            throw new IllegalArgumentException("Invalid circuit supplied (NULL).");

        State resultState = new State(circuit.qubitCount());
        List<Step> steps = circuit.getSteps();

        for (Step step : steps) {
            List<Operation> operations = dataProvider.getStepOperations(circuit.qubitCount(), step);
            for (Operation operation : operations) {
                switch (operation.operationType()) {
                    case Gate:
                        applyGate(resultState, (Gate) operation);
                        break;
                    case ControlGate:
                        applyControlGate(resultState, (ControlGate) operation);
                        break;
                    case Function:
                        applyStandardFunction(resultState, (Function) operation);
                        break;
                    case CustomFunction:
                        applyCustomFunction(resultState, (Function) operation);
                        break;
                    default:
                        throw new UnsupportedOperationException("Operation type '" + operation.operationType() + "' is not supported in a full state vector simulator.");
                }
            }
        }

        return resultState;
    }

    @Override
    public int simulateAndCollapse(Circuit circuit) {
        return simulateFullState(circuit).collapse();
    }

    private void applyGate(State state, Gate gate) {
        ComplexTensor gateData = dataProvider.getOperationData(gate);
        float[] gateReal = new float[] { gateData.getElement(0, 0).real(), gateData.getElement(0, 1).real(), gateData.getElement(1, 0).real(), gateData.getElement(1, 1).real(), };
        float[] gateImag = new float[] { gateData.getElement(0, 0).imag(), gateData.getElement(0, 1).imag(), gateData.getElement(1, 0).imag(), gateData.getElement(1, 1).imag(), };
        FsvOperand.applyGate(gate.targetQubit(), state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData(), state.size() / 2, gateReal, gateImag);
    }

    private void applyControlGate(State state, ControlGate controlGate) {
        ComplexTensor gateData = dataProvider.getOperationData(controlGate);
        float[] gateReal = new float[] { gateData.getElement(0, 0).real(), gateData.getElement(0, 1).real(), gateData.getElement(1, 0).real(), gateData.getElement(1, 1).real(), };
        float[] gateImag = new float[] { gateData.getElement(0, 0).imag(), gateData.getElement(0, 1).imag(), gateData.getElement(1, 0).imag(), gateData.getElement(1, 1).imag(), };
        FsvOperand.applyControlGate(controlGate.targetQubit(), controlGate.controlQubit(), state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData(), state.size() / 2, gateReal,
                gateImag);
    }

    private void applyStandardFunction(State state, Function standardFunction) {
        // TODO: implement data generation for standard functions
        throw new UnsupportedOperationException("Standard functions are not yet supported.");
    }

    private void applyCustomFunction(State state, Function customFunction) {
        // TODO: implement data generation for custom functions
        throw new UnsupportedOperationException("Custom functions are not yet supported.");
    }
}
