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
package uk.ac.manchester.tornado.qsim.circuit.utils;

/**
 * Provides converter methods to convert between state representation as number
 * and state representation as bitstring.
 * 
 * @author Ales Kubicek
 */
public class StateConverter {
    /**
     * Converts supplied bitstring state to integer state representation.
     * 
     * @param bitstring
     *            state representation as a bitstring.
     * @return state representation as an integer.
     */
    public static int stateFromBitstring(String bitstring) {
        return Integer.parseInt(bitstring, 2);
    }

    /**
     * Converts supplied integer state representation to bitstring state
     * representation. The bitstring is padded with 0s based on the number of qubits
     * (eg. 0 → '000').
     * 
     * @param state
     *            state representation as an integer.
     * @param noQubits
     *            number of qubits.
     * @return state representation as a bitstring.
     */
    public static String stateToBitstring(int state, int noQubits) {
        String format = "%" + noQubits + "s";
        return String.format(format, Integer.toBinaryString(state)).replace(" ", "0");
    }

    /**
     * Converts supplied integer state representation to bitstring state
     * representation. No padding with 0s (eg. 0 → '0' instead of '000').
     * 
     * @param state
     *            state representation as an integer.
     * @return state representation as a bitstring.
     */
    public static String stateToBitstring(int state) {
        return Integer.toBinaryString(state);
    }
}
