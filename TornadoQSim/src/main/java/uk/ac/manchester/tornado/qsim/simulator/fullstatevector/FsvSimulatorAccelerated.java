package uk.ac.manchester.tornado.qsim.simulator.fullstatevector;

import uk.ac.manchester.tornado.api.TaskSchedule;
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
 * iterating over the full state vector. The simulation process is accelerated
 * on heterogeneous hardware via TornadoVM. This simulation process follows the
 * full state vector / wavefunction simulation model of quantum computation.
 * 
 * @author Ales Kubicek
 */
public class FsvSimulatorAccelerated implements Simulator {
    private final FsvDataProvider dataProvider;

    private TaskSchedule applyGateSchedule;
    private TaskSchedule applyControlGateSchedule;

    private int[] targetQubit;
    private int[] controlQubit;
    private float[] gateReal;
    private float[] gateImag;
    private float[] stateReal;
    private float[] stateImag;
    private float[] stateRealControl;
    private float[] stateImagControl;

    private long startGate,endGate;
    private long startArrayCopyForGate,endArrayCopyForGate;
    private long totalElapsedTimeOfArrayCopyForGate = 0;
    private long totalElapsedTimeOfGate = 0;
    private int numOfInvocationsOfGate;

    private long startControlGate,endControlGate;
    private long startArrayCopyForControlGate,endArrayCopyForControlGate;
    private long totalElapsedTimeOfArrayCopyForControlGate = 0;
    private long totalElapsedTimeOfControlGate = 0;
    private int numOfInvocationsOfControlGate;

    /**
     * Constructs a full state vector simulator.
     */
    public FsvSimulatorAccelerated(int noQubits) {
        dataProvider = new FsvDataProvider();
        targetQubit = new int[1];
        controlQubit = new int[1];
    }

    private void initializeStateArrays(State state) {
        if (stateReal == null) {
            stateReal = new float[state.getStateVector().getRawRealData().length];
        }
        if (stateImag == null) {
            stateImag = new float[state.getStateVector().getRawImagData().length];
        }
        if (stateRealControl == null) {
            stateRealControl = new float[state.getStateVector().getRawRealData().length];
        }
        if (stateImagControl == null) {
            stateImagControl = new float[state.getStateVector().getRawImagData().length];
        }
    }

    @Override
    public State simulateFullState(Circuit circuit) {
        resetCounters();
        if (circuit == null)
            throw new IllegalArgumentException("Invalid circuit supplied (NULL).");

        State resultState = new State(circuit.qubitCount());
        List<Step> steps = circuit.getSteps();

        initializeStateArrays(resultState);

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
        totalElapsedTimeOfArrayCopyForGate = 0;
        numOfInvocationsOfControlGate = 0;
        totalElapsedTimeOfControlGate = 0;
        totalElapsedTimeOfArrayCopyForControlGate = 0;
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

    public long getArrayCopyTimeOfGate() {
        return totalElapsedTimeOfArrayCopyForGate;
    }

    public long getTimeOfControlGate() {
        return totalElapsedTimeOfControlGate;
    }

    public long getNumOfInvocationsOfControlGate() {
        return numOfInvocationsOfControlGate;
    }

    public long getArrayCopyTimeOfControlGate() {
        return totalElapsedTimeOfArrayCopyForControlGate;
    }

    private void applyGate(State state, Gate gate) {
        // totalElapsedTimeOfArrayCopyForGate = 0;
        startArrayCopyForGate = System.nanoTime();
        updateInputDataOfTaskSchedule(state, gate);
        endArrayCopyForGate = System.nanoTime();
        totalElapsedTimeOfArrayCopyForGate += (endArrayCopyForGate - startArrayCopyForGate);
        applyGateSchedule.execute();
        startArrayCopyForControlGate = System.nanoTime();
        updateOutputDataOfGate(state, gate);
        endArrayCopyForControlGate = System.nanoTime();
        totalElapsedTimeOfArrayCopyForGate += (endArrayCopyForGate - startArrayCopyForGate);
    }

    private void updateInputDataOfTaskSchedule(State state, Gate gate) {
        int halfRows = state.size() / 2;
        ComplexTensor gateData = dataProvider.getOperationData(gate);

        if (applyGateSchedule == null) {
            targetQubit[0] = gate.targetQubit()[0];
            System.arraycopy(state.getStateVector().getRawRealData(), 0, stateReal, 0, state.getStateVector().getRawRealData().length);
            System.arraycopy(state.getStateVector().getRawImagData(), 0, stateImag, 0, state.getStateVector().getRawImagData().length);
            if (gateReal == null) {
                gateReal = new float[gateData.size()];
            }
            if (gateImag == null) {
                gateImag = new float[gateData.size()];
            }
            System.arraycopy(gateData.getRawRealData(), 0, gateReal, 0, gateData.getRawRealData().length);
            System.arraycopy(gateData.getRawImagData(), 0, gateImag, 0, gateData.getRawRealData().length);

            // @formatter:off
            applyGateSchedule = new TaskSchedule("applyGate")
                    .streamIn(targetQubit, stateReal, stateImag, gateReal, gateImag)
                    .task("applyGateTask", FsvOperand::applyGate, targetQubit, stateReal, stateImag, halfRows, gateReal, gateImag)
                    .streamOut(stateReal, stateImag);
            // @formatter:on
        } else {
            targetQubit[0] = gate.targetQubit()[0];
            System.arraycopy(state.getStateVector().getRawRealData(), 0, stateReal, 0, state.getStateVector().getRawRealData().length);
            System.arraycopy(state.getStateVector().getRawImagData(), 0, stateImag, 0, state.getStateVector().getRawImagData().length);
            System.arraycopy(gateData.getRawRealData(), 0, gateReal, 0, gateData.getRawRealData().length);
            System.arraycopy(gateData.getRawImagData(), 0, gateImag, 0, gateData.getRawRealData().length);
        }
        applyGateSchedule.updateReference(targetQubit, gate.targetQubit());
    }

    private void updateOutputDataOfGate(State state, Gate gate) {
        System.arraycopy(stateReal, 0, state.getStateVector().getRawRealData(), 0, stateReal.length);
        System.arraycopy(stateImag, 0, state.getStateVector().getRawImagData(), 0, stateImag.length);
        applyGateSchedule.updateReference(gate.targetQubit(), targetQubit);
    }

    private void applyControlGate(State state, ControlGate controlGate) {
        // totalElapsedTimeOfArrayCopyForControlGate = 0;
        startArrayCopyForControlGate = System.nanoTime();
        updateInputDataOfTaskSchedule(state, controlGate);
        endArrayCopyForControlGate = System.nanoTime();
        totalElapsedTimeOfArrayCopyForControlGate += (endArrayCopyForControlGate - startArrayCopyForControlGate);
        applyControlGateSchedule.execute();
        startArrayCopyForControlGate = System.nanoTime();
        updateOutputDataOfControlGate(state);
        endArrayCopyForControlGate = System.nanoTime();
        totalElapsedTimeOfArrayCopyForControlGate += (endArrayCopyForControlGate - startArrayCopyForControlGate);
    }

    private void updateInputDataOfTaskSchedule(State state, ControlGate gate) {
        int halfRows = state.size() / 2;
        ComplexTensor gateData = dataProvider.getOperationData(gate);

        if (applyControlGateSchedule == null) {
            targetQubit[0] = gate.targetQubit()[0];
            controlQubit[0] = gate.controlQubit()[0];
            System.arraycopy(state.getStateVector().getRawRealData(), 0, stateRealControl, 0, state.getStateVector().getRawRealData().length);
            System.arraycopy(state.getStateVector().getRawImagData(), 0, stateImagControl, 0, state.getStateVector().getRawImagData().length);
            if (gateReal == null) {
                gateReal = new float[gateData.size()];
            }
            if (gateImag == null) {
                gateImag = new float[gateData.size()];
            }
            System.arraycopy(gateData.getRawRealData(), 0, gateReal, 0, gateData.getRawRealData().length);
            System.arraycopy(gateData.getRawImagData(), 0, gateImag, 0, gateData.getRawRealData().length);

            // @formatter:off
            applyControlGateSchedule = new TaskSchedule("applyControlGate")
                    .streamIn(targetQubit, controlQubit, stateRealControl, stateImagControl, gateReal, gateImag)
                    .task("applyControlGateTask", FsvOperand::applyControlGate, targetQubit, controlQubit, stateRealControl, stateImagControl, halfRows, gateReal, gateImag)
                    .streamOut(stateRealControl, stateImagControl);
            // @formatter:on
        } else {
            targetQubit[0] = gate.targetQubit()[0];
            controlQubit[0] = gate.controlQubit()[0];
            System.arraycopy(state.getStateVector().getRawRealData(), 0, stateRealControl, 0, state.getStateVector().getRawRealData().length);
            System.arraycopy(state.getStateVector().getRawImagData(), 0, stateImagControl, 0, state.getStateVector().getRawImagData().length);
            System.arraycopy(gateData.getRawRealData(), 0, gateReal, 0, gateData.getRawRealData().length);
            System.arraycopy(gateData.getRawImagData(), 0, gateImag, 0, gateData.getRawRealData().length);
        }
    }

    private void updateOutputDataOfControlGate(State state) {
        System.arraycopy(stateRealControl, 0, state.getStateVector().getRawRealData(), 0, stateRealControl.length);
        System.arraycopy(stateImagControl, 0, state.getStateVector().getRawImagData(), 0, stateImagControl.length);
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