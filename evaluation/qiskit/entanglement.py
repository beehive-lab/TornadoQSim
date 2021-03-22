import common
from qiskit import *

if __name__ == "__main__":
    no_qubits = common.get_qubit_count(sys.argv)

    circuit = QuantumCircuit(no_qubits)
    circuit.h(0)
    for target in range(no_qubits - 1, 0, -1):
        circuit.cnot(0, target)

    backend = Aer.get_backend("unitary_simulator")

    common.simulate_and_print(backend, circuit)
