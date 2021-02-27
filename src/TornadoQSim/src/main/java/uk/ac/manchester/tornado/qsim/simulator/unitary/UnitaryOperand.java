package uk.ac.manchester.tornado.qsim.simulator.unitary;

import uk.ac.manchester.tornado.api.annotations.Parallel;

/**
 * Provides operands used in the unitary simulator that can be accelerated with TornadoVM.
 * @author AlesKubicek
 */
public class UnitaryOperand {

    /**
     * Performs a complex matrix multiplication on the supplied complex matrices (decomposed to primitive type arrays).
     * It is assumed that the supplied matrices are valid for the operation (colsA = rowsB).
     * @param realA flattened real parts of the complex matrix A.
     * @param imagA flattened imaginary parts of the complex matrix A.
     * @param rowsA number of rows in the complex matrix A.
     * @param colsA number of columns in the complex matrix A.
     * @param realB flattened real parts of the complex matrix B.
     * @param imagB flattened imaginary parts of the complex matrix B.
     * @param colsB number of columns in the complex matrix B.
     * @param realC flattened real parts of the result complex matrix C (correct size must be allocated).
     * @param imagC flattened imaginary parts of the result complex matrix C (correct size must be allocated).
     */
    protected static void matrixProduct(float[] realA, float[] imagA, final int rowsA, final int colsA,
                                        float[] realB, float[] imagB, final int colsB,
                                        float[] realC, float[] imagC) {
        for (@Parallel int i = 0; i < rowsA; i++) {
            for (@Parallel int j = 0; j < colsB; j++) {
                int indexC = (i * rowsA) + j;
                // Note: rowsB = colsA
                for (int k = 0; k < colsA; k++) {
                    int indexA = (i * colsA) + k;
                    int indexB = (k * colsB) + j;
                    realC[indexC] += (realA[indexA] * realB[indexB]) - (imagA[indexA] * imagB[indexB]);
                    imagC[indexC] += (realA[indexA] * imagB[indexB]) + (imagA[indexA] * realB[indexB]);
                }
            }
        }
    }

    /**
     * Performs a complex multiplication with a transpose of the supplied row vector.
     * It is assumed that the supplied vector is valid for the operation (colsA = sizeB).
     * @param realA flattened real parts of the complex matrix A.
     * @param imagA flattened imaginary parts of the complex matrix A.
     * @param rowsA number of rows in the complex matrix A.
     * @param colsA number of columns in the complex matrix A.
     * @param realB flattend real parts of the row vector B.
     * @param imagB flattend imaginary parts of the row vector B.
     * @param realC flattened real parts of the result row vector C (correct size must be allocated).
     * @param imagC flattened imaginary parts of the result row vector C (correct size must be allocated).
     */
    protected static void matrixVectorProduct(float[] realA, float[] imagA, final int rowsA, final int colsA,
                                              float[] realB, float[] imagB,
                                              float[] realC, float[] imagC) {
        for (@Parallel int i = 0; i < rowsA; i++) {
            for (@Parallel int j = 0; j < colsA; j++) {
                int indexA = (i * colsA) + j;
                int indexB = j;
                int indexC = i;
                realC[indexC] += (realA[indexA] * realB[indexB]) - (imagA[indexA] * imagB[indexB]);
                imagC[indexC] += (realA[indexA] * imagB[indexB]) + (imagA[indexA] * realB[indexB]);
            }
        }
    }

    /**
     * Performs Kronecker product operation on the supplied complex matrices (decomposed to primitive type arrays).
     * @param realA flattened real parts of the complex matrix A.
     * @param imagA flattened imaginary parts of the complex matrix A.
     * @param rowsA number of rows in the complex matrix A.
     * @param colsA number of columns in the complex matrix A.
     * @param realB flattened real parts of the complex matrix B.
     * @param imagB flattened imaginary parts of the complex matrix B.
     * @param rowsB number of rows in the complex matrix B.
     * @param colsB number of columns in the complex matrix B.
     * @param realC flattened real parts of the result complex matrix C (correct size must be allocated).
     * @param imagC flattened imaginary parts of the result complex matrix C (correct size must be allocated).
     */
    protected static void kroneckerProduct(float[] realA, float[] imagA, final int rowsA, final int colsA,
                                           float[] realB, float[] imagB, final int rowsB, final int colsB,
                                           float[] realC, float[] imagC) {
        final int colsC = colsA * colsB;
        // For every element of A
        for (@Parallel int ia = 0; ia < rowsA; ia++) {
            for (@Parallel int ja = 0; ja < colsA; ja++) {
                int indexA = (ia * colsA) + ja;
                // For every element of B
                for (@Parallel int ib = 0; ib < rowsB; ib++) {
                    for (int jb = 0; jb < colsB; jb++) {
                        int indexB = (ib * colsB) + jb;
                        int indexC = (((ia * rowsB) + ib) * colsC) + ((ja * colsB) + jb);
                        realC[indexC] = (realA[indexA] * realB[indexB]) - (imagA[indexA] * imagB[indexB]);
                        imagC[indexC] = (realA[indexA] * imagB[indexB]) + (imagA[indexA] * realB[indexB]);
                    }
                }
            }
        }
    }

}
