package uk.ac.manchester.tornado.qsim.simulator.unitary;

import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.circuit.State;
import uk.ac.manchester.tornado.qsim.circuit.Step;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;

import java.util.List;
import java.util.ListIterator;

/**
 * Represents a quantum circuit simulator that composes a unitary matrix for each step of the circuit in order
 * to simulated the final state. The simulation process is not accelerated on any heterogeneous hardware. This
 * simulation process follows the standard mathematical model of quantum computation.
 * @author Ales Kubicek
 */
public class UnitarySimulatorStandard implements Simulator {
    private final UnitaryDataProvider dataProvider;

    /**
     * Constructs a unitary matrix simulator.
     */
    public UnitarySimulatorStandard() {
        dataProvider = new UnitaryDataProvider(false);
    }

    @Override
    public State simulateFullState(Circuit circuit) {
        if (circuit == null)
            throw new IllegalArgumentException("Invalid circuit supplied (NULL).");

        List<Step> steps = circuit.getSteps();
        ListIterator<Step> iterator = steps.listIterator(steps.size());

        ComplexTensor unitaryA, unitaryB;

        unitaryA = prepareStepUnitary(circuit.qubitCount(), iterator.previous());
        while (iterator.hasPrevious()) {
            unitaryB = prepareStepUnitary(circuit.qubitCount(), iterator.previous());
            unitaryA = matrixMultiplication(unitaryA, unitaryB);
        }

        State resultState = new State(circuit.qubitCount());
        resultState.setStateVector(matrixVectorMultiplication(unitaryA, resultState.getStateVector()));

        return resultState;
    }

    @Override
    public int simulateAndCollapse(Circuit circuit) {
        return simulateFullState(circuit).collapse();
    }

    private ComplexTensor prepareStepUnitary(int noQubits, Step step) {
        List<ComplexTensor> stepOperationData = dataProvider.getStepOperationData(noQubits, step);
        ListIterator<ComplexTensor> iterator = stepOperationData.listIterator(stepOperationData.size());
        ComplexTensor stepUnitary = iterator.previous();
        while (iterator.hasPrevious())
            stepUnitary = kroneckerProduct(stepUnitary, iterator.previous());
        return stepUnitary;
    }

    private ComplexTensor matrixMultiplication(ComplexTensor a, ComplexTensor b) {
        int resultRows = a.shape()[0];
        int resultCols = b.shape()[1];
        ComplexTensor result = new ComplexTensor(resultRows,resultCols);
        UnitaryOperand.matrixProduct(a.getRawRealData(), a.getRawImagData(), a.shape()[0], a.shape()[1],
                b.getRawRealData(), b.getRawImagData(), b.shape()[1],
                result.getRawRealData(), result.getRawImagData());
        return result;
    }

    private ComplexTensor kroneckerProduct(ComplexTensor a, ComplexTensor b) {
        int resultRows = a.shape()[0] * b.shape()[0];
        int resultCols = a.shape()[1] * b.shape()[1];
        ComplexTensor result = new ComplexTensor(resultRows,resultCols);
        UnitaryOperand.kroneckerProduct(a.getRawRealData(), a.getRawImagData(), a.shape()[0], a.shape()[1],
                b.getRawRealData(), b.getRawImagData(), b.shape()[0], b.shape()[1],
                result.getRawRealData(), result.getRawImagData());
        return result;
    }

    private ComplexTensor matrixVectorMultiplication(ComplexTensor matrix, ComplexTensor vector) {
        ComplexTensor result = new ComplexTensor(vector.size());
        UnitaryOperand.matrixVectorProduct(matrix.getRawRealData(), matrix.getRawImagData(), matrix.shape()[0],
                matrix.shape()[1], vector.getRawRealData(), vector.getRawImagData(),
                result.getRawRealData(), result.getRawImagData());
        return result;
    }

}
