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

import java.util.Arrays;
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
    private int[] targetControlQubit;
    private float[] gateReal;
    private float[] gateImag;
    private float[] stateReal;
    private float[] stateImag;

    /**
     * Constructs a full state vector simulator.
     */
    public FsvSimulatorAccelerated(int noQubits) {
        dataProvider = new FsvDataProvider();
    }

    @Override
    public State simulateFullState(Circuit circuit) {
        if (circuit == null)
            throw new IllegalArgumentException("Invalid circuit supplied (NULL).");

        State resultState = new State(circuit.qubitCount());
        List<Step> steps = circuit.getSteps();

        for (Step step : steps) {
            List<Operation> operations = dataProvider.getStepOperations(circuit.qubitCount(), step);
            for (Operation operation : operations) {
                switch (operation.operationType()) {
                    case Gate:
                        applyGate(resultState, (Gate) operation);
                        break;
                    case ControlGate:
                        applyControlGate(resultState, (ControlGate) operation);
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

    @Override
    public int simulateAndCollapse(Circuit circuit) {
        return simulateFullState(circuit).collapse();
    }

    private void applyGate(State state, Gate gate) {
        buildJitTaskSchedule(state, gate);
        applyGateSchedule.execute();
        System.arraycopy(stateReal, 0, state.getStateVector().getRawRealData(), 0, stateReal.length);
        System.arraycopy(stateImag, 0, state.getStateVector().getRawImagData(), 0, stateImag.length);
    }

    private void buildJitTaskSchedule(State state, Gate gate) {
        int halfRows = state.size() / 2;
        ComplexTensor gateData = dataProvider.getOperationData(gate);
        // TODO: arraycopy from ComplexTensor raw data

        // @formatter:off
        if (applyGateSchedule == null) {
            if (targetQubit == null) {
                targetQubit = new int[1];
            }
            if (stateReal == null) {
                stateReal = new float[state.getStateVector().getRawRealData().length];
            }
            if (stateImag == null) {
                stateImag = new float[state.getStateVector().getRawImagData().length];
            }
            
            targetQubit[0] = gate.targetQubit()[0];
            System.arraycopy(state.getStateVector().getRawRealData(), 0, stateReal, 0, state.getStateVector().getRawRealData().length);
            System.arraycopy(state.getStateVector().getRawImagData(), 0, stateImag, 0, state.getStateVector().getRawImagData().length);
//            stateReal = state.getStateVector().getRawRealData();
//            stateImag = state.getStateVector().getRawImagData();
            gateReal = new float[] { //
                    gateData.getElement(0, 0).real(), //
                    gateData.getElement(0, 1).real(), //
                    gateData.getElement(1, 0).real(), //
                    gateData.getElement(1, 1).real()  //
            };
            gateImag = new float[] { //
                    gateData.getElement(0, 0).imag(), //
                    gateData.getElement(0, 1).imag(), //
                    gateData.getElement(1, 0).imag(), //
                    gateData.getElement(1, 1).imag()  //
            };
            
            applyGateSchedule = new TaskSchedule("applyGate")
                    .streamIn(targetQubit, stateReal, stateImag, gateReal, gateImag)
                    .task("applyGateTask", FsvOperand::applyGate, targetQubit, stateReal, stateImag, halfRows, gateReal, gateImag)
                    .streamOut(stateReal, stateImag);
        } else {
            targetQubit[0] = gate.targetQubit()[0];
            System.arraycopy(state.getStateVector().getRawRealData(), 0, stateReal, 0, state.getStateVector().getRawRealData().length);
            System.arraycopy(state.getStateVector().getRawImagData(), 0, stateImag, 0, state.getStateVector().getRawImagData().length);
//            stateReal = state.getStateVector().getRawRealData();
//            stateImag = state.getStateVector().getRawImagData();
            gateReal[0] = gateData.getElement(0, 0).real();
            gateReal[1] = gateData.getElement(0, 1).real();
            gateReal[2] = gateData.getElement(1, 0).real();
            gateReal[3] = gateData.getElement(1, 1).real();
            gateImag[0] = gateData.getElement(0, 0).imag();
            gateImag[1] = gateData.getElement(0, 1).imag();
            gateImag[2] = gateData.getElement(1, 0).imag();
            gateImag[3] = gateData.getElement(1, 1).imag();
        }
        // @formatter:on
    }

    private void buildJitTaskSchedule(State state, ControlGate gate) {
        int halfRows = state.size() / 2;
        ComplexTensor gateData = dataProvider.getOperationData(gate);
        // TODO: arraycopy from ComplexTensor raw data

        // @formatter:off
        if (applyControlGateSchedule == null) {
            if (targetQubit == null) {
                targetQubit = new int[1];
            }
            if (targetControlQubit == null) {
                targetControlQubit = new int[1];
            }
            if (stateReal == null) {
                stateReal = new float[state.getStateVector().getRawRealData().length];
            }
            if (stateImag == null) {
                stateImag = new float[state.getStateVector().getRawImagData().length];
            }

            targetQubit[0] = gate.targetQubit()[0];
            targetControlQubit[0] = gate.controlQubit()[0];
            System.arraycopy(state.getStateVector().getRawRealData(), 0, stateReal, 0, state.getStateVector().getRawRealData().length);
            System.arraycopy(state.getStateVector().getRawImagData(), 0, stateImag, 0, state.getStateVector().getRawImagData().length);
            // stateReal = state.getStateVector().getRawRealData();
            // stateImag = state.getStateVector().getRawImagData();
            gateReal = new float[] { //
                    gateData.getElement(0, 0).real(), //
                    gateData.getElement(0, 1).real(), //
                    gateData.getElement(1, 0).real(), //
                    gateData.getElement(1, 1).real() //
            };
            gateImag = new float[] { //
                    gateData.getElement(0, 0).imag(), //
                    gateData.getElement(0, 1).imag(), //
                    gateData.getElement(1, 0).imag(), //
                    gateData.getElement(1, 1).imag() //
            };

            applyControlGateSchedule = new TaskSchedule("applyControlGate")
                    .streamIn(targetQubit, stateReal, stateImag, gateReal, gateImag)
                    .task("applyControlGateTask", FsvOperand::applyControlGate, targetQubit, gate.controlQubit(), stateReal, stateImag, halfRows, gateReal, gateImag)
                    .streamOut(stateReal, stateImag);
        } else {
            targetQubit[0] = gate.targetQubit()[0];
            targetControlQubit[0] = gate.controlQubit()[0];
            System.arraycopy(state.getStateVector().getRawRealData(), 0, stateReal, 0, state.getStateVector().getRawRealData().length);
            System.arraycopy(state.getStateVector().getRawImagData(), 0, stateImag, 0, state.getStateVector().getRawImagData().length);
            gateReal[0] = gateData.getElement(0, 0).real();
            gateReal[1] = gateData.getElement(0, 1).real();
            gateReal[2] = gateData.getElement(1, 0).real();
            gateReal[3] = gateData.getElement(1, 1).real();
            gateImag[0] = gateData.getElement(0, 0).imag();
            gateImag[1] = gateData.getElement(0, 1).imag();
            gateImag[2] = gateData.getElement(1, 0).imag();
            gateImag[3] = gateData.getElement(1, 1).imag();
        }
        // @formatter:on
    }

    private void applyControlGate(State state, ControlGate controlGate) {
        buildJitTaskSchedule(state, controlGate);
        applyControlGateSchedule.execute();
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