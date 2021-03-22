import numpy as np
import matplotlib.pyplot as plt
import data

qubits = np.arange(1, 30)
data = data.get_experiment_data("entanglement")

x = data[0][:, 0]
y = data[0][:, 2]
e_max = data[0][:, 1] - y
e_min = y - data[0][:, 3]

plt.errorbar(x, y, yerr=[e_min, e_max], label="Qiskit", marker="o", linestyle=":", linewidth=1.0, capsize=2, color="#848484")

x = data[1][:, 0]
y = data[1][:, 2]
e_max = data[1][:, 1] - y
e_min = y - data[1][:, 3]

plt.errorbar(x, y, yerr=[e_min, e_max], label="Strange", marker="o", linestyle=":", linewidth=1.0, capsize=2, color="#545454")

x = data[2][:, 0]
y = data[2][:, 2]
e_max = data[2][:, 1] - y
e_min = y - data[2][:, 3]

plt.errorbar(x, y, yerr=[e_min, e_max], label="Cirq", marker="o", linestyle=":", linewidth=1.0, capsize=2, color="#262626")

x = data[3][:, 0]
y = data[3][:, 2]
e_max = data[3][:, 1] - y
e_min = y - data[3][:, 3]

plt.errorbar(x, y, yerr=[e_min, e_max], label="TornadoQSim (non-accelerated)", marker="o", linestyle=":", linewidth=1.0, capsize=2, color="#7cacf6")

x = data[4][:, 0]
y = data[4][:, 2]
e_max = data[4][:, 1] - y
e_min = y - data[4][:, 3]

plt.errorbar(x, y, yerr=[e_min, e_max], label="TornadoQSim (Intel Core i7)", marker="o", linestyle=":", linewidth=1.0, capsize=2, color="#3b7ee6")

x = data[5][:, 0]
y = data[5][:, 2]
e_max = data[5][:, 1] - y
e_min = y - data[5][:, 3]

plt.errorbar(x, y, yerr=[e_min, e_max], label="TornadoQSim (Nvidia Quadro GP100)", marker="o", linestyle=":", linewidth=1.0, capsize=2, color="#002d8c")

# Define graph formalities
plt.legend(loc="lower right")
plt.xticks(qubits)
plt.xlabel("Number of qubits", labelpad=8)
plt.ylabel("Simulation time (ms)", labelpad=8)
plt.yscale("log")
plt.grid(color="grey", linestyle=":", linewidth=0.5, alpha=0.4)

# Show the defined plot
plt.show()
