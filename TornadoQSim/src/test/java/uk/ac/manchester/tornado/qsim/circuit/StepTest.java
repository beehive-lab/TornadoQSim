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
package uk.ac.manchester.tornado.qsim.circuit;

import org.junit.jupiter.api.Test;
import uk.ac.manchester.tornado.qsim.circuit.operation.*;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.*;

import static org.junit.jupiter.api.Assertions.*;

public class StepTest {

    @Test
    public void testStepDefinition() {
        Step step = new Step(1);
        assertEquals(true, step.isQubitFree(0));
        assertEquals(null, step.getOperation(0));

        step = new Step(5);
        for (int qubit = 0; qubit < 5; qubit++) {
            assertEquals(true, step.isQubitFree(qubit));
            assertEquals(null, step.getOperation(qubit));
        }

        Step finalStep = step;
        assertThrows(IllegalArgumentException.class, () -> finalStep.isQubitFree(-2));
        assertThrows(IllegalArgumentException.class, () -> finalStep.isQubitFree(10));
        assertThrows(IllegalArgumentException.class, () -> finalStep.getOperation(-2));
        assertThrows(IllegalArgumentException.class, () -> finalStep.getOperation(10));

        assertThrows(IllegalArgumentException.class, () -> new Step(0));
        assertThrows(IllegalArgumentException.class, () -> new Step(-2));
    }

    @Test
    public void testStepOperations() {
        Operation[] operations = new Operation[] { new Gate(GateType.H, 0), new ControlGate(new Gate(GateType.X, 1), 3, 1), new Function(FunctionType.Swap, 4, 5),
                new Instruction(InstructionType.Measure, 6) };

        Step step = new Step(7);
        for (Operation operation : operations)
            assertEquals(true, step.canAddOperation(operation));

        for (Operation operation : operations)
            step.addOperation(operation);

        for (Operation operation : operations)
            assertEquals(false, step.canAddOperation(operation));

        assertEquals(4, step.getOperationCount());

        assertEquals(operations[0], step.getOperation(0));
        assertEquals(operations[1], step.getOperation(1));
        assertEquals(operations[1], step.getOperation(2));
        assertEquals(operations[1], step.getOperation(3));
        assertEquals(operations[2], step.getOperation(4));
        assertEquals(operations[2], step.getOperation(5));
        assertEquals(operations[3], step.getOperation(6));

        for (int qubit = 0; qubit < 7; qubit++)
            assertEquals(false, step.isQubitFree(qubit));

        Gate invalid = new Gate(GateType.X, 10);
        assertThrows(IllegalArgumentException.class, () -> step.canAddOperation(null));
        assertThrows(IllegalArgumentException.class, () -> step.canAddOperation(invalid));

        assertThrows(IllegalArgumentException.class, () -> step.addOperation(null));
        assertThrows(IllegalArgumentException.class, () -> step.addOperation(invalid));
    }

    @Test
    public void testStepEquality() {
        Step a = new Step(7);
        Step b = new Step(6);
        assertNotEquals(a, b);

        b = new Step(7);
        assertEquals(a, b);

        a.addOperation(new Gate(GateType.H, 3));
        assertNotEquals(a, b);

        b.addOperation(new Gate(GateType.H, 3));
        assertEquals(a, b);
    }
}
