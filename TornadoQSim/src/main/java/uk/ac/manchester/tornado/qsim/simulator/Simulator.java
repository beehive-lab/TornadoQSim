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
package uk.ac.manchester.tornado.qsim.simulator;

import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.circuit.State;

/**
 * Represents a general quantum simulator.
 * 
 * @author Ales Kubicek
 */
public interface Simulator {
    /**
     * Simulates the supplied circuit and returns the full state representation of
     * the quantum system.
     * 
     * @param circuit
     *            quantum circuit to be simulated.
     * @return full state of the simulated circuit.
     */
    public State simulateFullState(Circuit circuit);

    /**
     * Simulates the supplied circuit and returns the collapsed state of the quantum
     * system.
     * 
     * @param circuit
     *            quantum circuit to be simulated.
     * @return collapsed state of the simulated circuit (bitstring).
     */
    public int simulateAndCollapse(Circuit circuit);
}
