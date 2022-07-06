package evaluation;

import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;
import uk.ac.manchester.tornado.qsim.simulator.fullstatevector.FsvSimulatorAccelerated;
import uk.ac.manchester.tornado.qsim.simulator.fullstatevector.FsvSimulatorStandard;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorAccelerated;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorStandard;

/**
 * Evaluate a simulation of Quantum Fourier Transform quantum algorithm.
 * 
 * @author Ales Kubicek
 */
public class QFT {

    /**
     * Run the evaluation using: "tornado evaluation.QFT".
     * 
     * @param args
     *            args[0] simulator type (1-4), args[1] - number of qubits in the
     *            quantum circuit.
     */
    public static void main(String[] args) {
        int noQubits = Common.getQubitCount(args);
        int simulatorType = Common.getSimulatorType(args);

        Circuit circuit = new Circuit(noQubits);
        initState(circuit);
        qftRotations(circuit);
        qftSwaps(circuit);

        Simulator simulator = new FsvSimulatorStandard();
        switch (simulatorType) {
            case 1:
                simulator = new UnitarySimulatorStandard();
                break;
            case 2:
                simulator = new UnitarySimulatorAccelerated(noQubits);
                break;
            case 3:
                simulator = new FsvSimulatorStandard();
                break;
            case 4:
                simulator = new FsvSimulatorAccelerated(noQubits);
                break;
        }

        Common.simulateAndPrint(simulator, circuit);
        switch (simulatorType) {
            case 1:
                System.out.println("-------------------------------------------------------------------------------");
                System.out.println("Invocations of matrix multiplication: " + ((UnitarySimulatorStandard) simulator).getNumOfInvocationsOfMultiplication() + ".");
                System.out.println("Execution time of matrix multiplication: " + ((UnitarySimulatorStandard) simulator).getTimeOfMultiplication() + "ns.");
                System.out.println("-------------------------------------------------------------------------------");
                System.out.println("Invocations of vector: " + ((UnitarySimulatorStandard) simulator).getNumOfInvocationsOfVector() + ".");
                System.out.println("Execution time of vector: " + ((UnitarySimulatorStandard) simulator).getTimeOfVector() + "ns.");
                System.out.println("-------------------------------------------------------------------------------");
                break;
            case 2:
                System.out.println("-------------------------------------------------------------------------------");
                System.out.println("Invocations of matrix multiplication: " + ((UnitarySimulatorAccelerated) simulator).getNumOfInvocationsOfMultiplication() + ".");
                System.out.println("Execution time of matrix multiplication: " + ((UnitarySimulatorAccelerated) simulator).getTimeOfMultiplication() + "ns.");
                System.out.println("-------------------------------------------------------------------------------");
                System.out.println("Invocations of vector: " + ((UnitarySimulatorAccelerated) simulator).getNumOfInvocationsOfVector() + ".");
                System.out.println("Execution time of vector: " + ((UnitarySimulatorAccelerated) simulator).getTimeOfVector() + "ns.");
                System.out.println("-------------------------------------------------------------------------------");
                break;
            case 3:
                System.out.println("-------------------------------------------------------------------------------");
                System.out.println("Invocations of gate: " + ((FsvSimulatorStandard) simulator).getNumOfInvocationsOfGate() + ".");
                System.out.println("Execution time of gate: " + ((FsvSimulatorStandard) simulator).getTimeOfGate() + "ns.");
                System.out.println("-------------------------------------------------------------------------------");
                System.out.println("Invocations of control gate: " + ((FsvSimulatorStandard) simulator).getNumOfInvocationsOfControlGate() + ".");
                System.out.println("Execution time of control gate: " + ((FsvSimulatorStandard) simulator).getTimeOfControlGate() + "ns.");
                System.out.println("-------------------------------------------------------------------------------");
                break;
            case 4:
                System.out.println("-------------------------------------------------------------------------------");
                System.out.println("Invocations of gate: " + ((FsvSimulatorAccelerated) simulator).getNumOfInvocationsOfGate() + ".");
                System.out.println("Execution time of gate: " + ((FsvSimulatorAccelerated) simulator).getTimeOfGate() + "ns.");
                System.out.println("-------------------------------------------------------------------------------");
                System.out.println("Invocations of control gate: " + ((FsvSimulatorAccelerated) simulator).getNumOfInvocationsOfControlGate() + ".");
                System.out.println("Execution time of control gate: " + ((FsvSimulatorAccelerated) simulator).getTimeOfControlGate() + "ns.");
                System.out.println("-------------------------------------------------------------------------------");
                break;
        }
    }

    private static void initState(Circuit circuit) {
        circuit.X(0, circuit.qubitCount() - 1);
    }

    private static void qftRotations(Circuit circuit) {
        for (int targetQubit = circuit.qubitCount() - 1; targetQubit >= 0; targetQubit--) {
            circuit.H(targetQubit);
            for (int controlQubit = 0; controlQubit < targetQubit; controlQubit++) {
                int k = targetQubit - controlQubit;
                circuit.CR(controlQubit, targetQubit, (float) (Math.PI / Math.pow(2, k)));
            }
        }
    }

    private static void qftSwaps(Circuit circuit) {
        for (int qubitA = 0; qubitA < circuit.qubitCount() / 2; qubitA++) {
            int qubitB = circuit.qubitCount() - qubitA - 1;
            circuit.CNOT(qubitA, qubitB);
            circuit.CNOT(qubitB, qubitA);
            circuit.CNOT(qubitA, qubitB);
        }
    }

}
