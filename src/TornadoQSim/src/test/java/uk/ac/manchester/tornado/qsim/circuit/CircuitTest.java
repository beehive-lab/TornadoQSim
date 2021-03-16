package uk.ac.manchester.tornado.qsim.circuit;

import org.junit.jupiter.api.Test;
import uk.ac.manchester.tornado.qsim.circuit.operation.*;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.FunctionType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.InstructionType;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CircuitTest {

    @Test
    public void testCircuitDefinition() {
        Circuit circuit = new Circuit(5);
        assertEquals(5, circuit.qubitCount());
        assertEquals(1, circuit.depth());
        assertEquals(1, circuit.getSteps().size());
        Step initialStep = circuit.getSteps().get(0);
        for (int qubit = 0; qubit < circuit.qubitCount(); qubit++)
            assertTrue(initialStep.isQubitFree(qubit));

        assertThrows(IllegalArgumentException.class, () -> new Circuit(0));
        assertThrows(IllegalArgumentException.class, () -> new Circuit(-2));
    }

    @Test
    public void testCircuitGates() {
        // Build circuit from gates only
        Circuit circuit = new Circuit(6);
        circuit.X(0);
        circuit.Y(1);
        circuit.Z(2);
        circuit.H(3);
        circuit.S(4);
        circuit.T(5);
        circuit.H(0,1,2,3,4,5);
        circuit.X(3);

        // Check circuit properties
        assertEquals(6, circuit.qubitCount());
        assertEquals(3, circuit.depth());
        assertEquals(3, circuit.getSteps().size());

        // Check gate composition
        List<Step> steps = circuit.getSteps();
        GateType[] types;

        types = new GateType[] { GateType.X, GateType.Y, GateType.Z, GateType.H, GateType.S, GateType.T };
        assertStep(steps.get(0), types);

        types = new GateType[] { GateType.H, GateType.H, GateType.H, GateType.H, GateType.H, GateType.H };
        assertStep(steps.get(1), types);

        types = new GateType[] { null, null, null, GateType.X, null, null };
        assertStep(steps.get(2), types);

        assertThrows(IllegalArgumentException.class, () -> circuit.H(-1));
        assertThrows(IllegalArgumentException.class, () -> circuit.H(10));
    }

    @Test
    public void testCircuitControlGates() {
        // Build circuit from control gates only
        Circuit circuit = new Circuit(6);
        circuit.CX(0,1);
        circuit.CY(2,3);
        circuit.CZ(4,5);
        circuit.CH(5,3);
        circuit.CS(4, 5);
        circuit.CT(2, 0);

        // Check circuit properties
        assertEquals(6, circuit.qubitCount());
        assertEquals(3, circuit.depth());
        assertEquals(3, circuit.getSteps().size());

        // Check gate composition
        List<Step> steps = circuit.getSteps();
        GateType[] types;

        types = new GateType[] { GateType.X, GateType.X, GateType.Y, GateType.Y, GateType.Z, GateType.Z };
        assertStep(steps.get(0), types);

        types = new GateType[] { null, null, null, GateType.H, GateType.H, GateType.H };
        assertStep(steps.get(1), types);

        types = new GateType[] { GateType.T, GateType.T, GateType.T, null, GateType.S, GateType.S };
        assertStep(steps.get(2), types);

        assertThrows(IllegalArgumentException.class, () -> circuit.CH(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> circuit.CH(2, 10));
    }

    @Test
    public void testCircuitFunctions() {
        // Build circuit from functions only
        OperationDataProvider.getInstance().registerFunctionData("custom", new ComplexTensor(2,2));
        Circuit circuit = new Circuit(4);
        circuit.swap(0,2);
        circuit.customFunction("custom",0,0);

        // Check circuit properties
        assertEquals(4, circuit.qubitCount());
        assertEquals(2, circuit.depth());
        assertEquals(2, circuit.getSteps().size());

        // Check gate composition
        List<Step> steps = circuit.getSteps();
        FunctionType[] types;

        types = new FunctionType[] { FunctionType.Swap, FunctionType.Swap, FunctionType.Swap, null };
        assertStep(steps.get(0), types);

        types = new FunctionType[] { FunctionType.Custom, null, null, null };
        assertStep(steps.get(1), types);

        assertThrows(IllegalArgumentException.class, () -> circuit.swap(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> circuit.swap(3, 4));
        assertThrows(IllegalArgumentException.class, () -> circuit.customFunction("custom",-1, 0));
        assertThrows(IllegalArgumentException.class, () -> circuit.customFunction("custom",2, 10));
        assertThrows(IllegalArgumentException.class, () -> circuit.customFunction("invalid",2, 3));
    }

    @Test
    public void testCircuitInstructions() {
        // Build circuit from instructions only
        Circuit circuit = new Circuit(3);
        circuit.measure(0,1,2);
        circuit.reset(2,1);
        circuit.measure(1,2);

        // Check circuit properties
        assertEquals(3, circuit.qubitCount());
        assertEquals(3, circuit.depth());
        assertEquals(3, circuit.getSteps().size());

        // Check gate composition
        List<Step> steps = circuit.getSteps();
        InstructionType[] types;

        types = new InstructionType[] { InstructionType.Measure, InstructionType.Measure, InstructionType.Measure };
        assertStep(steps.get(0), types);

        types = new InstructionType[] { null, InstructionType.Reset, InstructionType.Reset };
        assertStep(steps.get(1), types);

        types = new InstructionType[] { null, InstructionType.Measure, InstructionType.Measure };
        assertStep(steps.get(2), types);

        assertThrows(IllegalArgumentException.class, () -> circuit.measure(-1));
        assertThrows(IllegalArgumentException.class, () -> circuit.measure(10));
    }

    @Test
    public void testCircuitAppend() {
        Circuit circuit = new Circuit(3);
        circuit.H(0,1,2);
        circuit.swap(1,2);

        Circuit another = new Circuit(3);
        another.X(0,2);
        another.measure(0,1,2);

        circuit.appendCircuit(another);

        assertEquals(3, circuit.qubitCount());
        assertEquals(4, circuit.depth());
        assertEquals(4, circuit.getSteps().size());

        // Check gate composition
        List<Step> steps = circuit.getSteps();

        assertStep(steps.get(0), new GateType[] { GateType.H, GateType.H, GateType.H });
        assertStep(steps.get(1), new FunctionType[] { null, FunctionType.Swap, FunctionType.Swap });
        assertStep(steps.get(2), new GateType[] { GateType.X, null, GateType.X });
        assertStep(steps.get(3), new InstructionType[] { InstructionType.Measure, InstructionType.Measure, InstructionType.Measure });

        assertThrows(IllegalArgumentException.class, () -> circuit.appendCircuit(null));
        assertThrows(IllegalArgumentException.class, () -> circuit.appendCircuit(new Circuit(5)));
    }

    @Test
    public void testCircuitEquality() {
        Circuit a = new Circuit(3);
        a.H(0,1,2);
        a.swap(0,1);
        a.X(1);
        a.measure(0,1,2);

        Circuit b = new Circuit(3);
        b.H(0);
        b.H(1);
        b.H(2);
        b.swap(0,1);
        b.X(1);
        b.measure(0,1);
        b.measure(2);

        Circuit c = new Circuit(3);
        c.H(0,1,2);
        c.swap(0,1);
        c.Y(1);
        c.measure(0,1,2);

        assertEquals(a, b);
        assertNotEquals(a, c);
    }

    private void assertStep(Step step, GateType[] types) {
        // Assume types.length == noQubits (type per qubit)
        for (int qubit = 0; qubit < types.length; qubit++) {
            Operation operation = step.getOperation(qubit);
            if (operation == null) {
                assertNull(types[qubit]);
                assertTrue(step.isQubitFree(qubit));
            }
            else if (operation instanceof Gate) {
                Gate gate = (Gate)(step.getOperation(qubit));
                assertEquals(types[qubit], gate.type());
                assertFalse(step.isQubitFree(qubit));
            }
            else if (operation instanceof ControlGate) {
                ControlGate controlGate = (ControlGate)(step.getOperation(qubit));
                assertEquals(types[qubit], controlGate.gate().type());
                assertFalse(step.isQubitFree(qubit));
            }
            else {
                throw new UnsupportedOperationException("Only Gate or ControlGate supported for this assertStep.");
            }
        }
    }

    private void assertStep(Step step, FunctionType[] types) {
        // Assume types.length == noQubits (type per qubit)
        for (int qubit = 0; qubit < types.length; qubit++) {
            Operation operation = step.getOperation(qubit);
            if (operation == null) {
                assertNull(types[qubit]);
                assertTrue(step.isQubitFree(qubit));
            }
            else if (operation instanceof Function) {
                Function function = (Function)(step.getOperation(qubit));
                assertEquals(types[qubit], function.type());
                assertFalse(step.isQubitFree(qubit));
            }
            else {
                throw new UnsupportedOperationException("Only Gate or ControlGate supported for this assertStep.");
            }
        }
    }

    private void assertStep(Step step, InstructionType[] types) {
        // Assume types.length == noQubits (type per qubit)
        for (int qubit = 0; qubit < types.length; qubit++) {
            Operation operation = step.getOperation(qubit);
            if (operation == null) {
                assertNull(types[qubit]);
                assertTrue(step.isQubitFree(qubit));
            }
            else if (operation instanceof Instruction) {
                Instruction instruction = (Instruction)(step.getOperation(qubit));
                assertEquals(types[qubit], instruction.type());
                assertFalse(step.isQubitFree(qubit));
            }
            else {
                throw new UnsupportedOperationException("Only Gate or ControlGate supported for this assertStep.");
            }
        }
    }
}
