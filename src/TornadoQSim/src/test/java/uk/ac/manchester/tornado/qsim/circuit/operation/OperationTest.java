package uk.ac.manchester.tornado.qsim.circuit.operation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.ac.manchester.tornado.qsim.circuit.Qubit;
import uk.ac.manchester.tornado.qsim.circuit.TestQubitFactory;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.FunctionType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.InstructionType;

import static org.junit.jupiter.api.Assertions.*;

public class OperationTest {

    private static Qubit[] q;

    @BeforeAll
    public static void createQubits() {
        q = TestQubitFactory.getInstance().createQubits(0,1,2,3,4);
    }

    @Test
    public void testGate() {
        Gate gate = new Gate(GateType.H, q[0]);
        assertEquals(GateType.H, gate.type());
        assertEquals(q[0], gate.targetQubit());
        assertArrayEquals(new Qubit[] { q[0] }, gate.involvedQubits());

        assertEquals(new Gate(GateType.H, q[0]), gate);

        assertThrows(IllegalArgumentException.class, () -> new Gate(GateType.H, null));
    }

    @Test
    public void testControlGate() {
        ControlGate cGate = new ControlGate(GateType.X, q[0], q[1]);
        assertEquals(GateType.X, cGate.type());
        assertEquals(q[0], cGate.controlQubit());
        assertEquals(q[1], cGate.targetQubit());
        assertArrayEquals(new Qubit[] { q[0], q[1] }, cGate.involvedQubits());

        cGate = new ControlGate(GateType.X, q[4], q[0]);
        assertEquals(GateType.X, cGate.type());
        assertEquals(q[4], cGate.controlQubit());
        assertEquals(q[0], cGate.targetQubit());
        assertArrayEquals(new Qubit[] { q[4], q[0] }, cGate.involvedQubits());

        assertEquals(new ControlGate(GateType.X, q[4], q[0]), cGate);

        assertThrows(IllegalArgumentException.class, () -> new ControlGate(GateType.X, q[0], null));
        assertThrows(IllegalArgumentException.class, () -> new ControlGate(GateType.X, null, q[0]));
    }

    @Test
    public void testFunction() {
        Function qFunction = new Function(FunctionType.Oracle, q[0],q[1],q[2]);
        assertEquals(FunctionType.Oracle, qFunction.type());
        assertEquals("", qFunction.name());
        assertEquals(3, qFunction.size());
        assertArrayEquals(new Qubit[] { q[0], q[1], q[2] }, qFunction.targetQubits());
        assertArrayEquals(new Qubit[] { q[0], q[1], q[2] }, qFunction.involvedQubits());

        qFunction = new Function("custom", q[2],q[3]);
        assertEquals(FunctionType.Custom, qFunction.type());
        assertEquals("custom", qFunction.name());
        assertEquals(2, qFunction.size());
        assertArrayEquals(new Qubit[] { q[2], q[3] }, qFunction.targetQubits());
        assertArrayEquals(new Qubit[] { q[2], q[3] }, qFunction.involvedQubits());

        assertEquals(new Function("custom", q[2],q[3]), qFunction);

        assertThrows(IllegalArgumentException.class, () -> new Function(FunctionType.Oracle, null));
        assertThrows(IllegalArgumentException.class, () -> new Function(FunctionType.Oracle, new Qubit[0]));
        assertThrows(IllegalArgumentException.class, () -> new Function(FunctionType.Oracle, q[0],q[2],q[3]));
        assertThrows(IllegalArgumentException.class, () -> new Function(FunctionType.Oracle, q[3],q[2],q[0]));
        assertThrows(IllegalArgumentException.class, () -> new Function(FunctionType.Custom, q[0]));

        assertThrows(IllegalArgumentException.class, () -> new Function("custom", null));
        assertThrows(IllegalArgumentException.class, () -> new Function("custom", new Qubit[0]));
        assertThrows(IllegalArgumentException.class, () -> new Function("custom", q[0],q[2],q[3]));
        assertThrows(IllegalArgumentException.class, () -> new Function("custom", q[3],q[2],q[0]));
    }

    @Test
    public void testInstruction() {
        Instruction instruction = new Instruction(InstructionType.Measure, q[0]);
        assertEquals(InstructionType.Measure, instruction.type());
        assertEquals(q[0], instruction.targetQubit());
        assertArrayEquals(new Qubit[] { q[0] }, instruction.involvedQubits());

        assertEquals(new Instruction(InstructionType.Measure, q[0]), instruction);

        assertThrows(IllegalArgumentException.class, () -> new Instruction(InstructionType.Measure, null));
    }
}
