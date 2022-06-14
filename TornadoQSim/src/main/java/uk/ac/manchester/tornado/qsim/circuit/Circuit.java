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
package uk.ac.manchester.tornado.qsim.circuit;

import uk.ac.manchester.tornado.qsim.circuit.operation.*;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.FunctionType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.InstructionType;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Represents quantum circuit composed from steps that hold operations (gate,
 * control gate, instruction) on qubits. Such circuit can be then evaluated
 * using one of the simulators.
 * 
 * @author Ales Kubicek
 */
public class Circuit {
    private final int noQubits;
    private final LinkedList<Step> steps;

    /**
     * Constructs an empty quantum circuit with the number of available qubits as
     * supplied by the parameter.
     * 
     * @param noQubits
     *            number of qubits.
     */
    public Circuit(int noQubits) {
        if (noQubits < 1)
            throw new IllegalArgumentException("Number of qubits in a circuit must be greater than 0.");
        this.noQubits = noQubits;
        steps = new LinkedList<>();
        steps.add(new Step(noQubits));
    }

    /**
     * Gets the number of qubits in this quantum circuit.
     * 
     * @return number of qubits.
     */
    public int qubitCount() {
        return noQubits;
    }

    /**
     * Gets the depth of this quantum cirucit (number of steps).
     * 
     * @return depth of this circuit.
     */
    public int depth() {
        return steps.size();
    }

    /**
     * Gets all steps of this quantum circuit.
     * 
     * @return all steps of this circuit.
     */
    public List<Step> getSteps() {
        return steps;
    }

    /**
     * Appends all the steps of the supplied quantum circuit to the end of this
     * quantum circuit
     * 
     * @param circuit
     *            quantum circuit to be appended.
     */
    public void appendCircuit(Circuit circuit) {
        if (circuit == null)
            throw new IllegalArgumentException("Invalid circuit supplied (NULL).");
        if (circuit.qubitCount() != noQubits)
            throw new IllegalArgumentException("Incompatible circuits (qubit count).");
        steps.addAll(circuit.getSteps());
    }

    /**
     * Applies X gate to the supplied qubit/s
     * 
     * @param qubits
     *            qubits to which this gate will be applied to.
     */
    public void X(int... qubits) {
        addGate(GateType.X, qubits);
    }

    /**
     * Applies Y gate to the supplied qubit/s
     * 
     * @param qubits
     *            qubits to which this gate will be applied to.
     */
    public void Y(int... qubits) {
        addGate(GateType.Y, qubits);
    }

    /**
     * Applies Z gate to the supplied qubit/s
     * 
     * @param qubits
     *            qubits to which this gate will be applied to.
     */
    public void Z(int... qubits) {
        addGate(GateType.Z, qubits);
    }

    /**
     * Applies H gate to the supplied qubit/s
     * 
     * @param qubits
     *            qubits to which this gate will be applied to.
     */
    public void H(int... qubits) {
        addGate(GateType.H, qubits);
    }

    /**
     * Applies S gate to the supplied qubit/s
     * 
     * @param qubits
     *            qubits to which this gate will be applied to.
     */
    public void S(int... qubits) {
        addGate(GateType.S, qubits);
    }

    /**
     * Applies T gate to the supplied qubit/s
     * 
     * @param qubits
     *            qubits to which this gate will be applied to.
     */
    public void T(int... qubits) {
        addGate(GateType.T, qubits);
    }

    /**
     * Applies phase shift gate R to the supplied qubit/s
     * 
     * @param phi
     *            phase shift in radians.
     * @param qubits
     *            qubits to which this gate will be applied to.
     */
    public void R(float phi, int... qubits) {
        addGate(GateType.R, phi, qubits);
    }

    /**
     * Applies controlled X gate to the supplied control and target qubits.
     * 
     * @param controlQubit
     *            control qubit for this controlled gate.
     * @param targetQubit
     *            target qubit for this controlled gate.
     */
    public void CNOT(int controlQubit, int targetQubit) {
        Gate targetGate = new Gate(GateType.X, targetQubit);
        addControlGate(targetGate, controlQubit, targetQubit);
    }

    /**
     * Applies controlled X gate to the supplied control and target qubits.
     * 
     * @param controlQubit
     *            control qubit for this controlled gate.
     * @param targetQubit
     *            target qubit for this controlled gate.
     */
    public void CX(int controlQubit, int targetQubit) {
        Gate targetGate = new Gate(GateType.X, targetQubit);
        addControlGate(targetGate, controlQubit, targetQubit);
    }

    /**
     * Applies controlled Y gate to the supplied control and target qubits.
     * 
     * @param controlQubit
     *            control qubit for this controlled gate.
     * @param targetQubit
     *            target qubit for this controlled gate.
     */
    public void CY(int controlQubit, int targetQubit) {
        Gate targetGate = new Gate(GateType.Y, targetQubit);
        addControlGate(targetGate, controlQubit, targetQubit);
    }

    /**
     * Applies controlled Z gate to the supplied control and target qubits.
     * 
     * @param controlQubit
     *            control qubit for this controlled gate.
     * @param targetQubit
     *            target qubit for this controlled gate.
     */
    public void CZ(int controlQubit, int targetQubit) {
        Gate targetGate = new Gate(GateType.Z, targetQubit);
        addControlGate(targetGate, controlQubit, targetQubit);
    }

    /**
     * Applies controlled H gate to the supplied control and target qubits.
     * 
     * @param controlQubit
     *            control qubit for this controlled gate.
     * @param targetQubit
     *            target qubit for this controlled gate.
     */
    public void CH(int controlQubit, int targetQubit) {
        Gate targetGate = new Gate(GateType.H, targetQubit);
        addControlGate(targetGate, controlQubit, targetQubit);
    }

    /**
     * Applies controlled S gate to the supplied control and target qubits.
     * 
     * @param controlQubit
     *            control qubit for this controlled gate.
     * @param targetQubit
     *            target qubit for this controlled gate.
     */
    public void CS(int controlQubit, int targetQubit) {
        Gate targetGate = new Gate(GateType.S, targetQubit);
        addControlGate(targetGate, controlQubit, targetQubit);
    }

    /**
     * Applies controlled T gate to the supplied control and target qubits.
     * 
     * @param controlQubit
     *            control qubit for this controlled gate.
     * @param targetQubit
     *            target qubit for this controlled gate.
     */
    public void CT(int controlQubit, int targetQubit) {
        Gate targetGate = new Gate(GateType.T, targetQubit);
        addControlGate(targetGate, controlQubit, targetQubit);
    }

    /**
     * Applies controlled phase shift gate R to the supplied control and target
     * qubits.
     * 
     * @param controlQubit
     *            control qubit for this controlled gate.
     * @param targetQubit
     *            target qubit for this controlled gate.
     * @param phi
     *            phase shift in radians.
     */
    public void CR(int controlQubit, int targetQubit, float phi) {
        Gate targetGate = new Gate(GateType.R, targetQubit, phi);
        addControlGate(targetGate, controlQubit, targetQubit);
    }

    /**
     * Applies swap function to the supplied qubits.
     * 
     * @param qubitA
     *            qubit to be swapped.
     * @param qubitB
     *            qubit to be swapped.
     */
    public void swap(int qubitA, int qubitB) {
        addFunction(FunctionType.Swap, qubitA, qubitB);
    }

    /**
     * Applies custom function to the supplied range of qubits. This function must
     * first be registered with the operation data provider.
     * 
     * @param name
     *            name of the custom quantum function.
     * @param fromQubit
     *            from this qubit.
     * @param toQubit
     *            to this qubit.
     */
    public void customFunction(String name, int fromQubit, int toQubit) {
        if (!areQubitsValid(fromQubit, toQubit))
            throw new IllegalArgumentException("Invalid qubit / qubits / qubit range supplied.");
        if (!OperationDataProvider.getInstance().isFunctionDataRegistered(name))
            throw new IllegalArgumentException("Function is not registered with operation data provider.");
        addOperation(new Function(name, fromQubit, toQubit));
    }

    /**
     * Applies measure instruction to the supplied qubit/s
     * 
     * @param qubits
     *            qubits to which this instruction will be applied to.
     */
    public void measure(int... qubits) {
        addInstruction(InstructionType.Measure, qubits);
    }

    /**
     * Applies reset instruction to the supplied qubit/s
     * 
     * @param qubits
     *            qubits to which this instruction will be applied to.
     */
    public void reset(int... qubits) {
        addInstruction(InstructionType.Reset, qubits);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Circuit circuit = (Circuit) o;
        return noQubits == circuit.noQubits && steps.equals(circuit.steps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noQubits, steps);
    }

    private void addGate(GateType type, int... qubits) {
        if (!areQubitsValid(qubits))
            throw new IllegalArgumentException("Invalid qubit / qubits supplied.");
        for (int qubit : qubits)
            addOperation(new Gate(type, qubit));
    }

    private void addGate(GateType type, float phi, int... qubits) {
        if (!areQubitsValid(qubits))
            throw new IllegalArgumentException("Invalid qubit / qubits supplied.");
        for (int qubit : qubits)
            addOperation(new Gate(type, qubit, phi));
    }

    private void addControlGate(Gate gate, int controlQubit, int targetQubit) {
        if (!areQubitsValid(controlQubit, targetQubit))
            throw new IllegalArgumentException("Invalid qubits supplied.");
        addOperation(new ControlGate(gate, controlQubit, targetQubit));
    }

    private void addFunction(FunctionType type, int fromQubit, int toQubit) {
        if (!areQubitsValid(fromQubit, toQubit))
            throw new IllegalArgumentException("Invalid qubit range supplied.");
        addOperation(new Function(type, fromQubit, toQubit));
    }

    private void addInstruction(InstructionType type, int... qubits) {
        if (!areQubitsValid(qubits))
            throw new IllegalArgumentException("Invalid qubit / qubits supplied.");
        for (int qubit : qubits)
            addOperation(new Instruction(type, qubit));
    }

    private void addOperation(Operation operation) {
        if (!steps.peekLast().canAddOperation(operation))
            steps.add(new Step(noQubits));
        steps.peekLast().addOperation(operation);
    }

    private boolean areQubitsValid(int... qubits) {
        for (int qubit : qubits)
            if (qubit < 0 || qubit > noQubits - 1)
                return false;
        return true;
    }
}
