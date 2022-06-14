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
package uk.ac.manchester.tornado.qsim.circuit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.ac.manchester.tornado.qsim.math.Complex;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;

import static org.junit.jupiter.api.Assertions.*;

public class StateTest {

    private static Complex ONE,ZERO,HALF;
    private static ComplexTensor stateVectorA,stateVectorB,stateVectorC,stateVectorD;

    @BeforeAll
    public static void prepareStandardAmplitudes() {
        ONE = new Complex(1, 0);
        ZERO = new Complex(0, 0);
        HALF = new Complex((float) (1 / Math.sqrt(2)), 0);

        stateVectorA = new ComplexTensor(8);
        stateVectorA.insertElement(ONE, 0);

        stateVectorB = new ComplexTensor(8);
        stateVectorB.insertElement(ONE, 7);

        stateVectorC = new ComplexTensor(8);
        stateVectorC.insertElement(HALF, 1);
        stateVectorC.insertElement(HALF, 2);

        stateVectorD = new ComplexTensor(8);
        stateVectorD.insertElement(HALF, 0);
        stateVectorD.insertElement(HALF, 7);
    }

    @Test
    public void testStateDefinition() {
        State qState = new State(5);
        assertEquals(1, qState.getStateVector().rank());
        assertEquals(ONE, qState.getStateVector().getElement(0));
        assertEquals(32, qState.size());

        // Normalized
        assertTrue(qState.isNormalized());

        // Not normalized - greater
        qState.getStateVector().insertElement(HALF, 1);
        assertFalse(qState.isNormalized());

        // Not normalized - less
        qState.getStateVector().insertElement(HALF, 0);
        qState.getStateVector().insertElement(ZERO, 1);
        assertFalse(qState.isNormalized());

        // Normalized
        qState.getStateVector().insertElement(HALF, 1);
        assertTrue(qState.isNormalized());

        assertThrows(IllegalArgumentException.class, () -> new State(-5));
        assertThrows(IllegalArgumentException.class, () -> new State(0));
    }

    @Test
    public void testStateDefinitionInitialVector() {
        ComplexTensor normalizedValid = getNormalizedVector(16);
        State qState = new State(normalizedValid);
        assertEquals(1, qState.getStateVector().rank());
        assertEquals(ONE, qState.getStateVector().getElement(0));
        assertEquals(16, qState.size());
        assertTrue(qState.isNormalized());

        ComplexTensor invalid = null;
        ComplexTensor normalizedInvalid = getNormalizedVector(5);
        ComplexTensor notNormalized = new ComplexTensor(4);

        assertThrows(IllegalArgumentException.class, () -> new State(invalid));
        assertThrows(IllegalArgumentException.class, () -> new State(notNormalized));
        assertThrows(IllegalArgumentException.class, () -> new State(normalizedInvalid));
    }

    @Test
    public void testStateVectorUpdate() {
        State qState = new State(getNormalizedVector(16));

        ComplexTensor valid = new ComplexTensor(16);
        valid.insertElement(HALF, 0);
        valid.insertElement(HALF, 1);

        qState.setStateVector(valid);
        assertEquals(valid, qState.getStateVector());

        ComplexTensor invalid = null;
        ComplexTensor normalizedInvalid = getNormalizedVector(5);
        ComplexTensor notNormalized = new ComplexTensor(4);

        assertThrows(IllegalArgumentException.class, () -> qState.setStateVector(invalid));
        assertThrows(IllegalArgumentException.class, () -> qState.setStateVector(normalizedInvalid));
        assertThrows(IllegalArgumentException.class, () -> qState.setStateVector(notNormalized));
    }

    @Test
    public void testStateQubitQueries() {
        State qState = new State(3);

        // State vector test A
        qState = new State(stateVectorA);
        assertTrue(qState.isNormalized());
        assertEquals(0.0, qState.getQubitProbability(0));
        assertEquals(0.0, qState.getQubitProbability(1));
        assertEquals(0.0, qState.getQubitProbability(2));

        // State vector test B
        qState = new State(stateVectorB);
        assertTrue(qState.isNormalized());
        assertEquals(1.0, qState.getQubitProbability(0));
        assertEquals(1.0, qState.getQubitProbability(1));
        assertEquals(1.0, qState.getQubitProbability(2));

        // State vector test C
        qState = new State(stateVectorC);
        assertTrue(qState.isNormalized());
        assertTrue(almostEqual(0.5f, qState.getQubitProbability(0)));
        assertTrue(almostEqual(0.5f, qState.getQubitProbability(1)));
        assertTrue(almostEqual(0.0f, qState.getQubitProbability(2)));

        // State vector test D
        qState = new State(stateVectorD);
        assertTrue(qState.isNormalized());
        assertTrue(almostEqual(0.5f, qState.getQubitProbability(0)));
        assertTrue(almostEqual(0.5f, qState.getQubitProbability(1)));
        assertTrue(almostEqual(0.5f, qState.getQubitProbability(2)));

        // Random float numbers: 0.722, 0.734, 0.194
        // => quaranteed by seed = 100
        qState.setSeed(100);
        assertEquals(0, qState.getQubitCollapsed(0));
        assertEquals(0, qState.getQubitCollapsed(1));
        assertEquals(1, qState.getQubitCollapsed(2));

        State finalQState = qState;
        assertThrows(IllegalArgumentException.class, () -> finalQState.getQubitProbability(-5));
        assertThrows(IllegalArgumentException.class, () -> finalQState.getQubitProbability(5));
    }

    @Test
    public void testStateQueries() {
        State qState = new State(3);

        // State vector test A
        qState = new State(stateVectorA);
        assertTrue(qState.isNormalized());
        assertEquals(ONE, qState.getStateAmplitude(0));
        assertEquals(1.0, qState.getStateProbability(0));
        for (int state = 1; state < qState.size(); state++) {
            assertEquals(ZERO, qState.getStateAmplitude(state));
            assertEquals(0.0, qState.getStateProbability(state));
        }

        // State vector test B
        qState = new State(stateVectorB);
        assertTrue(qState.isNormalized());
        assertEquals(ZERO, qState.getStateAmplitude(0));
        assertEquals(0.0, qState.getStateProbability(0));
        assertEquals(ONE, qState.getStateAmplitude(7));
        assertEquals(1.0, qState.getStateProbability(7));

        // State vector test C
        qState = new State(stateVectorC);
        assertTrue(qState.isNormalized());
        assertEquals(HALF, qState.getStateAmplitude(1));
        assertTrue(almostEqual(0.5f, qState.getStateProbability(1)));
        assertEquals(HALF, qState.getStateAmplitude(2));
        assertTrue(almostEqual(0.5f, qState.getStateProbability(2)));

        // State vector test D
        qState = new State(stateVectorD);
        assertTrue(qState.isNormalized());
        assertEquals(HALF, qState.getStateAmplitude(0));
        assertTrue(almostEqual(0.5f, qState.getStateProbability(0)));
        assertEquals(HALF, qState.getStateAmplitude(7));
        assertTrue(almostEqual(0.5f, qState.getStateProbability(7)));

        State finalQState = qState;
        assertThrows(IllegalArgumentException.class, () -> finalQState.getStateAmplitude(-5));
        assertThrows(IllegalArgumentException.class, () -> finalQState.getStateAmplitude(8));
        assertThrows(IllegalArgumentException.class, () -> finalQState.getStateProbability(-5));
        assertThrows(IllegalArgumentException.class, () -> finalQState.getStateProbability(8));
    }

    @Test
    public void testStateCollapse() {
        State qState = new State(3);

        // Random float numbers: 0.722
        // => quaranteed by seed = 100

        // State vector test A
        qState = new State(stateVectorA);
        qState.setSeed(100);
        assertTrue(qState.isNormalized());
        assertEquals(0, qState.collapse());

        // State vector test B
        qState = new State(stateVectorB);
        qState.setSeed(100);
        assertTrue(qState.isNormalized());
        assertEquals(7, qState.collapse());

        // State vector test C
        qState = new State(stateVectorC);
        qState.setSeed(100);
        assertTrue(qState.isNormalized());
        assertEquals(2, qState.collapse());

        // State vector test D
        qState = new State(stateVectorD);
        qState.setSeed(100);
        assertTrue(qState.isNormalized());
        assertEquals(7, qState.collapse());
    }

    @Test
    public void testStateEquality() {
        State a = new State(3);
        ComplexTensor stateVector = a.getStateVector();
        stateVector.insertElement(HALF, 0);
        stateVector.insertElement(HALF, 7);

        State b = new State(3);
        stateVector = b.getStateVector();
        stateVector.insertElement(HALF, 0);
        stateVector.insertElement(HALF, 7);

        State c = new State(3);
        stateVector = c.getStateVector();
        stateVector.insertElement(HALF, 0);
        stateVector.insertElement(HALF, 6);

        State d = new State(4);
        stateVector = c.getStateVector();
        stateVector.insertElement(HALF, 0);
        stateVector.insertElement(HALF, 6);

        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(c, d);
    }

    private ComplexTensor getNormalizedVector(int size) {
        ComplexTensor vector = new ComplexTensor(size);
        vector.insertElement(new Complex(1, 0), 0);
        return vector;
    }

    private boolean almostEqual(float expected, float actual) {
        return Math.abs(expected - actual) < 0.01;
    }

}
