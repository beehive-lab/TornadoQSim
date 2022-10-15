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
package uk.ac.manchester.tornado.qsim.simulator.unitary;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.circuit.State;
import uk.ac.manchester.tornado.qsim.circuit.utils.StateConverter;
import uk.ac.manchester.tornado.qsim.math.Complex;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnitarySimulatorStandardTest {

    private static Complex HALF,iHALF;

    @BeforeAll
    private static void prepareConstants() {
        HALF = new Complex((float) (1 / Math.sqrt(2)), 0);
        iHALF = new Complex(0, (float) (1 / Math.sqrt(2)));
    }

    @Test
    public void testBellState() {
        Circuit circuit;
        State state;
        Simulator unitarySimulator = new UnitarySimulatorStandard();

        // Test Bell state qubits: 0-1
        circuit = new Circuit(3);
        circuit.H(0);
        circuit.CNOT(0, 1);
        state = unitarySimulator.simulateFullState(circuit);

        assertTrue(state.isNormalized());
        assertEquals(HALF, state.getStateAmplitude(StateConverter.stateFromBitstring("000")));
        assertEquals(HALF, state.getStateAmplitude(StateConverter.stateFromBitstring("011")));

        // Test Bell state qubits: 0-2
        circuit = new Circuit(3);
        circuit.H(0);
        circuit.CNOT(0, 2);
        state = unitarySimulator.simulateFullState(circuit);

        assertTrue(state.isNormalized());
        assertEquals(HALF, state.getStateAmplitude(StateConverter.stateFromBitstring("000")));
        assertEquals(HALF, state.getStateAmplitude(StateConverter.stateFromBitstring("101")));

        // Test Bell state qubits: 1-2
        circuit = new Circuit(3);
        circuit.H(1);
        circuit.CNOT(1, 2);
        state = unitarySimulator.simulateFullState(circuit);

        assertTrue(state.isNormalized());
        assertEquals(HALF, state.getStateAmplitude(StateConverter.stateFromBitstring("000")));
        assertEquals(HALF, state.getStateAmplitude(StateConverter.stateFromBitstring("110")));
    }

    @Test
    public void testRandomCircuit() {
        Simulator unitarySimulator = new UnitarySimulatorStandard();
        Circuit circuit = new Circuit(3);
        circuit.H(0);
        circuit.T(2);
        circuit.CNOT(0, 2);
        circuit.CNOT(2, 0);
        circuit.Z(0);
        circuit.S(2);
        circuit.T(0);
        circuit.Z(0);
        circuit.X(0);

        State state = unitarySimulator.simulateFullState(circuit);

        assertTrue(state.isNormalized());
        assertEquals(HALF, state.getStateAmplitude(StateConverter.stateFromBitstring("001")));
        assertEquals(iHALF, state.getStateAmplitude(StateConverter.stateFromBitstring("101")));
    }

}
