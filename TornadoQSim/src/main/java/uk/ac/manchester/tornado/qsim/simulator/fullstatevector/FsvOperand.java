/*
 * This file is part of TornadoQSim:
 * A Java-based quantum computing framework accelerated with TornadoVM.
 *
 * URL: https://github.com/beehive-lab/TornadoQSim
 *
 * Copyright (c) 2021-2022, APT Group, Department of Computer Science,
 * The University of Manchester. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.manchester.tornado.qsim.simulator.fullstatevector;

import uk.ac.manchester.tornado.api.annotations.Parallel;

/**
 * Provides operands used in the full state vector simulator that can be
 * accelerated with TornadoVM.
 * 
 * @author Ales Kubicek
 */
class FsvOperand {

    /**
     * Performs application of the standard quantum gate, which is supplied as
     * decomposed 2x2 unitary matrix. The form of the matrix is [[A,B],[C,D]].
     * 
     * @param targetQubit
     *            qubit to which the gate is applied.
     * @param real
     *            flattened real parts of the full state vector.
     * @param imag
     *            flattened imaginary parts of the full state vector.
     * @param halfRows
     *            half dimension of the full state vector.
     * @param gateReal
     *            real part of the components A, B, C and D of the unitary matrix.
     * @param gateImag
     *            imaginary part of the components A, B, C and D of the unitary
     *            matrix.
     */

    protected static void applyGate(int[] targetQubit, float[] real, float[] imag, final int halfRows, float[] gateReal, float[] gateImag) {
        for (@Parallel int i = 0; i < halfRows; i++) {
            int maskRight = (1 << targetQubit[0]) - 1;
            int maskLeft = ~maskRight;

            int a = (i & maskRight) | ((i & maskLeft) << 1);
            int b = a | (1 << targetQubit[0]);

            float valueAReal = real[a];
            float valueAImag = imag[a];
            float valueBReal = real[b];
            float valueBImag = imag[b];

            real[a] = (valueAReal * gateReal[0] - valueAImag * gateImag[0]) + (valueBReal * gateReal[1] - valueBImag * gateImag[1]);
            imag[a] = (valueAReal * gateImag[0] + valueAImag * gateReal[0]) + (valueBReal * gateImag[1] + valueBImag * gateReal[1]);

            real[b] = (valueAReal * gateReal[2] - valueAImag * gateImag[2]) + (valueBReal * gateReal[3] - valueBImag * gateImag[3]);
            imag[b] = (valueAReal * gateImag[2] + valueAImag * gateReal[2]) + (valueBReal * gateImag[3] + valueBImag * gateReal[3]);
        }
    }

    /**
     * Performs application of the standard quantum gate, which is supplied as
     * decomposed 2x2 unitary matrix. The form of the matrix is [[A,B],[C,D]].
     * 
     * @param targetQubit
     *            qubit to which the gate is applied.
     * @param controlQubit
     *            qubit that controls the gate application.
     * @param real
     *            flattened real parts of the full state vector.
     * @param imag
     *            flattened imaginary parts of the full state vector.
     * @param halfRows
     *            half dimension of the full state vector.
     * @param gateReal
     *            real part of the components A, B, C and D of the unitary matrix.
     * @param gateImag
     *            imaginary part of the components A, B, C and D of the unitary
     *            matrix.
     */
    protected static void applyControlGate(int[] targetQubit, int[] controlQubit, float[] real, float[] imag, final int halfRows, float[] gateReal, float[] gateImag) {
        for (@Parallel int i = 0; i < halfRows; i++) {
            int maskRight = (1 << targetQubit[0]) - 1;
            int maskLeft = ~maskRight;

            int a = (i & maskRight) | ((i & maskLeft) << 1);
            int b = a | (1 << targetQubit[0]);

            if (((1 << controlQubit[0]) & a) > 0) {
                float valueAReal = real[a];
                float valueAImag = imag[a];
                float valueBReal = real[b];
                float valueBImag = imag[b];

                real[a] = (valueAReal * gateReal[0] - valueAImag * gateImag[0]) + (valueBReal * gateReal[1] - valueBImag * gateImag[1]);
                imag[a] = (valueAReal * gateImag[0] + valueAImag * gateReal[0]) + (valueBReal * gateImag[1] + valueBImag * gateReal[1]);

                real[b] = (valueAReal * gateReal[2] - valueAImag * gateImag[2]) + (valueBReal * gateReal[3] - valueBImag * gateImag[3]);
                imag[b] = (valueAReal * gateImag[2] + valueAImag * gateReal[2]) + (valueBReal * gateImag[3] + valueBImag * gateReal[3]);
            }
        }
    }
}
