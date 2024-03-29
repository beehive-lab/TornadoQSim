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
package uk.ac.manchester.tornado.qsim.circuit.operation;

import org.junit.jupiter.api.Test;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.FunctionType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.InstructionType;

import static org.junit.jupiter.api.Assertions.*;

public class OperationTest {

    @Test
    public void testGate() {
        Gate gate = new Gate(GateType.H, 0);
        assertEquals(GateType.H, gate.type());
        assertEquals(0, gate.targetQubit()[0]);
        assertArrayEquals(new int[] { 0 }, gate.involvedQubits());
        assertEquals(1, gate.size());

        assertEquals(new Gate(GateType.H, 0), gate);

        assertThrows(IllegalArgumentException.class, () -> new Gate(GateType.X, -1));
        assertThrows(UnsupportedOperationException.class, () -> new Gate(GateType.X, 0, (float) Math.PI / 2));
        assertThrows(UnsupportedOperationException.class, () -> gate.phi());
    }

    @Test
    public void testPhaseShiftGate() {
        Gate gate = new Gate(GateType.R, 0, (float) Math.PI / 2);
        assertEquals(GateType.R, gate.type());
        assertEquals((float) Math.PI / 2, gate.phi());
        assertEquals(0, gate.targetQubit()[0]);
        assertArrayEquals(new int[] { 0 }, gate.involvedQubits());
        assertEquals(1, gate.size());
    }

    @Test
    public void testControlGate() {
        ControlGate cGate = new ControlGate(new Gate(GateType.X, 1), 0, 1);
        assertEquals(GateType.X, cGate.gate().type());
        assertEquals(0, cGate.controlQubit()[0]);
        assertEquals(1, cGate.targetQubit()[0]);
        assertArrayEquals(new int[] { 0, 1 }, cGate.involvedQubits());
        assertEquals(2, cGate.size());

        cGate = new ControlGate(new Gate(GateType.X, 0), 4, 0);
        assertEquals(GateType.X, cGate.gate().type());
        assertEquals(4, cGate.controlQubit()[0]);
        assertEquals(0, cGate.targetQubit()[0]);
        assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, cGate.involvedQubits());
        assertEquals(5, cGate.size());

        assertEquals(new ControlGate(new Gate(GateType.X, 0), 4, 0), cGate);

        assertThrows(IllegalArgumentException.class, () -> new ControlGate(new Gate(GateType.X, 3), 3, 3));
        assertThrows(IllegalArgumentException.class, () -> new ControlGate(new Gate(GateType.X, 3), -1, 3));
        assertThrows(IllegalArgumentException.class, () -> new ControlGate(new Gate(GateType.X, -1), 3, -1));
    }

    @Test
    public void testFunction() {
        Function qFunction = new Function(FunctionType.Swap, 0, 1);
        assertEquals(FunctionType.Swap, qFunction.type());
        assertEquals("", qFunction.name());
        assertArrayEquals(new int[] { 0, 1 }, qFunction.targetQubits());
        assertArrayEquals(new int[] { 0, 1 }, qFunction.involvedQubits());
        assertEquals(2, qFunction.size());

        qFunction = new Function("custom", 2, 3);
        assertEquals(FunctionType.Custom, qFunction.type());
        assertEquals("custom", qFunction.name());
        assertArrayEquals(new int[] { 2, 3 }, qFunction.targetQubits());
        assertArrayEquals(new int[] { 2, 3 }, qFunction.involvedQubits());
        assertEquals(2, qFunction.size());

        assertEquals(new Function("custom", 2, 3), qFunction);

        assertThrows(IllegalArgumentException.class, () -> new Function(FunctionType.Swap, 3, 2));
        assertThrows(IllegalArgumentException.class, () -> new Function(FunctionType.Swap, -1, 3));
        assertThrows(IllegalArgumentException.class, () -> new Function(FunctionType.Swap, 3, -1));
        assertThrows(IllegalArgumentException.class, () -> new Function("custom", 3, 2));
    }

    @Test
    public void testInstruction() {
        Instruction instruction = new Instruction(InstructionType.Measure, 0);
        assertEquals(InstructionType.Measure, instruction.type());
        assertEquals(0, instruction.targetQubit());
        assertArrayEquals(new int[] { 0 }, instruction.involvedQubits());
        assertEquals(1, instruction.size());

        assertEquals(new Instruction(InstructionType.Measure, 0), instruction);

        assertThrows(IllegalArgumentException.class, () -> new Instruction(InstructionType.Measure, -1));
    }
}
