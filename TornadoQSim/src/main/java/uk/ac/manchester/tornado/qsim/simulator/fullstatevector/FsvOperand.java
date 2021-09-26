package uk.ac.manchester.tornado.qsim.simulator.fullstatevector;

import uk.ac.manchester.tornado.api.annotations.Parallel;

/**
 * Provides operands used in the full state vector simulator that can be accelerated with TornadoVM.
 * @author Ales Kubicek
 */
class FsvOperand {

    /**
     * Performs application of the standard quantum gate, which is supplied as decomposed 2x2 unitary matrix.
     * The form of the matrix is [[A,B],[C,D]].
     * @param targetQubit qubit to which the gate is applied.
     * @param real flattened real parts of the full state vector.
     * @param imag flattened imaginary parts of the full state vector.
     * @param halfRows half dimension of the full state vector.
     * @param maReal real part of the component A of the unitary matrix.
     * @param maImag imaginary part of the component A of the unitary matrix.
     * @param mbReal real part of the component B of the unitary matrix.
     * @param mbImag imaginary part of the component B of the unitary matrix.
     * @param mcReal real part of the component C of the unitary matrix.
     * @param mcImag imaginary part of the component C of the unitary matrix.
     * @param mdReal real part of the component D of the unitary matrix.
     * @param mdImag imaginary part of the component D of the unitary matrix.
     */
    protected static void applyGate(int targetQubit, float[] real, float[] imag, final int halfRows,
                                    float maReal, float maImag, float mbReal, float mbImag,
                                    float mcReal, float mcImag, float mdReal, float mdImag) {
        for (@Parallel int i = 0; i < halfRows; i++) {
            int maskRight = (1 << targetQubit) - 1;
            int maskLeft = ~maskRight;

            int a = (i & maskRight) | ((i & maskLeft) << 1);
            int b = a | (1 << targetQubit);

            float valueAReal = real[a];
            float valueAImag = imag[a];
            float valueBReal = real[b];
            float valueBImag = imag[b];

            real[a] = (valueAReal * maReal - valueAImag * maImag)
                    + (valueBReal * mbReal - valueBImag * mbImag);
            imag[a] = (valueAReal * maImag + valueAImag * maReal)
                    + (valueBReal * mbImag + valueBImag * mbReal);

            real[b] = (valueAReal * mcReal - valueAImag * mcImag)
                    + (valueBReal * mdReal - valueBImag * mdImag);
            imag[b] = (valueAReal * mcImag + valueAImag * mcReal)
                    + (valueBReal * mdImag + valueBImag * mdReal);
        }
    }

    /**
     * Performs application of the standard quantum gate, which is supplied as decomposed 2x2 unitary matrix.
     * The form of the matrix is [[A,B],[C,D]].
     * @param targetQubit qubit to which the gate is applied.
     * @param controlQubit qubit that controls the gate application.
     * @param real flattened real parts of the full state vector.
     * @param imag flattened imaginary parts of the full state vector.
     * @param halfRows half dimension of the full state vector.
     * @param maReal real part of the component A of the unitary matrix.
     * @param maImag imaginary part of the component A of the unitary matrix.
     * @param mbReal real part of the component B of the unitary matrix.
     * @param mbImag imaginary part of the component B of the unitary matrix.
     * @param mcReal real part of the component C of the unitary matrix.
     * @param mcImag imaginary part of the component C of the unitary matrix.
     * @param mdReal real part of the component D of the unitary matrix.
     * @param mdImag imaginary part of the component D of the unitary matrix.
     */
    protected static void applyControlGate(int targetQubit, int controlQubit,
                                           float[] real, float[] imag, final int halfRows,
                                           float maReal, float maImag, float mbReal, float mbImag,
                                           float mcReal, float mcImag, float mdReal, float mdImag) {
        for (@Parallel int i = 0; i < halfRows; i++) {
            int maskRight = (1 << targetQubit) - 1;
            int maskLeft = ~maskRight;

            int a = (i & maskRight) | ((i & maskLeft) << 1);
            int b = a | (1 << targetQubit);

            if (((1 << controlQubit) & a) > 0) {
                float valueAReal = real[a];
                float valueAImag = imag[a];
                float valueBReal = real[b];
                float valueBImag = imag[b];

                real[a] = (valueAReal * maReal - valueAImag * maImag)
                        + (valueBReal * mbReal - valueBImag * mbImag);
                imag[a] = (valueAReal * maImag + valueAImag * maReal)
                        + (valueBReal * mbImag + valueBImag * mbReal);

                real[b] = (valueAReal * mcReal - valueAImag * mcImag)
                        + (valueBReal * mdReal - valueBImag * mdImag);
                imag[b] = (valueAReal * mcImag + valueAImag * mcReal)
                        + (valueBReal * mdImag + valueBImag * mdReal);
            }
        }
    }
}
