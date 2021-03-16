package uk.ac.manchester.tornado.qsim.circuit.operation;

import org.junit.jupiter.api.Test;
import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;
import uk.ac.manchester.tornado.qsim.math.Complex;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;

import static org.junit.jupiter.api.Assertions.*;

public class OperationDataProviderTest {

    @Test
    public void testSingletonInstance() {
        assertEquals(OperationDataProvider.getInstance(), OperationDataProvider.getInstance());
    }

    @Test
    public void testGateDataProvider() {
        OperationDataProvider dataProvider = OperationDataProvider.getInstance();

        ComplexTensor gate = new ComplexTensor(gateData(GateType.X), 2,2);
        assertEquals(gate, dataProvider.getData(new Gate(GateType.X, 0)));

        gate = new ComplexTensor(gateData(GateType.Y), 2,2);
        assertEquals(gate, dataProvider.getData(new Gate(GateType.Y, 0)));

        gate = new ComplexTensor(gateData(GateType.Z), 2,2);
        assertEquals(gate, dataProvider.getData(new Gate(GateType.Z, 0)));

        gate = new ComplexTensor(gateData(GateType.H), 2,2);
        assertEquals(gate, dataProvider.getData(new Gate(GateType.H, 0)));

        gate = new ComplexTensor(gateData(GateType.S), 2,2);
        assertEquals(gate, dataProvider.getData(new Gate(GateType.S, 0)));

        gate = new ComplexTensor(gateData(GateType.T), 2,2);
        assertEquals(gate, dataProvider.getData(new Gate(GateType.T, 0)));

        gate = new ComplexTensor(gateData(GateType.I), 2,2);
        assertEquals(gate, dataProvider.getData(new Gate(GateType.I, 0)));
    }

    @Test
    public void testPhaseShiftGateDataProvider() {
        OperationDataProvider dataProvider = OperationDataProvider.getInstance();

        float phi = (float)(Math.PI / 1.0);
        ComplexTensor gate = new ComplexTensor(phaseShiftGateData(phi), 2,2);
        assertEquals(gate, dataProvider.getData(new Gate(GateType.R, 0, phi)));

        phi = (float)(Math.PI / 2.0);
        gate = new ComplexTensor(phaseShiftGateData(phi), 2,2);
        assertEquals(gate, dataProvider.getData(new Gate(GateType.R, 0, phi)));

        phi = (float)(Math.PI / 4.0);
        gate = new ComplexTensor(phaseShiftGateData(phi), 2,2);
        assertEquals(gate, dataProvider.getData(new Gate(GateType.R, 0, phi)));

        phi = (float)(Math.PI / 8.0);
        gate = new ComplexTensor(phaseShiftGateData(phi), 2,2);
        assertEquals(gate, dataProvider.getData(new Gate(GateType.R, 0, phi)));
    }

    @Test
    public void testFunctionDataProvider() {
        OperationDataProvider dataProvider = OperationDataProvider.getInstance();

        ComplexTensor validData = new ComplexTensor(gateData(GateType.H), 2,2);
        dataProvider.registerFunctionData("custom", validData);
        assertEquals(validData, dataProvider.getData("custom"));

        validData = new ComplexTensor(gateData(GateType.X), 2,2);
        dataProvider.registerFunctionData("custom", validData);
        assertEquals(validData, dataProvider.getData("custom"));

        assertTrue(dataProvider.isFunctionDataRegistered("custom"));
        assertFalse(dataProvider.isFunctionDataRegistered("other"));

        String invalidName = null;
        ComplexTensor invalidData = new ComplexTensor(5);
        ComplexTensor data = validData;

        assertThrows(IllegalArgumentException.class, () -> dataProvider.registerFunctionData(invalidName, data));
        assertThrows(IllegalArgumentException.class, () -> dataProvider.registerFunctionData("", data));
        assertThrows(IllegalArgumentException.class, () -> dataProvider.registerFunctionData("valid", invalidData));

        assertThrows(IllegalArgumentException.class, () -> dataProvider.isFunctionDataRegistered(invalidName));
        assertThrows(IllegalArgumentException.class, () -> dataProvider.isFunctionDataRegistered(""));

        assertThrows(IllegalArgumentException.class, () -> dataProvider.getData(invalidName));
        assertThrows(IllegalArgumentException.class, () -> dataProvider.getData(""));
        assertThrows(IllegalArgumentException.class, () -> dataProvider.getData("other"));
    }

    private Complex[] gateData(GateType type) {
        switch (type) {
            case X:
                return new Complex[] {
                        new Complex(0,0),
                        new Complex(1,0),
                        new Complex(1,0),
                        new Complex(0,0)
                };
            case Y:
                return new Complex[] {
                        new Complex(0,0),
                        new Complex(0,-1),
                        new Complex(0,1),
                        new Complex(0,0)
                };
            case Z:
                return new Complex[] {
                        new Complex(1,0),
                        new Complex(0,0),
                        new Complex(0,0),
                        new Complex(-1,0)
                };
            case H:
                return new Complex[] {
                        new Complex((float)(1 / Math.sqrt(2)),0),
                        new Complex((float)(1 / Math.sqrt(2)),0),
                        new Complex((float)(1 / Math.sqrt(2)),0),
                        new Complex((float)(-1 / Math.sqrt(2)),0)
                };
            case S:
                return new Complex[] {
                        new Complex(1,0),
                        new Complex(0,0),
                        new Complex(0,0),
                        new Complex(0,1)
                };
            case T:
                return new Complex[] {
                        new Complex(1,0),
                        new Complex(0,0),
                        new Complex(0,0),
                        new Complex((float)(1 / Math.sqrt(2)),(float)(1 / Math.sqrt(2)))
                };
            case I:
                return new Complex[] {
                        new Complex(1,0),
                        new Complex(0,0),
                        new Complex(0,0),
                        new Complex(1,0)
                };
            default:
                return new Complex[0];
        }
    }

    private Complex[] phaseShiftGateData(float phi) {
        return new Complex[] {
                new Complex(1,0),
                new Complex(0,0),
                new Complex(0,0),
                new Complex(0,phi).exp()
        };
    }
}
