import time as tm
import numpy as np
import resource as rs
from qiskit import *

WARMUP_ITERATIONS = 0;
TIMING_ITERATIONS = 1;


def get_qubit_count(argv):
    no_qubits = 8
    try:
        no_qubits = int(argv[1])
    except Exception:
        pass
    return no_qubits


def simulate_and_print(backend, circuit):
    for _ in range(WARMUP_ITERATIONS):
        execute(circuit, backend)

    exec_times = np.empty(TIMING_ITERATIONS, dtype="float")

    for iteration in range(TIMING_ITERATIONS):
        start = tm.time()
        execute(circuit, backend)
        stop = tm.time()
        exec_times[iteration] = (stop - start) * 1000

    exec_max = int(np.max(exec_times))
    exec_avg = np.mean(exec_times)
    exec_min = int(np.min(exec_times))
    peak_memory = rs.getrusage(rs.RUSAGE_SELF).ru_maxrss * 1024

    print("[{:d}, {:d}, {:.4f}, {:d}, {:d}], ".format(circuit.num_qubits, exec_max, exec_avg, exec_min, peak_memory))
