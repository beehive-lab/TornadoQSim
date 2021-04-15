# TornadoQSim

TornadoQSim is a quantum computing framework, that allows to develop quantum algorithms and simulate them on a variety of quantum simulators. The aim is to achieve high-performance simulation of quantum circuits without the need to develop the simulator back-end in a platform specific programming language, such as CUDA. 

This framework is developed entirely in Java, including the quantum simulator back-ends that are accelerated on heterogeneous hardware using [TornadoVM](https://www.tornadovm.org/). 

TornadoQSim was developed by Ales Kubicek as part of the BSc Computer Science degree (final year project) at the University of Manchester.

## Usage
```java
// Bell state example
public static void main(String[] args) {
    int noQubits = 2;

    // Example circuit - Bell state
    Circuit circuit = new Circuit(noQubits);

    circuit.H(0);
    circuit.CNOT(0, 1);

    // Quantum simulator back-ends 
    // => other back-ends - future work
    Simulator simulatorAccelerated = new UnitarySimulatorAccelerated(noQubits);
    Simulator simulatorStandard = new UnitarySimulatorStandard();

    // Print full state vector
    System.out.println("--------- Accelerated --------");
    System.out.println(simulatorAccelerated.simulateFullState(circuit));

    System.out.println("---------- Standard ----------");
    System.out.println(simulatorStandard.simulateFullState(circuit));
}
```
