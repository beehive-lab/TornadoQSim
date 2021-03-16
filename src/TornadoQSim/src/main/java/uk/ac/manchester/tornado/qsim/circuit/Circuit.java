package uk.ac.manchester.tornado.qsim.circuit;

import uk.ac.manchester.tornado.qsim.circuit.operation.*;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.FunctionType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.InstructionType;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Represents quantum circuit composed from steps that hold operations (gate, control gate, instruction) on qubits.
 * Such circuit can be then evaluated using one of the simulators.
 * @author Ales Kubicek
 */
public class Circuit {
    private final int noQubits;
    private final LinkedList<Step> steps;

    /**
     * Constructs an empty quantum circuit with the number of available qubits as supplied by the parameter.
     * @param noQubits number of qubits.
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
     * @return number of qubits.
     */
    public int qubitCount() { return noQubits; }

    /**
     * Gets the depth of this quantum cirucit (number of steps).
     * @return depth of this circuit.
     */
    public int depth() { return steps.size(); }

    /**
     * Gets all steps of this quantum circuit.
     * @return all steps of this circuit.
     */
    public List<Step> getSteps() { return steps; }

    /**
     * Appends all the steps of the supplied quantum circuit to the end of this quantum circuit
     * @param circuit quantum circuit to be appended.
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
     * @param qubits qubits to which this gate will be applied to.
     */
    public void X(int... qubits) { addGate(GateType.X, qubits); }

    /**
     * Applies Y gate to the supplied qubit/s
     * @param qubits qubits to which this gate will be applied to.
     */
    public void Y(int... qubits) { addGate(GateType.Y, qubits); }

    /**
     * Applies Z gate to the supplied qubit/s
     * @param qubits qubits to which this gate will be applied to.
     */
    public void Z(int... qubits) { addGate(GateType.Z, qubits); }

    /**
     * Applies H gate to the supplied qubit/s
     * @param qubits qubits to which this gate will be applied to.
     */
    public void H(int... qubits) { addGate(GateType.H, qubits); }

    /**
     * Applies S gate to the supplied qubit/s
     * @param qubits qubits to which this gate will be applied to.
     */
    public void S(int... qubits) { addGate(GateType.S, qubits); }

    /**
     * Applies T gate to the supplied qubit/s
     * @param qubits qubits to which this gate will be applied to.
     */
    public void T(int... qubits) { addGate(GateType.T, qubits); }


    /**
     * Applies controlled X gate to the supplied control and target qubits.
     * @param controlQubit control qubit for this controlled gate.
     * @param targetQubit target qubit for this controlled gate.
     */
    public void CNOT(int controlQubit, int targetQubit) { addControlGate(GateType.X, controlQubit, targetQubit); }

    /**
     * Applies controlled X gate to the supplied control and target qubits.
     * @param controlQubit control qubit for this controlled gate.
     * @param targetQubit target qubit for this controlled gate.
     */
    public void CX(int controlQubit, int targetQubit) { addControlGate(GateType.X, controlQubit, targetQubit); }

    /**
     * Applies controlled Y gate to the supplied control and target qubits.
     * @param controlQubit control qubit for this controlled gate.
     * @param targetQubit target qubit for this controlled gate.
     */
    public void CY(int controlQubit, int targetQubit) { addControlGate(GateType.Y, controlQubit, targetQubit); }

    /**
     * Applies controlled Z gate to the supplied control and target qubits.
     * @param controlQubit control qubit for this controlled gate.
     * @param targetQubit target qubit for this controlled gate.
     */
    public void CZ(int controlQubit, int targetQubit) { addControlGate(GateType.Z, controlQubit, targetQubit); }

    /**
     * Applies controlled H gate to the supplied control and target qubits.
     * @param controlQubit control qubit for this controlled gate.
     * @param targetQubit target qubit for this controlled gate.
     */
    public void CH(int controlQubit, int targetQubit) { addControlGate(GateType.H, controlQubit, targetQubit); }

    /**
     * Applies controlled S gate to the supplied control and target qubits.
     * @param controlQubit control qubit for this controlled gate.
     * @param targetQubit target qubit for this controlled gate.
     */
    public void CS(int controlQubit, int targetQubit) { addControlGate(GateType.S, controlQubit, targetQubit); }

    /**
     * Applies controlled T gate to the supplied control and target qubits.
     * @param controlQubit control qubit for this controlled gate.
     * @param targetQubit target qubit for this controlled gate.
     */
    public void CT(int controlQubit, int targetQubit) { addControlGate(GateType.T, controlQubit, targetQubit); }


    /**
     * Applies swap function to the supplied adjacent qubits.
     * @param qubitA qubit to be swapped.
     * @param qubitB qubit to be swapped.
     */
    public void swap(int qubitA, int qubitB) {
        if (!areQubitsAdjacent(qubitA, qubitB))
            throw new IllegalArgumentException("Non-adjancent qubits supplied for swap operation.");
        addFunction(FunctionType.Swap, qubitA, qubitB);
    }

    /**
     * Applies custom function to the supplied range of qubits.
     * This function must first be registered with the operation data provider.
     * @param name name of the custom quantum function.
     * @param fromQubit from this qubit.
     * @param toQubit to this qubit.
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
     * @param qubits qubits to which this instruction will be applied to.
     */
    public void measure(int... qubits) { addInstruction(InstructionType.Measure, qubits); }

    /**
     * Applies reset instruction to the supplied qubit/s
     * @param qubits qubits to which this instruction will be applied to.
     */
    public void reset(int... qubits) { addInstruction(InstructionType.Reset, qubits); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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

    private void addControlGate(GateType type, int controlQubit, int targetQubit) {
        if (!areQubitsValid(controlQubit, targetQubit))
            throw new IllegalArgumentException("Invalid qubits supplied.");
        addOperation(new ControlGate(type, controlQubit, targetQubit));
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

    private boolean areQubitsAdjacent(int qubitA, int qubitB) {
        return qubitA + 1 == qubitB || qubitA - 1 == qubitB;
    }
}
