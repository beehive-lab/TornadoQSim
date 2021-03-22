import common
from qiskit import *
from math import pi


def init_state(circuit):
    circuit.x(0)
    circuit.x(circuit.num_qubits - 1)


def qft_rotations(circuit):
    for target in range(circuit.num_qubits - 1, 0, -1):
        circuit.h(target)
        for control in range(target):
            k = target - control
            circuit.cp(pi / 2**k, control, target)


def qft_swaps(circuit):
    for qubit_a in range(circuit.num_qubits // 2):
        qubit_b = circuit.num_qubits - qubit_a - 1
        circuit.swap(qubit_a, qubit_b)


if __name__ == "__main__":
    no_qubits = common.get_qubit_count(sys.argv)

    circuit = QuantumCircuit(no_qubits)
    init_state(circuit)
    qft_rotations(circuit)
    qft_swaps(circuit)

    backend = Aer.get_backend("unitary_simulator")

    common.simulate_and_print(backend, circuit)
