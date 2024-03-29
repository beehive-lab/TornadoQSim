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

package uk.ac.manchester.tornado.qsim.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ComplexTest {

    @Test
    public void testComplexStructure() {
        Complex a = new Complex(0, 0);
        assertEquals(0, a.real());
        assertEquals(0, a.imag());

        a = new Complex(-5, 5);
        assertEquals(-5, a.real());
        assertEquals(5, a.imag());

        float real = 5.12548f;
        float imag = 0.44587f;
        a = new Complex(real, imag);
        assertEquals(real, a.real());
        assertEquals(imag, a.imag());
    }

    @Test
    public void testComplexArithmetics() {
        Complex a = new Complex(-4.5f, 8);
        Complex b = new Complex(10.5f, -4);

        assertEquals(new Complex(6, 4), a.plus(b));
        assertEquals(new Complex(6, 4), b.plus(a));

        assertEquals(new Complex(-15, 12), a.minus(b));
        assertEquals(new Complex(15, -12), b.minus(a));

        assertEquals(new Complex(-15.25f, 102), a.times(b));
        assertEquals(new Complex(-15.25f, 102), b.times(a));

        a = new Complex(20, 4);
        b = new Complex(10, 2);
        assertEquals(new Complex(2, 0), a.div(b));
        assertEquals(new Complex(0.5f, 0), b.div(a));

        a = new Complex(3, 4);
        assertEquals(new Complex(15, 20), a.scale(5));
        assertEquals(new Complex(3, -4), a.conjugate());
        assertEquals(new Complex(-13.128783f, -15.200785f), a.exp());
        assertEquals(5, a.abs());
    }

    @Test
    public void testComplexEquality() {
        Complex a = new Complex(-4.5f, 8);
        Complex b = new Complex(-4.5f, 8);
        assertEquals(a, b);

        a = new Complex(0, 8);
        b = new Complex(0, 8);
        assertEquals(a, b);

        a = new Complex(4.5f, 0);
        b = new Complex(4.5f, 0);
        assertEquals(a, b);

        Complex c = new Complex(-4.5f, 0);
        assertNotEquals(a, c);
    }

    @Test
    public void testComplexString() {
        assertEquals(new Complex(-4.5f, 0).toString(), "-4.500 + 0.000i");
        assertEquals(new Complex(0, 4.5f).toString(), "0.000 + 4.500i");
        assertEquals(new Complex(0, -4.5f).toString(), "0.000 - 4.500i");
        assertEquals(new Complex(8, -4.5f).toString(), "8.000 - 4.500i");
        assertEquals(new Complex(8, 4.5f).toString(), "8.000 + 4.500i");
    }

}
