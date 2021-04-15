package evaluation;

import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorAccelerated;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorStandard;

/**
 * Evaluate a simulation of fully entangled quantum circuit.
 * @author Ales Kubicek
 */
public class Entanglement {

    /**
     * Run the evaluation using: "tornado evaluation.Entanglement".
     * @param args args[0] - number of qubits in the quantum circuit, args[1] - "accelerate" or empty.
     */
    public static void main(String[] args) {
        int noQubits = Common.getQubitCount(args);
        boolean accelerate = Common.getAccelerationFlag(args);

        Circuit circuit = new Circuit(noQubits);
        circuit.H(0);
        for (int target = noQubits - 1; target > 0; target--)
            circuit.CNOT(0, target);

        Simulator simulator = accelerate ?
                new UnitarySimulatorAccelerated(noQubits) :
                new UnitarySimulatorStandard();

        Common.simulateAndPrint(simulator, circuit);
    }

}
