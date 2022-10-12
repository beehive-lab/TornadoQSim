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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StateConverterTest {

    @Test
    public void testStateToBitstring() {
        assertEquals("0", StateConverter.stateToBitstring(0));
        assertEquals("10", StateConverter.stateToBitstring(2));
        assertEquals("11110000", StateConverter.stateToBitstring(240));

        assertEquals("000", StateConverter.stateToBitstring(0, 3));
        assertEquals("00010", StateConverter.stateToBitstring(2, 5));
        assertEquals("0000000011110000", StateConverter.stateToBitstring(240, 16));
    }

    @Test
    public void testBitstringToState() {
        assertEquals(0, StateConverter.stateFromBitstring("0"));
        assertEquals(2, StateConverter.stateFromBitstring("010"));
        assertEquals(240, StateConverter.stateFromBitstring("11110000"));

        assertEquals(0, StateConverter.stateFromBitstring("000"));
        assertEquals(2, StateConverter.stateFromBitstring("00010"));
        assertEquals(240, StateConverter.stateFromBitstring("0000000011110000"));
    }

}
