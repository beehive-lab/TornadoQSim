import sys
import time as tm
import numpy as np
import resource as rs
import cirq
from cirq import Simulator

WARMUP_ITERATIONS = 0;
TIMING_ITERATIONS = 1;


def simulate_and_print(noQubits, simulator, circuit):
    for _ in range(WARMUP_ITERATIONS):
        simulator.simulate(circuit)

    exec_times = np.empty(TIMING_ITERATIONS, dtype="float")

    for iteration in range(TIMING_ITERATIONS):
        start = tm.time()
        simulator.simulate(circuit)
        stop = tm.time()
        exec_times[iteration] = (stop - start) * 1000

    max = int(np.max(exec_times))
    avg = np.mean(exec_times)
    min = int(np.min(exec_times))
    peak_memory = rs.getrusage(rs.RUSAGE_SELF).ru_maxrss * 1024

    print("[{:d}, {:d}, {:.4f}, {:d}, {:d}], ".format(noQubits, max, avg, min, peak_memory))


if __name__ == "__main__":
    noQubits = 8
    try:
        noQubits = int(sys.argv[1])
    except Exception:
        pass

    qubits = [cirq.GridQubit(x, y) for x in range(noQubits) for y in range(noQubits)]
    circuit = cirq.Circuit()

    circuit.append(cirq.H(qubits[0]))

    for target in range(noQubits - 1, 0, -1):
        circuit.append(cirq.CNOT(qubits[0], qubits[target]))

    for qubit in range(noQubits):
        circuit.append(cirq.measure(qubits[qubit]))

    simulator = Simulator()
    simulate_and_print(noQubits, simulator, circuit)
