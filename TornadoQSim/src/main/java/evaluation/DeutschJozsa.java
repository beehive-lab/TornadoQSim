package evaluation;

import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;
import uk.ac.manchester.tornado.qsim.simulator.fullstatevector.FsvSimulatorAccelerated;
import uk.ac.manchester.tornado.qsim.simulator.fullstatevector.FsvSimulatorStandard;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorAccelerated;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorStandard;

/**
 * Evaluate a simulation of Deutsch-Jozsa quantum algorithm.
 * @author Ales Kubicek
 */
public class DeutschJozsa {
    private static boolean BALANCED = true;

    /**
     * Run the evaluation using: "tornado evaluation.DeutschJozsa".
     * @param args args[0] - number of qubits in the quantum circuit, args[1] - simulator type (1-4).
     */
    public static void main(String[] args) {
        int noQubits = Common.getQubitCount(args);
        int simulatorType = Common.getSimulatorType(args);

        Circuit circuit = new Circuit(noQubits);
        hadamardFunctionQubits(circuit);
        prepareOutputQubit(circuit);

        if (BALANCED)
            balancedOracle(circuit);
        else
            constantOracle(circuit);

        hadamardFunctionQubits(circuit);

        Simulator simulator = new FsvSimulatorStandard();
        switch (simulatorType) {
            case 1: simulator = new UnitarySimulatorStandard(); break;
            case 2: simulator = new UnitarySimulatorAccelerated(noQubits); break;
            case 4: simulator = new FsvSimulatorAccelerated(noQubits); break;
        }

        Common.simulateAndPrint(simulator, circuit);
    }

    private static void hadamardFunctionQubits(Circuit circuit) {
        for (int qubit = 0; qubit < circuit.qubitCount() - 1; qubit++)
            circuit.H(qubit);
    }

    private static void prepareOutputQubit(Circuit circuit) {
        int outputQubit = circuit.qubitCount() - 1;
        circuit.X(outputQubit);
        circuit.H(outputQubit);
    }

    private static void constantOracle(Circuit circuit) {
        circuit.X(0);
    }

    private static void balancedOracle(Circuit circuit) {
        int target = circuit.qubitCount() - 1;
        for (int control = 0; control < target; control++)
            circuit.CNOT(control, target);
    }

}
