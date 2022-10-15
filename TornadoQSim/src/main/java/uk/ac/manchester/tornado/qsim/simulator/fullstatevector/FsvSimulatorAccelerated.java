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

import uk.ac.manchester.tornado.api.TaskSchedule;
import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.circuit.State;
import uk.ac.manchester.tornado.qsim.circuit.Step;
import uk.ac.manchester.tornado.qsim.circuit.operation.ControlGate;
import uk.ac.manchester.tornado.qsim.circuit.operation.Function;
import uk.ac.manchester.tornado.qsim.circuit.operation.Gate;
import uk.ac.manchester.tornado.qsim.circuit.operation.Operation;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;

import java.util.Arrays;
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

    private TaskSchedule applyGateSchedule;
    private TaskSchedule applyControlGateSchedule;

    private int[] targetQubit;
    private int[] controlQubit;
    private float[] gateReal;
    private float[] gateImag;
    private float[] stateReal;
    private float[] stateImag;
    private float[] stateRealControl;
    private float[] stateImagControl;
    private float[] controlGateReal;
    private float[] controlGateImag;

    private long startGate,endGate;
    private long startArrayCopyForGate,endArrayCopyForGate;
    private long totalElapsedTimeOfArrayCopyForGate = 0;
    private long totalElapsedTimeOfGate = 0;
    private int numOfInvocationsOfGate;

    private long startControlGate,endControlGate;
    private long startArrayCopyForControlGate,endArrayCopyForControlGate;
    private long totalElapsedTimeOfArrayCopyForControlGate = 0;
    private long totalElapsedTimeOfControlGate = 0;
    private int numOfInvocationsOfControlGate;

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
        resetCounters();
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
                        numOfInvocationsOfGate++;
                        startGate = System.nanoTime();
                        applyGate(resultState, (Gate) operation);
                        endGate = System.nanoTime();
                        totalElapsedTimeOfGate += (endGate - startGate);
                        break;
                    case ControlGate:
                        numOfInvocationsOfControlGate++;
                        startControlGate = System.nanoTime();
                        applyControlGate(resultState, (ControlGate) operation);
                        endControlGate = System.nanoTime();
                        totalElapsedTimeOfControlGate += (endControlGate - startControlGate);
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
        applyGateSchedule.unlockObjectsFromMemory(resultState.getStateVector().getRawRealData(), resultState.getStateVector().getRawImagData());
        applyControlGateSchedule.unlockObjectsFromMemory(resultState.getStateVector().getRawRealData(), resultState.getStateVector().getRawImagData());
        return resultState;
    }

    public void resetCounters() {
        numOfInvocationsOfGate = 0;
        totalElapsedTimeOfGate = 0;
        totalElapsedTimeOfArrayCopyForGate = 0;
        numOfInvocationsOfControlGate = 0;
        totalElapsedTimeOfControlGate = 0;
        totalElapsedTimeOfArrayCopyForControlGate = 0;
        applyGateSchedule = null;
        applyControlGateSchedule = null;
        gateReal = null;
        controlGateReal = null;
    }

    @Override
    public int simulateAndCollapse(Circuit circuit) {
        return simulateFullState(circuit).collapse();
    }

    public long getTimeOfGate() {
        return totalElapsedTimeOfGate;
    }

    public long getNumOfInvocationsOfGate() {
        return numOfInvocationsOfGate;
    }

    public long getArrayCopyTimeOfGate() {
        return totalElapsedTimeOfArrayCopyForGate;
    }

    public long getTimeOfControlGate() {
        return totalElapsedTimeOfControlGate;
    }

    public long getNumOfInvocationsOfControlGate() {
        return numOfInvocationsOfControlGate;
    }

    public long getArrayCopyTimeOfControlGate() {
        return totalElapsedTimeOfArrayCopyForControlGate;
    }

    private void applyGate(State state, Gate gate) {
        startArrayCopyForGate = System.nanoTime();
        updateInputDataOfTaskSchedule(state, gate);
        endArrayCopyForGate = System.nanoTime();
        totalElapsedTimeOfArrayCopyForGate += (endArrayCopyForGate - startArrayCopyForGate);
        applyGateSchedule.execute();
        startArrayCopyForControlGate = System.nanoTime();
        updateOutputDataOfGate(gate);
        endArrayCopyForControlGate = System.nanoTime();
        totalElapsedTimeOfArrayCopyForGate += (endArrayCopyForGate - startArrayCopyForGate);
    }

    private void updateInputDataOfTaskSchedule(State state, Gate gate) {
        int halfRows = state.size() / 2;
        ComplexTensor gateData = dataProvider.getOperationData(gate);

        if (applyGateSchedule == null) {
            if (gateReal == null) {
                gateReal = new float[gateData.size()];
            }
            if (gateImag == null) {
                gateImag = new float[gateData.size()];
            }

            // @formatter:off
            applyGateSchedule = new TaskSchedule("applyGate")
                    .streamIn(targetQubit, state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData(), gateReal, gateImag)
                    .task("applyGateTask", FsvOperand::applyGate, targetQubit, state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData(), halfRows, gateReal, gateImag)
                    .streamOut(state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData());
            applyGateSchedule.lockObjectsInMemory(state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData());
            // @formatter:on
        }
        applyGateSchedule.updateReference(targetQubit, gate.targetQubit());
        applyGateSchedule.updateReference(gateReal, dataProvider.getOperationData(gate).getRawRealData());
        applyGateSchedule.updateReference(gateImag, dataProvider.getOperationData(gate).getRawImagData());
    }

    private void updateOutputDataOfGate(Gate gate) {
        applyGateSchedule.updateReference(gate.targetQubit(), targetQubit);
        applyGateSchedule.updateReference(dataProvider.getOperationData(gate).getRawRealData(), gateReal);
        applyGateSchedule.updateReference(dataProvider.getOperationData(gate).getRawImagData(), gateImag);
    }

    private void applyControlGate(State state, ControlGate controlGate) {
        startArrayCopyForControlGate = System.nanoTime();
        updateInputDataOfTaskSchedule(state, controlGate);
        endArrayCopyForControlGate = System.nanoTime();
        totalElapsedTimeOfArrayCopyForControlGate += (endArrayCopyForControlGate - startArrayCopyForControlGate);
        applyControlGateSchedule.execute();
        startArrayCopyForControlGate = System.nanoTime();
        updateOutputDataOfControlGate(controlGate);

        endArrayCopyForControlGate = System.nanoTime();
        totalElapsedTimeOfArrayCopyForControlGate += (endArrayCopyForControlGate - startArrayCopyForControlGate);
    }

    private void updateInputDataOfTaskSchedule(State state, ControlGate gate) {
        int halfRows = state.size() / 2;
        ComplexTensor gateData = dataProvider.getOperationData(gate);

        if (applyControlGateSchedule == null) {
            if (controlGateReal == null) {
                controlGateReal = new float[gateData.size()];
            }
            if (controlGateImag == null) {
                controlGateImag = new float[gateData.size()];
            }
            System.arraycopy(gateData.getRawRealData(), 0, gateReal, 0, gateData.getRawRealData().length);
            System.arraycopy(gateData.getRawImagData(), 0, gateImag, 0, gateData.getRawRealData().length);

            // @formatter:off
            applyControlGateSchedule = new TaskSchedule("applyControlGate")
                    .streamIn(targetQubit, controlQubit, state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData(), controlGateReal, controlGateImag)
                    .task("applyControlGateTask", FsvOperand::applyControlGate, targetQubit, controlQubit, state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData(), halfRows, controlGateReal, controlGateImag)
                    .streamOut(state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData());
            applyControlGateSchedule.lockObjectsInMemory(state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData());
            // @formatter:on
        }
        applyControlGateSchedule.updateReference(targetQubit, gate.targetQubit());
        applyControlGateSchedule.updateReference(controlQubit, gate.controlQubit());
        applyControlGateSchedule.updateReference(controlGateReal, dataProvider.getOperationData(gate).getRawRealData());
        applyControlGateSchedule.updateReference(controlGateImag, dataProvider.getOperationData(gate).getRawImagData());
    }

    private void updateOutputDataOfControlGate(ControlGate gate) {
        applyControlGateSchedule.updateReference(gate.targetQubit(), targetQubit);
        applyControlGateSchedule.updateReference(gate.controlQubit(), controlQubit);
        applyControlGateSchedule.updateReference(dataProvider.getOperationData(gate).getRawRealData(), controlGateReal);
        applyControlGateSchedule.updateReference(dataProvider.getOperationData(gate).getRawImagData(), controlGateImag);
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