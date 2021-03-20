import time as tm
import numpy as np
import sys
import resource as rs
from qiskit import *

WARMUP_ITERATIONS = 2;
TIMING_ITERATIONS = 3;


def simulate_and_print(noQubits, backend, circuit):
    for _ in range(WARMUP_ITERATIONS):
        execute(circuit, backend)

    exec_times = np.empty(TIMING_ITERATIONS, dtype="float")

    for iteration in range(TIMING_ITERATIONS):
        start = tm.time()
        execute(circuit, backend)
        stop = tm.time()
        exec_times[iteration] = (stop - start) * 1000

    max = int(np.max(exec_times))
    avg = np.mean(exec_times)
    min = int(np.min(exec_times))
    peak_memory = rs.getrusage(rs.RUSAGE_SELF).ru_maxrss

    print("[{:d}, {:d}, {:.4f}, {:d}, {:d}]".format(noQubits, max, avg, min, peak_memory))


if __name__ == "__main__":
    noQubits = 8
    try:
        noQubits = int(sys.argv[1])
        print(string_int)
    except Exception:
        pass

    circuit = QuantumCircuit(noQubits)
    circuit.h(0)
    for target in range(noQubits - 1, 0):
        circuit.cnot(0, target)

    backend = Aer.get_backend("unitary_simulator")

    simulate_and_print(noQubits, backend, circuit)
