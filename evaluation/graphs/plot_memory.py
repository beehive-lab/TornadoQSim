import numpy as np
import matplotlib.pyplot as plt
import data

qubits = np.arange(1, 16)
data = data.get_experiment_data("qft")

if data[0].size > 0:
    x = data[0][:, 0]
    y = data[0][:, 4]
    plt.plot(x, y, label="Qiskit", marker="o", linestyle=":", linewidth=1.0, color="#848484")

if data[1].size > 0:
    x = data[1][:, 0]
    y = data[1][:, 4]
    plt.plot(x, y, label="Strange", marker="o", linestyle=":", linewidth=1.0, color="#545454")

if data[2].size > 0:
    x = data[2][:, 0]
    y = data[2][:, 4]
    plt.plot(x, y, label="Cirq", marker="o", linestyle=":", linewidth=1.0, color="#262626")

if data[3].size > 0:
    x = data[3][:, 0]
    y = data[3][:, 4]
    plt.plot(x, y, label="TornadoQSim (non-accelerated)", marker="o", linestyle=":", linewidth=1.0, color="#7cacf6")

if data[4].size > 0:
    x = data[4][:, 0]
    y = data[4][:, 4]
    plt.plot(x, y, label="TornadoQSim (Intel Core i7)", marker="o", linestyle=":", linewidth=1.0, color="#3b7ee6")

if data[5].size > 0:
    x = data[5][:, 0]
    y = data[5][:, 4]
    plt.plot(x, y, label="TornadoQSim (Nvidia Quadro GP100)", marker="o", linestyle=":", linewidth=1.0, color="#002d8c")

# Define graph formalities
plt.legend(loc="lower right")
plt.xticks(qubits)
plt.xlabel("Number of qubits", labelpad=8)
plt.ylabel("Peak memory (B)", labelpad=8)
plt.yscale("log")
plt.grid(color="grey", linestyle=":", linewidth=0.5, alpha=0.4)

# Show the defined plot
plt.show()
