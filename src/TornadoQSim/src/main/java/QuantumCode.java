import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorAccelerated;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorStandard;

import java.util.Arrays;

public class QuantumCode {

    private static final int WARMING_UP_ITERATIONS = 0;
    private static final int TIMING_ITERATIONS = 1;

    public static void main(String[] args) {
        // Replace with UnitarySimulatorStandard() for non-accelerated simulation
        Simulator simulator = new UnitarySimulatorAccelerated();

        // Some random quantum circuit - feel free to:
        // --> adjust the circuit size (number of qubits)
        // --> adjust the quantum operations
        Circuit circuit = new Circuit(5);

        circuit.H(0,1,2,3,4);
        circuit.CNOT(0,4);
        circuit.CNOT(4,0);
        circuit.X(0);
        circuit.Y(1);
        circuit.Z(2);
        circuit.H(3);
        circuit.S(4);
        circuit.CS(3,4);
        circuit.H(0,1,2,3,4);
        circuit.X(0,1,2,3,4);

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

        System.out.println(Arrays.stream(execTimes).summaryStatistics());
    }

}
