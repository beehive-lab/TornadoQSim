package uk.ac.manchester.tornado.qsim.simulator.unitary;

import uk.ac.manchester.tornado.api.TaskSchedule;
import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.circuit.State;
import uk.ac.manchester.tornado.qsim.circuit.Step;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;

import java.util.List;
import java.util.ListIterator;

/**
 * Represents a quantum circuit simulator that composes a unitary matrix for
 * each step of the circuit in order to simulated the final state. The
 * simulation process is accelerated on heterogeneous hardware via TornadoVM.
 * This simulation process follows the standard mathematical model of quantum
 * computation.
 * 
 * @author Ales Kubicek
 */
public class UnitarySimulatorAccelerated implements Simulator {
    private final UnitaryDataProvider dataProvider;

    private TaskSchedule stepMulSchedule,stepVectorSchedule;

    private float[] stepAReal,stepAImag,stepBReal,stepBImag,stepResultReal,stepResultImag;
    private State finalState;
    private final int unitaryDimension,noQubits;

    private long startMul,endMul;
    private int numOfInvocationsOfMultiplication;
    private long totalElapsedTimeOfMultiplication = 0;

    private long startVec,endVec;
    private int numOfInvocationsOfVector;
    private long totalElapsedTimeOfVector = 0;

    /**
     * Constructs a unitary matrix simulator.
     */
    public UnitarySimulatorAccelerated(int noQubits) {
        dataProvider = new UnitaryDataProvider(false);
        this.noQubits = noQubits;
        unitaryDimension = (int) Math.pow(2, noQubits);
        prepareTaskSchedules();
    }

    @Override
    public State simulateFullState(Circuit circuit) {
        numOfInvocationsOfMultiplication = 0;
        totalElapsedTimeOfMultiplication = 0;
        numOfInvocationsOfVector = 0;
        totalElapsedTimeOfVector = 0;
        if (circuit == null)
            throw new IllegalArgumentException("Invalid circuit supplied (NULL).");

        List<Step> steps = circuit.getSteps();
        ListIterator<Step> iterator = steps.listIterator(steps.size());

        startMul = System.nanoTime();
        prepareStepUnitary(circuit.qubitCount(), iterator.previous(), stepResultReal, stepResultImag);
        while (iterator.hasPrevious()) {
            numOfInvocationsOfMultiplication++;
            prepareStepUnitary(circuit.qubitCount(), iterator.previous(), stepBReal, stepBImag);
            matrixMultiplication();
        }
        endMul = System.nanoTime();

        numOfInvocationsOfVector++;
        startVec = System.nanoTime();
        stepVectorSchedule.execute();
        endVec = System.nanoTime();
        totalElapsedTimeOfMultiplication += (endMul - startMul);
        totalElapsedTimeOfVector += (endVec - startVec);

        return finalState;
    }

    @Override
    public int simulateAndCollapse(Circuit circuit) {
        return simulateFullState(circuit).collapse();
    }

    public long getTimeOfVector() {
        return totalElapsedTimeOfVector;
    }

    public long getNumOfInvocationsOfVector() {
        return numOfInvocationsOfVector;
    }

    public long getTimeOfMultiplication() {
        return totalElapsedTimeOfMultiplication;
    }

    public long getNumOfInvocationsOfMultiplication() {
        return numOfInvocationsOfMultiplication;
    }

    private void prepareTaskSchedules() {
        int unitarySize = unitaryDimension * unitaryDimension;

        // Complex matrix multiplication
        stepAReal = new float[unitarySize];
        stepAImag = new float[unitarySize];
        stepBReal = new float[unitarySize];
        stepBImag = new float[unitarySize];
        stepResultReal = new float[unitarySize];
        stepResultImag = new float[unitarySize];

        stepMulSchedule = new TaskSchedule("stepMul").streamIn(stepAReal, stepAImag, stepBReal, stepBImag)
                .task("stepMulTask", UnitaryOperand::matrixProduct, stepAReal, stepAImag, unitaryDimension, unitaryDimension, stepBReal, stepBImag, unitaryDimension, stepResultReal, stepResultImag)
                .streamOut(stepResultReal, stepResultImag);

        // Application of complex unitary matrix to final state vector
        ComplexTensor initVector = new State(noQubits).getStateVector();
        finalState = new State(noQubits);

        stepVectorSchedule = new TaskSchedule("stepVector").streamIn(stepResultReal, stepResultImag, initVector.getRawRealData(), initVector.getRawImagData())
                .task("stepVectorTask", UnitaryOperand::matrixVectorProduct, stepResultReal, stepResultImag, unitaryDimension, unitaryDimension, initVector.getRawRealData(),
                        initVector.getRawImagData(), finalState.getStateVector().getRawRealData(), finalState.getStateVector().getRawImagData())
                .streamOut(finalState.getStateVector().getRawRealData(), finalState.getStateVector().getRawImagData());
    }

    private void prepareStepUnitary(int noQubits, Step step, float[] resultReal, float[] resultImag) {
        List<ComplexTensor> stepOperationData = dataProvider.getStepOperationData(noQubits, step);
        ListIterator<ComplexTensor> iterator = stepOperationData.listIterator(stepOperationData.size());
        ComplexTensor stepUnitary = iterator.previous();

        if (!iterator.hasPrevious()) {
            System.arraycopy(stepUnitary.getRawRealData(), 0, resultReal, 0, resultReal.length);
            System.arraycopy(stepUnitary.getRawImagData(), 0, resultImag, 0, resultImag.length);
            return;
        }

        while (iterator.hasPrevious() && iterator.previousIndex() > 0)
            stepUnitary = kroneckerProduct(stepUnitary, iterator.previous());

        ComplexTensor a = stepUnitary;
        ComplexTensor b = iterator.previous();
        UnitaryOperand.kroneckerProduct(a.getRawRealData(), a.getRawImagData(), a.shape()[0], a.shape()[1], b.getRawRealData(), b.getRawImagData(), b.shape()[0], b.shape()[1], resultReal, resultImag);
    }

    private ComplexTensor kroneckerProduct(ComplexTensor a, ComplexTensor b) {
        int resultRows = a.shape()[0] * b.shape()[0];
        int resultCols = a.shape()[1] * b.shape()[1];
        ComplexTensor result = new ComplexTensor(resultRows, resultCols);
        UnitaryOperand.kroneckerProduct(a.getRawRealData(), a.getRawImagData(), a.shape()[0], a.shape()[1], b.getRawRealData(), b.getRawImagData(), b.shape()[0], b.shape()[1], result.getRawRealData(),
                result.getRawImagData());
        return result;
    }

    private void matrixMultiplication() {
        // Option A - swap references (preferred - TornadoVM issue)
        /*
         * stepMulSchedule.updateReference(stepAReal, stepResultReal);
         * stepMulSchedule.updateReference(stepAImag, stepResultImag);
         * stepMulSchedule.updateReference(stepResultReal, stepAReal);
         * stepMulSchedule.updateReference(stepResultImag, stepAImag);
         * 
         * ComplexTensor result = new ComplexTensor(stepResultReal, stepResultImag,
         * unitaryDimension, unitaryDimension);
         * 
         * stepResultReal = stepAReal; stepResultImag = stepAImag; stepAReal =
         * result.getRawRealData(); stepAImag = result.getRawImagData();
         */

        // Option B - swap array content
        System.arraycopy(stepResultReal, 0, stepAReal, 0, stepAReal.length);
        System.arraycopy(stepResultImag, 0, stepAImag, 0, stepAImag.length);

        stepMulSchedule.execute();
    }

}
