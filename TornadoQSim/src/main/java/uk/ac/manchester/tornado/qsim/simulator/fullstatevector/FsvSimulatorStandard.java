package uk.ac.manchester.tornado.qsim.simulator.fullstatevector;

import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.circuit.State;
import uk.ac.manchester.tornado.qsim.circuit.Step;
import uk.ac.manchester.tornado.qsim.circuit.operation.ControlGate;
import uk.ac.manchester.tornado.qsim.circuit.operation.Function;
import uk.ac.manchester.tornado.qsim.circuit.operation.Gate;
import uk.ac.manchester.tornado.qsim.circuit.operation.Operation;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;

import java.util.List;

/**
 * Represents a quantum circuit simulator that applies each quantum gate by
 * iterating over the full state vector. The simulation process is not
 * accelerated on any heterogeneous hardware. This simulation process follows
 * the full state vector / wavefunction simulation model of quantum computation.
 * 
 * @author Ales Kubicek
 */
public class FsvSimulatorStandard implements Simulator {
    private final FsvDataProvider dataProvider;

    /**
     * Constructs a full state vector simulator.
     */
    public FsvSimulatorStandard() {
        dataProvider = new FsvDataProvider();
    }

    private long startGate,endGate;
    private long totalElapsedTimeOfGate = 0;
    private int numOfInvocationsOfGate;

    private long startControlGate,endControlGate;
    private long totalElapsedTimeOfControlGate = 0;
    private int numOfInvocationsOfControlGate;

    @Override
    public State simulateFullState(Circuit circuit) {
        resetCounters();
        if (circuit == null)
            throw new IllegalArgumentException("Invalid circuit supplied (NULL).");

        State resultState = new State(circuit.qubitCount());
        List<Step> steps = circuit.getSteps();

        for (Step step : steps) {
            List<Operation> operations = dataProvider.getStepOperations(circuit.qubitCount(), step);
            for (Operation operation : operations) {
                switch (operation.operationType()) {
                    case Gate:
                        numOfInvocationsOfGate++;
                        startGate = System.nanoTime();
                        applyGate(resultState, (Gate) operation);
                        endGate = System.nanoTime();
                        totalElapsedTimeOfGate += (endGate - startGate);
                        break;
                    case ControlGate:
                        numOfInvocationsOfControlGate++;
                        startControlGate = System.nanoTime();
                        applyControlGate(resultState, (ControlGate) operation);
                        endControlGate = System.nanoTime();
                        totalElapsedTimeOfControlGate += (endControlGate - startControlGate);
                        break;
                    case Function:
                        applyStandardFunction(resultState, (Function) operation);
                        break;
                    case CustomFunction:
                        applyCustomFunction(resultState, (Function) operation);
                        break;
                    default:
                        throw new UnsupportedOperationException("Operation type '" + operation.operationType() + "' is not supported in a full state vector simulator.");
                }
            }
        }

        return resultState;
    }

    public void resetCounters() {
        numOfInvocationsOfGate = 0;
        totalElapsedTimeOfGate = 0;
        numOfInvocationsOfControlGate = 0;
        totalElapsedTimeOfControlGate = 0;
    }

    @Override
    public int simulateAndCollapse(Circuit circuit) {
        return simulateFullState(circuit).collapse();
    }

    public long getTimeOfGate() {
        return totalElapsedTimeOfGate;
    }

    public long getNumOfInvocationsOfGate() {
        return numOfInvocationsOfGate;
    }

    public long getTimeOfControlGate() {
        return totalElapsedTimeOfControlGate;
    }

    public long getNumOfInvocationsOfControlGate() {
        return numOfInvocationsOfControlGate;
    }

    private void applyGate(State state, Gate gate) {
        ComplexTensor gateData = dataProvider.getOperationData(gate);
        float[] gateReal = new float[] { gateData.getElement(0, 0).real(), gateData.getElement(0, 1).real(), gateData.getElement(1, 0).real(), gateData.getElement(1, 1).real(), };
        float[] gateImag = new float[] { gateData.getElement(0, 0).imag(), gateData.getElement(0, 1).imag(), gateData.getElement(1, 0).imag(), gateData.getElement(1, 1).imag(), };
        FsvOperand.applyGate(gate.targetQubit(), state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData(), state.size() / 2, gateReal, gateImag);
    }

    private void applyControlGate(State state, ControlGate controlGate) {
        ComplexTensor gateData = dataProvider.getOperationData(controlGate);
        float[] gateReal = new float[] { gateData.getElement(0, 0).real(), gateData.getElement(0, 1).real(), gateData.getElement(1, 0).real(), gateData.getElement(1, 1).real(), };
        float[] gateImag = new float[] { gateData.getElement(0, 0).imag(), gateData.getElement(0, 1).imag(), gateData.getElement(1, 0).imag(), gateData.getElement(1, 1).imag(), };
        FsvOperand.applyControlGate(controlGate.targetQubit(), controlGate.controlQubit(), state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData(), state.size() / 2, gateReal,
                gateImag);
    }

    private void applyStandardFunction(State state, Function standardFunction) {
        // TODO: implement data generation for standard functions
        throw new UnsupportedOperationException("Standard functions are not yet supported.");
    }

    private void applyCustomFunction(State state, Function customFunction) {
        // TODO: implement data generation for custom functions
        throw new UnsupportedOperationException("Custom functions are not yet supported.");
    }
}
