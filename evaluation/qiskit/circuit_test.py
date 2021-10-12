import time as tm
import numpy as np
from qiskit import *
from math import pi


def get_circuit_a(no_qubits):
    circuit = QuantumCircuit(no_qubits)

    circuit.h(0)
    circuit.y(0)
    circuit.z(0)

    # for qubit in range(no_qubits):
    #     circuit.h(qubit)

    # circuit.y(0)
    # for qubit in range(1, no_qubits-1):
    #     circuit.z(qubit)
    # circuit.y(no_qubits-1)

    circuit.barrier()

    for target in range(circuit.num_qubits - 1, -1, -1):
        circuit.h(target)
        for control in range(target):
            k = target - control
            circuit.cp(pi / 2**k, control, target)

    for qubit_a in range(circuit.num_qubits // 2):
        qubit_b = circuit.num_qubits - qubit_a - 1
        circuit.barrier()
        circuit.cnot(qubit_a, qubit_b)
        circuit.cnot(qubit_b, qubit_a)
        circuit.cnot(qubit_a, qubit_b)
        circuit.barrier()

    return circuit


def simulate_and_print(backend, circuit):
    exec_times = np.empty(1, dtype="float")

    for iteration in range(1):
        start = tm.time()
        execute(circuit, backend)
        stop = tm.time()
        exec_times[iteration] = (stop - start) * 1000

    exec_max = int(np.max(exec_times))
    exec_avg = np.mean(exec_times)
    exec_min = int(np.min(exec_times))
    peak_memory = 0

    print("[{:d}, {:d}, {:.4f}, {:d}, {:d}], ".format(circuit.num_qubits, exec_max, exec_avg, exec_min, peak_memory))


if __name__ == "__main__":
    circuit = get_circuit_a(25)

    simulator = Aer.get_backend('statevector_simulator')
    simulate_and_print(simulator, circuit)
    # result = execute(circuit, simulator, shots=1).result()
    # statevector = result.get_statevector(circuit, decimals=3)
    # print(result.time_taken)

    # state = 0
    # for element in statevector:
    #     print("{:06b}  {:.2f}  ({:.3f}{:+.3f}i)".format(state, abs(element)*abs(element), element.real, element.imag))
    #     state += 1



