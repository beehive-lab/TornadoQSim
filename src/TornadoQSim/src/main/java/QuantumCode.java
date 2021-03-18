import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorAccelerated;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorStandard;

import java.util.Arrays;

/**
 * Example usecase of the TornadoQSim framework
 * Run using: tornado QuantumCode
 * @author Ales Kubicek
 */
public class QuantumCode {

    private static final int WARMING_UP_ITERATIONS = 10;
    private static final int TIMING_ITERATIONS = 30;

    public static void main(String[] args) {

        int noQubits = 7;

        // Example circuit - fully entangled
        Circuit circuit = new Circuit(noQubits);

        circuit.H(0);
        circuit.CNOT(0,6);
        circuit.CNOT(0,5);
        circuit.CNOT(0,4);
        circuit.CNOT(0,3);
        circuit.CNOT(0,2);
        circuit.CNOT(0,1);

        // Quantum simulator backends
        Simulator simAccelerated = new UnitarySimulatorAccelerated(noQubits);
        Simulator simStandard = new UnitarySimulatorStandard();

        // Evaluation
        System.out.println("--------- Accelerated --------");
        simulateAndPrint(simAccelerated, circuit);
        System.out.println();
        System.out.println("---------- Standard ----------");
        simulateAndPrint(simStandard, circuit);
    }

    /**
     * Run a simulation of the supplied circuit in two phases: warm-up and timing.
     * Statistics of the run will be printed out.
     * @param simulator quantum simulator backend.
     * @param circuit circuit to be simulated.
     */
    private static void simulateAndPrint(Simulator simulator, Circuit circuit) {
        for (int i = 0; i < WARMING_UP_ITERATIONS; i++)
            simulator.simulateFullState(circuit);

        long start, stop;
        long[] execTimes = new long[TIMING_ITERATIONS];

        for (int i = 0; i < TIMING_ITERATIONS; i++) {
            start = System.currentTimeMillis();
            simulator.simulateFullState(circuit);
            stop = System.currentTimeMillis();
            execTimes[i] = stop - start;
        }

        System.out.println("Simulation statistics:");
        System.out.println(Arrays.stream(execTimes).summaryStatistics());
    }
}
