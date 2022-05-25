package evaluation;

import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.circuit.State;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;
import uk.ac.manchester.tornado.qsim.simulator.fullstatevector.FsvSimulatorAccelerated;
import uk.ac.manchester.tornado.qsim.simulator.fullstatevector.FsvSimulatorStandard;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorAccelerated;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorStandard;

import java.util.Arrays;

/**
 * Example usecase of the TornadoQSim framework
 * 
 * Run using: tornado evaluation.QuantumCode
 * 
 * @author Ales Kubicek
 */
public class QuantumCode {

    private static final int WARMING_UP_ITERATIONS = 0;
    private static final int TIMING_ITERATIONS = 1;

    public static void main(String[] args) {
        int simulatorVersion = Integer.parseInt(args[0]);
        int noQubits = Integer.parseInt(args[1]);

        Circuit circuit = GetCircuitA(noQubits);

        // Quantum simulator backends
        Simulator simulator;
        switch (simulatorVersion) {
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
            default:
                throw new UnsupportedOperationException("Simulator type not supported.");
        }

        // Evaluation
        simulateAndPrint(simulator, circuit);
    }

    private static Circuit GetCircuitA(int noQubits) {
        Circuit circuit = new Circuit(noQubits);

        circuit.H(0, 1, 2);

        circuit.CNOT(0, 2);

        circuit.H(0);
        circuit.Y(0);
        circuit.Z(0);

        // for (int qubit = 0; qubit < noQubits; qubit++)
        // circuit.H(qubit);
        //
        // circuit.Y(0);
        // for (int qubit = 1; qubit < noQubits-1; qubit++)
        // circuit.Z(qubit);
        // circuit.Y(noQubits-1);

        // for (int targetQubit = circuit.qubitCount() - 1; targetQubit >= 0;
        // targetQubit--) {
        // circuit.H(targetQubit);
        // for (int controlQubit = 0; controlQubit < targetQubit; controlQubit++) {
        // int k = targetQubit - controlQubit;
        // circuit.CR(controlQubit, targetQubit, (float)(Math.PI / Math.pow(2, k)));
        // }
        // }

        // for (int qubitA = 0; qubitA < circuit.qubitCount() / 2; qubitA++) {
        // int qubitB = circuit.qubitCount() - qubitA - 1;
        // circuit.CNOT(qubitA, qubitB);
        // circuit.CNOT(qubitB, qubitA);
        // circuit.CNOT(qubitA, qubitB);
        // }

        return circuit;
    }

    /**
     * Run a simulation of the supplied circuit in two phases: warm-up and timing.
     * Statistics of the run will be printed out.
     * 
     * @param simulator
     *            quantum simulator backend.
     * @param circuit
     *            circuit to be simulated.
     */
    private static void simulateAndPrint(Simulator simulator, Circuit circuit) {
        for (int i = 0; i < WARMING_UP_ITERATIONS; i++)
            simulator.simulateFullState(circuit);

        long start,stop;
        long[] execTimes = new long[TIMING_ITERATIONS];
        State state = null;
        for (int i = 0; i < TIMING_ITERATIONS; i++) {
            start = System.currentTimeMillis();
            state = simulator.simulateFullState(circuit);
            stop = System.currentTimeMillis();
            execTimes[i] = stop - start;
        }
        System.out.println("State vector:");
        System.out.println(simulator.simulateFullState(circuit));
        System.out.println("Simulation statistics:");
        System.out.println(Arrays.stream(execTimes).summaryStatistics());
    }
}
