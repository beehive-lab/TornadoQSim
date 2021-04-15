package uk.ac.manchester.tornado.qsim.circuit.operation;

import uk.ac.manchester.tornado.qsim.circuit.operation.enums.GateType;
import uk.ac.manchester.tornado.qsim.math.Complex;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;

import java.util.HashMap;

/**
 * Provides data as complex tensor (rank 2 - matrix) for standard quantum logic gates or registered custom quantum
 * unitary functions.
 * @author Ales Kubicek
 */
public class OperationDataProvider {
    private final HashMap<String, ComplexTensor> customFunctionData;
    private final HashMap<GateType, ComplexTensor> gateData;
    private final HashMap<Float, ComplexTensor> phaseGateData;

    private static OperationDataProvider instance;

    private OperationDataProvider() {
        customFunctionData = new HashMap<>();
        gateData = new HashMap<>();
        phaseGateData = new HashMap<>();
    }

    /**
     * Gets the singleton instance.
     * @return singleton instance of the operation data provider.
     */
    public static OperationDataProvider getInstance() {
        if(instance == null)
            instance = new OperationDataProvider();
        return instance;
    }

    /**
     * Registers custom function data provided as rank 2 complex tensor.
     * Overwrites data registered with the same function name if already present.
     * @param functionName name of the custom quantum function.
     * @param data rank 2 complex tensor.
     */
    public void registerFunctionData(String functionName, ComplexTensor data) {
        if (!isFunctionNameValid(functionName))
            throw new IllegalArgumentException("Invalid function name supplied.");
        if (!isFunctionDataValid(data))
            throw new IllegalArgumentException("Invalid funciton data supplied (check definition, rank, size).");
        customFunctionData.put(functionName, data);
    }

    /**
     * Checks whether data for specific supplied function name is registered with the data provider.
     * @param functionName name of the custom quantum function.
     * @return true, if data for the supplied function name is registered.
     */
    public boolean isFunctionDataRegistered(String functionName) {
        if (!isFunctionNameValid(functionName))
            throw new IllegalArgumentException("Invalid function name supplied.");
        return customFunctionData.containsKey(functionName);
    }

    /**
     * Gets complex tensor data for the registered custom function.
     * @param functionName name of the custom quantum function.
     * @return function data.
     */
    public ComplexTensor getData(String functionName) {
        if (!isFunctionNameValid(functionName))
            throw new IllegalArgumentException("Invalid function name supplied.");
        if (!customFunctionData.containsKey(functionName))
            throw new IllegalArgumentException("Data for this custom function were not registered.");
        return customFunctionData.get(functionName);
    }

    /**
     * Gets complex tensor data for the standard quantum logic gate.
     * @param gate standard quantum logic gate.
     * @return gate data.
     */
    public ComplexTensor getData(Gate gate) {
        if (gate.type() == GateType.R) {
            if (phaseGateData.containsKey(gate.phi()))
                return phaseGateData.get(gate.phi());
            return createDataEntry(gate.type(), gate.phi());
        }
        else {
            if (gateData.containsKey(gate.type()))
                return gateData.get(gate.type());
            return createDataEntry(gate.type(), 0);
        }
    }

    private ComplexTensor createDataEntry(GateType type, float phi) {
        Complex phaseShift;
        ComplexTensor dataEntry = new ComplexTensor(2,2);
        switch (type) {
            case X:
                dataEntry.insertElement(new Complex(1,0), 0,1);
                dataEntry.insertElement(new Complex(1,0), 1,0);
                break;
            case Y:
                dataEntry.insertElement(new Complex(0,-1), 0,1);
                dataEntry.insertElement(new Complex(0,1), 1,0);
                break;
            case Z:
                phaseShift = new Complex(-1,0);
                dataEntry.insertElement(new Complex(1,0), 0,0);
                dataEntry.insertElement(phaseShift, 1,1);
                break;
            case H:
                dataEntry.insertElement(new Complex((float)(1 / Math.sqrt(2)),0), 0,0);
                dataEntry.insertElement(new Complex((float)(1 / Math.sqrt(2)),0), 0,1);
                dataEntry.insertElement(new Complex((float)(1 / Math.sqrt(2)),0), 1,0);
                dataEntry.insertElement(new Complex((float)(-1 / Math.sqrt(2)),0), 1,1);
                break;
            case S:
                phaseShift = new Complex(0,1);
                dataEntry.insertElement(new Complex(1,0), 0,0);
                dataEntry.insertElement(phaseShift, 1,1);
                break;
            case T:
                phaseShift = new Complex((float)(1 / Math.sqrt(2)),(float)(1 / Math.sqrt(2)));
                dataEntry.insertElement(new Complex(1,0), 0,0);
                dataEntry.insertElement(phaseShift, 1,1);
                break;
            case R:
                phaseShift = new Complex(0,phi).exp();
                dataEntry.insertElement(new Complex(1,0), 0,0);
                dataEntry.insertElement(phaseShift, 1,1);
                break;
            case I:
                dataEntry.insertElement(new Complex(1,0), 0,0);
                dataEntry.insertElement(new Complex(1,0), 1,1);
                break;

        }
        if (type == GateType.R)
            phaseGateData.put(phi, dataEntry);
        else
            gateData.put(type, dataEntry);
        return dataEntry;
    }

    private boolean isFunctionNameValid(String functionName) {
        return functionName != null && !functionName.equals("");
    }

    private boolean isFunctionDataValid(ComplexTensor data) {
        return data != null && data.rank() == 2 && data.size() >= 4 && data.shape()[0] == data.shape()[1];
    }
}
