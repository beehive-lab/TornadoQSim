package evaluation;

import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorAccelerated;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorStandard;

/**
 * Evaluate a simulation of Quantum Fourier Transform quantum algorithm.
 * @author Ales Kubicek
 */
public class QFT {

    /**
     * Run the evaluation using: "tornado evaluation.QFT".
     * @param args args[0] - number of qubits in the quantum circuit, args[1] - "accelerate" or empty.
     */
    public static void main(String[] args) {
        int noQubits = Common.getQubitCount(args);
        boolean accelerate = Common.getAccelerationFlag(args);

        Circuit circuit = new Circuit(noQubits);
        initState(circuit);
        qftRotations(circuit);
        qftSwaps(circuit);

        Simulator simulator = accelerate ?
                new UnitarySimulatorAccelerated(noQubits) :
                new UnitarySimulatorStandard();

        Common.simulateAndPrint(simulator, circuit);
    }

    private static void initState(Circuit circuit) {
        circuit.X(0, circuit.qubitCount() - 1);
    }

    private static void qftRotations(Circuit circuit) {
        for (int targetQubit = circuit.qubitCount() - 1; targetQubit >= 0; targetQubit--) {
            circuit.H(targetQubit);
            for (int controlQubit = 0; controlQubit < targetQubit; controlQubit++) {
                int k = targetQubit - controlQubit;
                circuit.CR(controlQubit, targetQubit, (float)(Math.PI / Math.pow(2, k)));
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
