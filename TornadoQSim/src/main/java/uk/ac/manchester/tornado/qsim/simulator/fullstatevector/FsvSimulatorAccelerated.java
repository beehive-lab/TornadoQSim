/*
 * This file is part of TornadoQSim:
 * A Java-based quantum computing framework accelerated with TornadoVM.
 *
 * URL: https://github.com/beehive-lab/TornadoQSim
 *
 * Copyright (c) 2021-2023, APT Group, Department of Computer Science,
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

import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;
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
 * iterating over the full state vector. The simulation process is accelerated
 * on heterogeneous hardware via TornadoVM. This simulation process follows the
 * full state vector / wavefunction simulation model of quantum computation.
 * 
 * @author Ales Kubicek
 */
public class FsvSimulatorAccelerated implements Simulator {
    private final FsvDataProvider dataProvider;

    private TaskGraph applyGateTaskGraph;
    private TaskGraph applyControlTaskGraph;

    private int[] targetQubit;
    private int[] controlQubit;
    private float[] gateReal;
    private float[] gateImag;
    private float[] stateReal;
    private float[] stateImag;
    private float[] stateRealControl;
    private float[] stateImagControl;

    /**
     * Constructs a full state vector simulator.
     */
    public FsvSimulatorAccelerated(int noQubits) {
        dataProvider = new FsvDataProvider();
        targetQubit = new int[1];
        controlQubit = new int[1];
    }

    private void initializeStateArrays(State state) {
        if (stateReal == null) {
            stateReal = new float[state.getStateVector().getRawRealData().length];
        }
        if (stateImag == null) {
            stateImag = new float[state.getStateVector().getRawImagData().length];
        }
        if (stateRealControl == null) {
            stateRealControl = new float[state.getStateVector().getRawRealData().length];
        }
        if (stateImagControl == null) {
            stateImagControl = new float[state.getStateVector().getRawImagData().length];
        }
    }

    @Override
    public State simulateFullState(Circuit circuit) {
        if (circuit == null)
            throw new IllegalArgumentException("Invalid circuit supplied (NULL).");

        State resultState = new State(circuit.qubitCount());
        List<Step> steps = circuit.getSteps();

        initializeStateArrays(resultState);

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
        updateInputDataOfTaskGraph(state, gate);
        applyGateTaskGraph.execute();
        updateOutputDataOfGate(state);
    }

    private void updateInputDataOfTaskGraph(State state, Gate gate) {
        int halfRows = state.size() / 2;
        ComplexTensor gateData = dataProvider.getOperationData(gate);

        if (applyGateTaskGraph == null) {
            targetQubit[0] = gate.targetQubit()[0];
            System.arraycopy(state.getStateVector().getRawRealData(), 0, stateReal, 0, state.getStateVector().getRawRealData().length);
            System.arraycopy(state.getStateVector().getRawImagData(), 0, stateImag, 0, state.getStateVector().getRawImagData().length);
            if (gateReal == null) {
                gateReal = new float[gateData.size()];
            }
            if (gateImag == null) {
                gateImag = new float[gateData.size()];
            }
            System.arraycopy(gateData.getRawRealData(), 0, gateReal, 0, gateData.getRawRealData().length);
            System.arraycopy(gateData.getRawImagData(), 0, gateImag, 0, gateData.getRawRealData().length);

            // @formatter:off
            applyGateTaskGraph = new TaskGraph("applyGate")
                    .transferToDevice(DataTransferMode.EVERY_EXECUTION, targetQubit, stateReal, stateImag, gateReal, gateImag)
                    .task("applyGateTask", FsvOperand::applyGate, targetQubit, stateReal, stateImag, halfRows, gateReal, gateImag)
                    .transferToHost(DataTransferMode.EVERY_EXECUTION ,stateReal, stateImag);
            // @formatter:on
        } else {
            targetQubit[0] = gate.targetQubit()[0];
            System.arraycopy(state.getStateVector().getRawRealData(), 0, stateReal, 0, state.getStateVector().getRawRealData().length);
            System.arraycopy(state.getStateVector().getRawImagData(), 0, stateImag, 0, state.getStateVector().getRawImagData().length);
            System.arraycopy(gateData.getRawRealData(), 0, gateReal, 0, gateData.getRawRealData().length);
            System.arraycopy(gateData.getRawImagData(), 0, gateImag, 0, gateData.getRawRealData().length);
        }
    }

    private void updateOutputDataOfGate(State state) {
        System.arraycopy(stateReal, 0, state.getStateVector().getRawRealData(), 0, stateReal.length);
        System.arraycopy(stateImag, 0, state.getStateVector().getRawImagData(), 0, stateImag.length);
    }

    private void applyControlGate(State state, ControlGate controlGate) {
        updateInputDataOfTaskGraph(state, controlGate);
        applyControlTaskGraph.execute();
        updateOutputDataOfControlGate(state);
    }

    private void updateInputDataOfTaskGraph(State state, ControlGate gate) {
        int halfRows = state.size() / 2;
        ComplexTensor gateData = dataProvider.getOperationData(gate);

        if (applyControlTaskGraph == null) {
            targetQubit[0] = gate.targetQubit()[0];
            controlQubit[0] = gate.controlQubit()[0];
            System.arraycopy(state.getStateVector().getRawRealData(), 0, stateRealControl, 0, state.getStateVector().getRawRealData().length);
            System.arraycopy(state.getStateVector().getRawImagData(), 0, stateImagControl, 0, state.getStateVector().getRawImagData().length);
            if (gateReal == null) {
                gateReal = new float[gateData.size()];
            }
            if (gateImag == null) {
                gateImag = new float[gateData.size()];
            }
            System.arraycopy(gateData.getRawRealData(), 0, gateReal, 0, gateData.getRawRealData().length);
            System.arraycopy(gateData.getRawImagData(), 0, gateImag, 0, gateData.getRawRealData().length);

            // @formatter:off
            applyControlTaskGraph = new TaskGraph("applyControlGate")
                    .transferToDevice(DataTransferMode.EVERY_EXECUTION, targetQubit, controlQubit, stateRealControl, stateImagControl, gateReal, gateImag)
                    .task("applyControlGateTask", FsvOperand::applyControlGate, targetQubit, controlQubit, stateRealControl, stateImagControl, halfRows, gateReal, gateImag)
                    .transferToHost(DataTransferMode.EVERY_EXECUTION, stateRealControl, stateImagControl);
            // @formatter:on
        } else {
            targetQubit[0] = gate.targetQubit()[0];
            controlQubit[0] = gate.controlQubit()[0];
            System.arraycopy(state.getStateVector().getRawRealData(), 0, stateRealControl, 0, state.getStateVector().getRawRealData().length);
            System.arraycopy(state.getStateVector().getRawImagData(), 0, stateImagControl, 0, state.getStateVector().getRawImagData().length);
            System.arraycopy(gateData.getRawRealData(), 0, gateReal, 0, gateData.getRawRealData().length);
            System.arraycopy(gateData.getRawImagData(), 0, gateImag, 0, gateData.getRawRealData().length);
        }
    }

    private void updateOutputDataOfControlGate(State state) {
        System.arraycopy(stateRealControl, 0, state.getStateVector().getRawRealData(), 0, stateRealControl.length);
        System.arraycopy(stateImagControl, 0, state.getStateVector().getRawImagData(), 0, stateImagControl.length);
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