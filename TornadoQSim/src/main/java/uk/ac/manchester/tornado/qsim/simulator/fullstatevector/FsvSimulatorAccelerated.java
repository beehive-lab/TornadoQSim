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
 * Represents a quantum circuit simulator that applies each quantum gate by iterating over the full state vector.
 * The simulation process is accelerated on heterogeneous hardware via TornadoVM. This simulation process follows the
 * full state vector / wavefunction simulation model of quantum computation.
 * @author Ales Kubicek
 */
public class FsvSimulatorAccelerated implements Simulator {
    private final FsvDataProvider dataProvider;

    private TaskSchedule applyGateSchedule, applyControlGateSchedule;

    private final int noQubits;
    private State state;
    private int targetQubit, controlQubit;
    private float maReal, maImag, mbReal, mbImag, mcReal, mcImag, mdReal, mdImag;

    /**
     * Constructs a full state vector simulator.
     */
    public FsvSimulatorAccelerated(int noQubits) {
        dataProvider = new FsvDataProvider();
        this.noQubits = noQubits;
        prepareTaskSchedules();
    }

    @Override
    public State simulateFullState(Circuit circuit) {
        if (circuit == null)
            throw new IllegalArgumentException("Invalid circuit supplied (NULL).");

        List<Step> steps = circuit.getSteps();

        for (Step step : steps) {
            List<Operation> operations = dataProvider.getStepOperations(circuit.qubitCount(), step);
            for (Operation operation : operations) {
                switch (operation.operationType()) {
                    case Gate:
                        applyGate((Gate)operation);
                        break;
                    case ControlGate:
                        applyControlGate((ControlGate)operation);
                        break;
                    case Function:
                        applyStandardFunction((Function)operation);
                        break;
                    case CustomFunction:
                        applyCustomFunction((Function)operation);
                        break;
                    default:
                        throw new UnsupportedOperationException("Operation type '"
                                + operation.operationType()
                                + "' is not supported in a full state vector simulator.");
                }
            }
        }
        return state;
    }

    @Override
    public int simulateAndCollapse(Circuit circuit) {
        return simulateFullState(circuit).collapse();
    }

    private void prepareTaskSchedules() {
        state = new State(noQubits);
        int halfRows = state.size() / 2;

        float[] stateReal = state.getStateVector().getRawRealData();
        float[] stateImag = state.getStateVector().getRawImagData();

        applyGateSchedule = new TaskSchedule("applyGate")
                .task("applyGateTask", FsvOperand::applyGate,
                        targetQubit, stateReal, stateImag, halfRows,
                        maReal, maImag, mbReal, mbImag,
                        mcReal, mcImag, mdReal, mdImag)
                .streamOut(stateReal, stateImag);

//        applyControlGateSchedule = (TaskSchedule) new TaskSchedule("applyControlGate")
//                .task("applyControlGateTask", FsvOperand::applyControlGate,
//                        targetQubit, controlQubit,
//                        stateReal, stateImag, halfRows,
//                        maReal, maImag, mbReal, mbImag,
//                        mcReal, mcImag, mdReal, mdImag)
//                .streamOut(stateReal, stateImag);
    }

    private void applyGate(Gate gate) {
        targetQubit = gate.targetQubit();

        ComplexTensor gateData = dataProvider.getOperationData(gate);
        maReal = gateData.getElement(0, 0).real();
        maImag = gateData.getElement(0, 0).imag();
        mbReal = gateData.getElement(0, 1).real();
        mbImag = gateData.getElement(0, 1).imag();
        mcReal = gateData.getElement(1, 0).real();
        mcImag = gateData.getElement(1, 0).imag();
        mdReal = gateData.getElement(1, 1).real();
        mdImag = gateData.getElement(1, 1).imag();
        applyGateSchedule.execute();
    }

    private void applyControlGate(ControlGate controlGate) {
        targetQubit = controlGate.targetQubit();
        controlQubit = controlGate.controlQubit();

        ComplexTensor gateData = dataProvider.getOperationData(controlGate);
        maReal = gateData.getElement(0, 0).real();
        maImag = gateData.getElement(0, 0).imag();
        mbReal = gateData.getElement(0, 1).real();
        mbImag = gateData.getElement(0, 1).imag();
        mcReal = gateData.getElement(1, 0).real();
        mcImag = gateData.getElement(1, 0).imag();
        mdReal = gateData.getElement(1, 1).real();
        mdImag = gateData.getElement(1, 1).imag();
        applyControlGateSchedule.execute();
    }

    private void applyStandardFunction(Function standardFunction) {
        // TODO: implement data generation for standard functions
        throw new UnsupportedOperationException("Standard functions are not yet supported.");
    }

    private void applyCustomFunction(Function customFunction) {
        // TODO: implement data generation for custom functions
        throw new UnsupportedOperationException("Custom functions are not yet supported.");
    }
}