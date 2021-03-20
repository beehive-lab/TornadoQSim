import numpy as np
import matplotlib.pyplot as plt
import data

qubits = np.arange(1, 20)
data = data.get_experiment_data("entanglement")

x = data[0][:, 0]
y = data[0][:, 4]

plt.plot(x, y, label="Qiskit", marker="o", linestyle=":", linewidth=1.0, color="darkgrey")


x = data[1][:, 0]
y = data[1][:, 4]

plt.plot(x, y, label="Strange", marker="o", linestyle=":", linewidth=1.0, color="dimgrey")

x = data[2][:, 0]
y = data[2][:, 4]

plt.plot(x, y, label="TornadoQSim (standard)", marker="o", linestyle=":", linewidth=1.0, color="blue")

x = data[3][:, 0]
y = data[3][:, 4]

plt.plot(x, y, label="TornadoQSim (accelerated)", marker="o", linestyle=":", linewidth=1.0, color="navy")

# Define graph formalities
plt.legend(loc="lower right")
plt.xticks(qubits)
plt.xlabel("Number of qubits", labelpad=8)
plt.ylabel("Peak memory (B)", labelpad=8)
plt.yscale("log")
plt.grid(color="grey", linestyle=":", linewidth=0.5, alpha=0.4)

# Show the defined plot
plt.show()
