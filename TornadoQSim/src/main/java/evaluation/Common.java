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
package evaluation;

import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.circuit.State;
import uk.ac.manchester.tornado.qsim.circuit.utils.StateConverter;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.LongSummaryStatistics;

/**
 * Common class to provide utils, such as: Argument parsing, simulation timing,
 * memory measurement.
 * 
 * @author Ales Kubicek
 */
public class Common {
    private static final int WARMUP_ITERATIONS = 20;
    private static final int TIMING_ITERATIONS = 9;

    /**
     * Parses number of qubits from supplied program arguments.
     * 
     * @param args
     *            full program arguments.
     * @return number of qubits (default = 8).
     */
    protected static int getQubitCount(String[] args) {
        int noQubits = 8;
        if (args.length >= 1) {
            try {
                noQubits = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
            }
        }
        return noQubits;
    }

    /**
     * Parses simulator type from supplied program arguments.
     * 
     * @param args
     *            full program arguments.
     * @return simulator type.
     */
    protected static int getSimulatorType(String[] args) {
        int simulatorType = 3;
        if (args.length >= 2) {
            try {
                simulatorType = Integer.parseInt(args[0]);
                if (simulatorType < 1 || simulatorType > 4)
                    throw new NumberFormatException();
            } catch (NumberFormatException ignored) {
                System.out.println("Invalid simulator type - circuit will be simulated with default fsv simulator.");
            }
        }
        return simulatorType;
    }

    /**
     * Simulate supplied quantum circuit on supplied quantum simulator. Including
     * timing and peak memory measurement.
     * 
     * @param simulator
     *            quantum simulator.
     * @param circuit
     *            quantum circuit to be simulated.
     */
    protected static void simulateAndPrint(Simulator simulator, Circuit circuit) {
        for (int i = 0; i < WARMUP_ITERATIONS; i++)
            simulator.simulateFullState(circuit);

        long start,stop;
        long[] execTimes = new long[TIMING_ITERATIONS];

        for (int i = 0; i < TIMING_ITERATIONS; i++) {
            start = System.currentTimeMillis();
            simulator.simulateFullState(circuit);
            stop = System.currentTimeMillis();
            execTimes[i] = stop - start;
        }

        LongSummaryStatistics stats = Arrays.stream(execTimes).summaryStatistics();
        long peakMemory = measurePeakMemory();
        System.out.printf("[%d, %d, %.4f, %d, %d], \n", circuit.qubitCount(), stats.getMax(), stats.getAverage(), stats.getMin(), peakMemory);
    }

    private static long measurePeakMemory() {
        long maxMemoryUsed = 0;
        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean pool : pools) {
            long memoryUsed = pool.getPeakUsage().getUsed();
            if (memoryUsed > maxMemoryUsed)
                maxMemoryUsed = memoryUsed;
        }
        return maxMemoryUsed;
    }
}
