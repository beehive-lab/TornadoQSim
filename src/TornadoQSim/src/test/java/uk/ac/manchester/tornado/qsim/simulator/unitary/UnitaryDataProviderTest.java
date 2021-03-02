package uk.ac.manchester.tornado.qsim.simulator.unitary;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.circuit.Step;
import uk.ac.manchester.tornado.qsim.circuit.operation.*;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.FunctionType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.InstructionType;
import uk.ac.manchester.tornado.qsim.math.Complex;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UnitaryDataProviderTest {

    private static UnitaryDataProvider dataProvider;
    private static Complex ZERO, ONE;

    @BeforeAll
    public static void prepareDataProvider() {
        dataProvider = new UnitaryDataProvider(false);
        ZERO = new Complex(0,0);
        ONE = new Complex(1,0);
    }

    @Test
    public void testGateUnitaryData() {
        OperationDataProvider origin = OperationDataProvider.getInstance();
        assertEquals(origin.getData(GateType.X), dataProvider.getOperationData(new Gate(GateType.X, 0)));
        assertEquals(origin.getData(GateType.Y), dataProvider.getOperationData(new Gate(GateType.Y, 0)));
        assertEquals(origin.getData(GateType.Z), dataProvider.getOperationData(new Gate(GateType.Z, 0)));
        assertEquals(origin.getData(GateType.H), dataProvider.getOperationData(new Gate(GateType.H, 0)));
        assertEquals(origin.getData(GateType.S), dataProvider.getOperationData(new Gate(GateType.S, 0)));
        assertEquals(origin.getData(GateType.T), dataProvider.getOperationData(new Gate(GateType.T, 0)));
        assertEquals(origin.getData(GateType.I), dataProvider.getOperationData(new Gate(GateType.I, 0)));
    }

    @Test
    public void testControlGateUnitaryData() {
        ComplexTensor controlGateData;
        controlGateData = dataProvider.getOperationData(new ControlGate(GateType.X, 6, 5));

        assertEquals(ONE, controlGateData.getElement(0,0));
        assertEquals(ZERO, controlGateData.getElement(0,1));
        assertEquals(ZERO, controlGateData.getElement(0,2));
        assertEquals(ZERO, controlGateData.getElement(0,3));
        assertEquals(ZERO, controlGateData.getElement(1,0));
        assertEquals(ONE, controlGateData.getElement(1,1));
        assertEquals(ZERO, controlGateData.getElement(1,2));
        assertEquals(ZERO, controlGateData.getElement(1,3));
        assertEquals(ZERO, controlGateData.getElement(2,0));
        assertEquals(ZERO, controlGateData.getElement(2,1));
        assertEquals(ZERO, controlGateData.getElement(2,2));
        assertEquals(ONE, controlGateData.getElement(2,3));
        assertEquals(ZERO, controlGateData.getElement(3,0));
        assertEquals(ZERO, controlGateData.getElement(3,1));
        assertEquals(ONE, controlGateData.getElement(3,2));
        assertEquals(ZERO, controlGateData.getElement(3,3));

        controlGateData = dataProvider.getOperationData(new ControlGate(GateType.X, 5, 6));

        assertEquals(ONE, controlGateData.getElement(0,0));
        assertEquals(ZERO, controlGateData.getElement(0,1));
        assertEquals(ZERO, controlGateData.getElement(0,2));
        assertEquals(ZERO, controlGateData.getElement(0,3));
        assertEquals(ZERO, controlGateData.getElement(1,0));
        assertEquals(ZERO, controlGateData.getElement(1,1));
        assertEquals(ZERO, controlGateData.getElement(1,2));
        assertEquals(ONE, controlGateData.getElement(1,3));
        assertEquals(ZERO, controlGateData.getElement(2,0));
        assertEquals(ZERO, controlGateData.getElement(2,1));
        assertEquals(ONE, controlGateData.getElement(2,2));
        assertEquals(ZERO, controlGateData.getElement(2,3));
        assertEquals(ZERO, controlGateData.getElement(3,0));
        assertEquals(ONE, controlGateData.getElement(3,1));
        assertEquals(ZERO, controlGateData.getElement(3,2));
        assertEquals(ZERO, controlGateData.getElement(3,3));
    }

    @Test
    public void testFunctionUnitaryData() {
        // TODO: implement data generation for standard functions
        Function function = new Function(FunctionType.QFT, 0, 2);
        assertThrows(UnsupportedOperationException.class, () -> dataProvider.getOperationData(function));
    }

    @Test
    public void testCustomFunctionUnitaryData() {
        Function function = new Function("custom", 0, 0);
        ComplexTensor customData = new ComplexTensor(2,2);
        OperationDataProvider.getInstance().registerFunctionData("custom", customData);

        assertEquals(customData, dataProvider.getOperationData(function));

        Function invalidSize = new Function("custom", 0, 1);
        Function invalidName = new Function("invalid", 0, 0);

        assertThrows(IllegalArgumentException.class, () -> dataProvider.getOperationData(invalidSize));
        assertThrows(IllegalArgumentException.class, () -> dataProvider.getOperationData(invalidName));
    }

    @Test
    public void testInstructionUnitaryData() {
        Instruction measure = new Instruction(InstructionType.Measure, 0);
        Instruction reset = new Instruction(InstructionType.Reset, 0);
        assertThrows(UnsupportedOperationException.class, () -> dataProvider.getOperationData(measure));
        assertThrows(UnsupportedOperationException.class, () -> dataProvider.getOperationData(reset));
    }

    @Test
    public void testStepUnitaryData() {
        Circuit circuit = new Circuit(5);
        circuit.H(0);
        circuit.CNOT(2,1);
        circuit.Z(4);

        Step step = circuit.getSteps().get(0);
        List<ComplexTensor> stepUnitaryData = dataProvider.getStepOperationData(5, step);
        List<ComplexTensor> expectedData = getExpectedData();

        assertEquals(expectedData, stepUnitaryData);
    }

    private List<ComplexTensor> getExpectedData() {
        List<ComplexTensor> expected = new LinkedList<>();

        expected.add(OperationDataProvider.getInstance().getData(GateType.H));
        expected.add(dataProvider.getOperationData(new ControlGate(GateType.X, 2, 1)));
        expected.add(OperationDataProvider.getInstance().getData(GateType.I));
        expected.add(OperationDataProvider.getInstance().getData(GateType.Z));

        return expected;
    }
}
