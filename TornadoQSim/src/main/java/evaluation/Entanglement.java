/*
 * This file is part of TornadoQSim:
 * A Java-based quantum computing framework accelerated with TornadoVM.
 *
 * URL: https://github.com/beehive-lab/TornadoQSim
 *
 * Copyright (c) 2021-2022, APT Group, Department of Computer Science,
 * The University of Manchester. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package evaluation;

import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;
import uk.ac.manchester.tornado.qsim.simulator.fullstatevector.FsvSimulatorAccelerated;
import uk.ac.manchester.tornado.qsim.simulator.fullstatevector.FsvSimulatorStandard;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorAccelerated;
import uk.ac.manchester.tornado.qsim.simulator.unitary.UnitarySimulatorStandard;

/**
 * Evaluate a simulation of fully entangled quantum circuit.
 * 
 * @author Ales Kubicek
 */
public class Entanglement {

    /**
     * Run the evaluation using: "tornado evaluation.Entanglement".
     * 
     * @param args
     *            args[0] simulator type (1-4), args[1] - number of qubits in the
     *            quantum circuit.
     */
    public static void main(String[] args) {
        int noQubits = Common.getQubitCount(args);
        int simulatorType = Common.getSimulatorType(args);

        Circuit circuit = new Circuit(noQubits);
        circuit.H(0);
        for (int target = noQubits - 1; target > 0; target--)
            circuit.CNOT(0, target);

        Simulator simulator = new FsvSimulatorStandard();
        switch (simulatorType) {
            case 1:
                simulator = new UnitarySimulatorStandard();
                break;
            case 2:
                simulator = new UnitarySimulatorAccelerated(noQubits);
                break;
            case 3:
                simulator = new FsvSimulatorStandard();
                break;
            case 4:
                simulator = new FsvSimulatorAccelerated(noQubits);
                break;
        }

        Common.simulateAndPrint(simulator, circuit);
    }

}
