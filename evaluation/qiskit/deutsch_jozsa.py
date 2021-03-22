import common
from qiskit import *

BALANCED = True


def hadamard_function_qubits(circuit):
    for qubit in range(circuit.num_qubits - 1):
        circuit.h(qubit)


def prepare_output_qubit(circuit):
    output_qubit = circuit.num_qubits - 1
    circuit.x(output_qubit)
    circuit.h(output_qubit)


def constant_oracle(circuit):
    circuit.x(0)


def balanced_oracle(circuit):
    target = circuit.num_qubits - 1
    for control in range(target):
        circuit.cnot(control, target)


if __name__ == "__main__":
    no_qubits = common.get_qubit_count(sys.argv)

    circuit = QuantumCircuit(no_qubits)
    hadamard_function_qubits(circuit)
    prepare_output_qubit(circuit)

    if BALANCED:
        balanced_oracle(circuit)
    else:
        constant_oracle(circuit)

    hadamard_function_qubits(circuit)

    backend = Aer.get_backend("unitary_simulator")

    common.simulate_and_print(backend, circuit)
