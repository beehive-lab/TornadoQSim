# TornadoQSim

TornadoQSim is a quantum computing framework, that allows to develop quantum algorithms and simulate them on a variety of quantum simulators. The aim is to achieve high-performance simulation of quantum circuits without the need to develop the simulator back-end in a platform specific programming language, such as CUDA. 

This framework is developed entirely in Java, including the quantum simulator back-ends that are accelerated on heterogeneous hardware using [TornadoVM](https://www.tornadovm.org/). 

TornadoQSim was developed by Ales Kubicek as part of the BSc Computer Science degree (final year project) at the University of Manchester.

## Installation

1. Clone the project:

```bash 
git clone https://github.com/beehive-lab/TornadoQSim.git
```

2. Install dependencies:

- Install TornadoVM. The following example builds TornadoVM with GraalVM JDK 11 and OpenCL:

```bash
git clone https://github.com/beehive-lab/TornadoVM.git 
cd TornadoVM
./scripts/tornadoVMInstaller.sh --graal-jdk-11 --opencl
source source.sh
cd ..
```

**If you cannot build TornadoVM with the installer, try
the [manual installation](https://github.com/beehive-lab/TornadoVM/blob/master/assembly/src/docs/12_INSTALL_WITH_JDK11_PLUS.md)
.**

3. Set up the environment and store the variables in a file (e.g. `sources.env`):

```bash 
cd TornadoQSim
vim sources.env
export TORNADO_QSIM_ROOT="${PWD}/TornadoQSim"
export PATH="${PATH}:${TORNADO_QSIM_ROOT=}/bin"
export TORNADO_ROOT=<path to TornadoVM>
export PATH="${PATH}:${TORNADO_ROOT}/bin/bin/"
export TORNADO_SDK=${TORNADO_ROOT}/bin/sdk
export JAVA_HOME=${TORNADO_ROOT}/TornadoVM-OpenJDK11/jdk-11.0.13+8
```

Load the environment:

```bash
source sources.env
cd $TORNADO_QSIM_ROOT
```

4. Build TornadoQSim:

```bash
mvn clean install
```

5. Run TornadoQSim:

```bash
tornadovm-qsim
```

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
