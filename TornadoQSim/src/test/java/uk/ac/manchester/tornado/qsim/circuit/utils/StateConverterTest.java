package uk.ac.manchester.tornado.qsim.circuit.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StateConverterTest {

    @Test
    public void testStateToBitstring() {
        assertEquals("0", StateConverter.stateToBitstring(0));
        assertEquals("10", StateConverter.stateToBitstring(2));
        assertEquals("11110000", StateConverter.stateToBitstring(240));

        assertEquals("000", StateConverter.stateToBitstring(0,3));
        assertEquals("00010", StateConverter.stateToBitstring(2,5));
        assertEquals("0000000011110000", StateConverter.stateToBitstring(240,16));
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
