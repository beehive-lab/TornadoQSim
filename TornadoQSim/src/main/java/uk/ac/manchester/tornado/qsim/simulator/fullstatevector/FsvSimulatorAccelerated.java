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
        applyGateSchedule = buildJitTaskSchedule(state, gate);
        applyGateSchedule.execute();
    }

    private TaskSchedule buildJitTaskSchedule(State state, Gate gate) {
        int halfRows = state.size() / 2;
        ComplexTensor gateData = dataProvider.getOperationData(gate);
        // TODO: arraycopy from ComplexTensor raw data
        float[] gateRealTmp = new float[] { //
                gateData.getElement(0, 0).real(), //
                gateData.getElement(0, 1).real(), //
                gateData.getElement(1, 0).real(), //
                gateData.getElement(1, 1).real(), //
        };
        float[] gateImagTmp = new float[] { //
                gateData.getElement(0, 0).imag(), //
                gateData.getElement(0, 1).imag(), //
                gateData.getElement(1, 0).imag(), //
                gateData.getElement(1, 1).imag(), //
        };

        // @formatter:off
        TaskSchedule taskSchedule = new TaskSchedule("applyGate")
                .streamIn(state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData(), gateRealTmp, gateImagTmp)
                .task("applyGateTask", FsvOperand::applyGate, gate.targetQubit(), state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData(), halfRows, gateRealTmp, gateImagTmp)
                .streamOut(state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData());
        // @formatter:on
        return taskSchedule;
    }

    private TaskSchedule buildJitTaskSchedule(State state, ControlGate gate) {
        int halfRows = state.size() / 2;
        ComplexTensor gateData = dataProvider.getOperationData(gate);
        // TODO: arraycopy from ComplexTensor raw data
        float[] gateRealTmp = new float[] { //
                gateData.getElement(0, 0).real(), //
                gateData.getElement(0, 1).real(), //
                gateData.getElement(1, 0).real(), //
                gateData.getElement(1, 1).real(), //
        };
        float[] gateImagTmp = new float[] { //
                gateData.getElement(0, 0).imag(), //
                gateData.getElement(0, 1).imag(), //
                gateData.getElement(1, 0).imag(), //
                gateData.getElement(1, 1).imag(), //
        };

        // @formatter:off
        TaskSchedule taskSchedule = new TaskSchedule("applyControlGate")
                .streamIn(state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData(), gateRealTmp, gateImagTmp)
                .task("applyControlGateTask", FsvOperand::applyControlGate, gate.targetQubit(), gate.controlQubit(), state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData(), halfRows, gateRealTmp, gateImagTmp)
                .streamOut(state.getStateVector().getRawRealData(), state.getStateVector().getRawImagData());
        // @formatter:on
        return taskSchedule;
    }

    private void applyControlGate(State state, ControlGate controlGate) {
        applyControlGateSchedule = buildJitTaskSchedule(state, controlGate);
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