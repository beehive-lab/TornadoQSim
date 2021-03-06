package uk.ac.manchester.tornado.qsim.simulator.unitary;

import uk.ac.manchester.tornado.api.TaskSchedule;
import uk.ac.manchester.tornado.qsim.circuit.Circuit;
import uk.ac.manchester.tornado.qsim.circuit.State;
import uk.ac.manchester.tornado.qsim.circuit.Step;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;
import uk.ac.manchester.tornado.qsim.simulator.Simulator;

import java.util.List;
import java.util.ListIterator;

public class UnitarySimulatorAccelerated implements Simulator {
    private final UnitaryDataProvider dataProvider;

    /**
     * Constructs a unitary matrix simulator.
     */
    public UnitarySimulatorAccelerated() {
        dataProvider = new UnitaryDataProvider(false);
    }


    @Override
    public State simulateFullState(Circuit circuit) {
        if (circuit == null)
            throw new IllegalArgumentException("Invalid circuit supplied (NULL).");

        List<Step> steps = circuit.getSteps();
        ListIterator<Step> iterator = steps.listIterator(steps.size());

        ComplexTensor unitaryA, unitaryB;
        int scheduleId = 0;

        unitaryA = prepareStepUnitary(scheduleId, circuit.qubitCount(), iterator.previous());
        while (iterator.hasPrevious()) {
            scheduleId++;
            unitaryB = prepareStepUnitary(scheduleId, circuit.qubitCount(), iterator.previous());
            unitaryA = matrixMultiplication(scheduleId, unitaryA, unitaryB);
        }

        State resultState = new State(circuit.qubitCount());
        resultState.setStateVector(matrixVectorMultiplication(unitaryA, resultState.getStateVector()));

        return resultState;
    }

    @Override
    public int simulateAndCollapse(Circuit circuit) { return simulateFullState(circuit).collapse(); }

    private ComplexTensor prepareStepUnitary(int scheduleId, int noQubits, Step step) {
        List<ComplexTensor> stepOperationData = dataProvider.getStepOperationData(noQubits, step);
        if (stepOperationData.size() == 1)
            return stepOperationData.get(0);

        ListIterator<ComplexTensor> iterator = stepOperationData.listIterator(stepOperationData.size());

        int count = 0;

        float[] realA, realB, realC = null;
        float[] imagA, imagB, imagC = null;
        int rowsA, rowsB, rowsC = 0;
        TaskSchedule taskSchedule = new TaskSchedule("stepUnitary" + scheduleId);

        ComplexTensor stepUnitary = iterator.previous();
        rowsA = stepUnitary.shape()[0];
        realA = stepUnitary.getRawRealData();
        imagA = stepUnitary.getRawImagData();
        taskSchedule.streamIn(realA, imagA);

        while (iterator.hasPrevious()) {
            stepUnitary = iterator.previous();
            rowsB = stepUnitary.shape()[0];
            realB = stepUnitary.getRawRealData();
            imagB = stepUnitary.getRawImagData();

            if (count % 2 == 0) {
                rowsC = rowsA * rowsB;
                realC = new float[rowsC * rowsC];
                imagC = new float[rowsC * rowsC];

                taskSchedule.streamIn(realB, imagB);
                taskSchedule.task("stepUnitaryTask" + count, UnitaryOperand::kroneckerProduct,
                        realA, imagA, rowsA, rowsA,
                        realB, imagB, rowsB, rowsB,
                        realC, imagC);
            }
            else {
                rowsA = rowsC * rowsB;
                realA = new float[rowsA * rowsA];
                imagA = new float[rowsA * rowsA];

                taskSchedule.streamIn(realB, imagB);
                taskSchedule.task("stepUnitaryTask" + count, UnitaryOperand::kroneckerProduct,
                        realC, imagC, rowsC, rowsC,
                        realB, imagB, rowsB, rowsB,
                        realA, imagA);
            }
            count++;
        }

        if (count % 2 == 0) {
            taskSchedule.streamOut(realA, imagA);
            taskSchedule.execute();
            return new ComplexTensor(realA, imagA, rowsA, rowsA);
        }
        else {
            taskSchedule.streamOut(realC, imagC);
            taskSchedule.execute();
            return new ComplexTensor(realC, imagC, rowsC, rowsC);
        }
    }

    private ComplexTensor matrixMultiplication(int scheduleId, ComplexTensor a, ComplexTensor b) {
        int resultRows = a.shape()[0];
        int resultCols = b.shape()[1];
        float[] resultReal = new float[resultRows * resultCols];
        float[] resultImag = new float[resultRows * resultCols];
        new TaskSchedule("stepMultiplication" + scheduleId)
                .streamIn(a.getRawRealData(), a.getRawImagData(), b.getRawRealData(), b.getRawImagData())
                .task("stepMultiplicationTask", UnitaryOperand::matrixProduct,
                        a.getRawRealData(), a.getRawImagData(), a.shape()[0], a.shape()[1],
                        b.getRawRealData(), b.getRawImagData(), b.shape()[1],
                        resultReal, resultImag)
                .streamOut(resultReal, resultImag)
                .execute();
        return new ComplexTensor(resultReal, resultImag, resultRows, resultCols);
    }

    private ComplexTensor matrixVectorMultiplication(ComplexTensor matrix, ComplexTensor vector) {
        float[] resultReal = new float[vector.size()];
        float[] resultImag = new float[vector.size()];
        new TaskSchedule("stateVectorMultiplication")
                .streamIn(matrix.getRawRealData(), matrix.getRawImagData(),
                        vector.getRawRealData(), vector.getRawImagData())
                .task("stateVectorMultiplicationTask", UnitaryOperand::matrixVectorProduct,
                        matrix.getRawRealData(), matrix.getRawImagData(), matrix.shape()[0], matrix.shape()[1],
                        vector.getRawRealData(), vector.getRawImagData(),
                        resultReal, resultImag)
                .streamOut(resultReal, resultImag)
                .execute();
        return new ComplexTensor(resultReal, resultImag, vector.size());
    }

}
