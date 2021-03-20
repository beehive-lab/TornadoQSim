import org.redfx.strange.Program;
import org.redfx.strange.QuantumExecutionEnvironment;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.LongSummaryStatistics;

public class Common {
    private static final int WARMUP_ITERATIONS = 0;
    private static final int TIMING_ITERATIONS = 1;

    protected static int getQubitCount(String[] args) {
        int noQubits = 8;
        if (args.length >= 1) {
            try { noQubits = Integer.parseInt(args[0]); }
            catch (NumberFormatException ignored) { }
        }
        return noQubits;
    }

    protected static void simulateAndPrint(QuantumExecutionEnvironment environment, Program program) {
        for (int i = 0; i < WARMUP_ITERATIONS; i++)
            environment.runProgram(program);

        long start, stop;
        long[] execTimes = new long[TIMING_ITERATIONS];

        for (int i = 0; i < TIMING_ITERATIONS; i++) {
            start = System.currentTimeMillis();
            environment.runProgram(program);
            stop = System.currentTimeMillis();
            execTimes[i] = stop - start;
        }

        LongSummaryStatistics stats = Arrays.stream(execTimes).summaryStatistics();
        long peakMemory = measurePeakMemory();
        System.out.printf("[%d, %d, %.4f, %d, %d], \n",
                program.getNumberQubits(), stats.getMax(), stats.getAverage(), stats.getMin(), peakMemory);
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
