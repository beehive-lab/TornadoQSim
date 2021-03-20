import numpy as np
import matplotlib.pyplot as plt
import data

qubits = np.arange(1, 20)
data = data.get_experiment_data("entanglement")

x = data[0][:, 0]
y = data[0][:, 2]
e_max = data[0][:, 1] - y
e_min = y - data[0][:, 3]

plt.errorbar(x, y, yerr=[e_min, e_max], label="Qiskit", marker="o", linestyle=":", linewidth=1.0, capsize=2, color="darkgrey")


x = data[1][:, 0]
y = data[1][:, 2]
e_max = data[1][:, 1] - y
e_min = y - data[1][:, 3]

plt.errorbar(x, y, yerr=[e_min, e_max], label="Strange", marker="o", linestyle=":", linewidth=1.0, capsize=2, color="dimgrey")

x = data[2][:, 0]
y = data[2][:, 2]
e_max = data[2][:, 1] - y
e_min = y - data[2][:, 3]

plt.errorbar(x, y, yerr=[e_min, e_max], label="TornadoQSim (standard)", marker="o", linestyle=":", linewidth=1.0, capsize=2, color="blue")

x = data[3][:, 0]
y = data[3][:, 2]
e_max = data[3][:, 1] - y
e_min = y - data[3][:, 3]

plt.errorbar(x, y, yerr=[e_min, e_max], label="TornadoQSim (accelerated)", marker="o", linestyle=":", linewidth=1.0, capsize=2, color="navy")

# Define graph formalities
plt.legend(loc="lower right")
plt.xticks(qubits)
plt.xlabel("Number of qubits", labelpad=8)
plt.ylabel("Simulation time (ms)", labelpad=8)
plt.yscale("log")
plt.grid(color="grey", linestyle=":", linewidth=0.5, alpha=0.4)

# Show the defined plot
plt.show()
