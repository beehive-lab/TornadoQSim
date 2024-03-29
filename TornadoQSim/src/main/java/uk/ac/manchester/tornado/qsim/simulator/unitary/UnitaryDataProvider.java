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
package uk.ac.manchester.tornado.qsim.simulator.unitary;

import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;
import uk.ac.manchester.tornado.qsim.circuit.Step;
import uk.ac.manchester.tornado.qsim.circuit.operation.*;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;

import java.util.LinkedList;
import java.util.List;

/**
 * Provides operation data for unitary matrix simulator. This includes
 * construction of control gate / function unitary matrices.
 * 
 * @author Ales Kubicek
 */
class UnitaryDataProvider {
    final boolean accelerated;

    /**
     * Constructs unitary data provider.
     * 
     * @param accelerated
     *            flag to switch on / off acceleration (TornadoVM).
     */
    protected UnitaryDataProvider(boolean accelerated) {
        // Note: accelerated flag provided for evaluation purposes
        this.accelerated = accelerated;
    }

    /**
     * Gets unitary matrix for the supplied operation.
     * 
     * @param operation
     *            quantum operation.
     * @return unitary matrix representing the quantum operation.
     */
    protected ComplexTensor getOperationData(Operation operation) {
        switch (operation.operationType()) {
            case Gate:
                return constructGateData((Gate) operation);
            case ControlGate:
                return constructControlGateData((ControlGate) operation);
            case Function:
                return constructFunctionData((Function) operation);
            case CustomFunction:
                return constructCustomFunctionData((Function) operation);
            default:
                throw new UnsupportedOperationException("Operation type '" + operation.operationType() + "' is not supported in a unitary simulator.");
        }
    }

    /**
     * Gets list of all unitary matrices of the operations present in the supplied
     * quantum step.
     * 
     * @param noQubits
     *            number of qubits in the supplied step.
     * @param step
     *            single step in a quantum circuit.
     * @return list of all unitary matrices of the step operations.
     */
    protected List<ComplexTensor> getStepOperationData(int noQubits, Step step) {
        LinkedList<ComplexTensor> operationData = new LinkedList<>();

        int qubit = 0;
        while (qubit < noQubits) {
            if (step.isQubitFree(qubit)) {
                Gate identity = new Gate(GateType.I, qubit);
                operationData.add(OperationDataProvider.getInstance().getData(identity));
                qubit++;
            } else {
                Operation operation = step.getOperation(qubit);
                operationData.add(getOperationData(operation));
                qubit += operation.size();
            }
        }
        return operationData;
    }

    private ComplexTensor constructGateData(Gate gate) {
        return OperationDataProvider.getInstance().getData(gate);
    }

    private ComplexTensor constructControlGateData(ControlGate controlGate) {
        int finalSize = (int) Math.pow(2, controlGate.size());
        ComplexTensor controlGateData = new ComplexTensor(finalSize, finalSize);
        ComplexTensor gateData = OperationDataProvider.getInstance().getData(controlGate.gate());
        int control,target;
        if (controlGate.controlQubit()[0] > controlGate.targetQubit()[0]) {
            control = controlGate.size() - 1;
            target = 0;
        } else {
            control = 0;
            target = controlGate.size() - 1;
        }
        buildControlGate(gateData.getRawRealData(), gateData.getRawImagData(), control, target, controlGateData.getRawRealData(), controlGateData.getRawImagData(), finalSize);
        return controlGateData;
    }

    private ComplexTensor constructFunctionData(Function function) {
        // TODO: implement data generation for standard functions
        throw new UnsupportedOperationException("Standard functions are not yet supported.");
    }

    private ComplexTensor constructCustomFunctionData(Function function) {
        ComplexTensor functionData = OperationDataProvider.getInstance().getData(function.name());
        if (Math.pow(2, function.size()) != functionData.shape()[0])
            throw new IllegalArgumentException("Registered custom function data do not fit the function application.");
        return functionData;
    }

    private void buildControlGate(float[] gR, float[] gI, int c, int t, float[] rR, float[] rI, int rSize) {
        if (accelerated) {
            TaskGraph taskGraph = new TaskGraph("sControlGate")
                    .transferToHost(DataTransferMode.FIRST_EXECUTION, gR, gI)
                    .task("tBuildControlGate", UnitaryOperand::buildControlGate, gR, gI, c, t, rR, rI, rSize)
                    .transferToHost(DataTransferMode.EVERY_EXECUTION, rR, rI);
            TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(taskGraph.snapshot());
            executionPlan.execute();
        } else {
            UnitaryOperand.buildControlGate(gR, gI, c, t, rR, rI, rSize);
        }
    }
}
