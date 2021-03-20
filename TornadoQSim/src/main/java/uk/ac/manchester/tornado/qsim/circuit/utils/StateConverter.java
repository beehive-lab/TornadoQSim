package uk.ac.manchester.tornado.qsim.circuit.utils;

/**
 * Provides converter methods to convert between state representation as number and state representation as bitstring.
 * @author Ales Kubicek
 */
public class StateConverter {
    /**
     * Converts supplied bitstring state to integer state representation.
     * @param bitstring state representation as a bitstring.
     * @return state representation as an integer.
     */
    public static int stateFromBitstring(String bitstring) {
        return Integer.parseInt(bitstring, 2);
    }

    /**
     * Converts supplied integer state representation to bitstring state representation.
     * The bitstring is padded with 0s based on the number of qubits (eg. 0 → '000').
     * @param state state representation as an integer.
     * @param noQubits number of qubits.
     * @return state representation as a bitstring.
     */
    public static String stateToBitstring(int state, int noQubits) {
        String format = "%" + noQubits + "s";
        return String.format(format, Integer.toBinaryString(state)).replace(" ", "0");
    }

    /**
     * Converts supplied integer state representation to bitstring state representation.
     * No padding with 0s (eg. 0 → '0' instead of '000').
     * @param state state representation as an integer.
     * @return state representation as a bitstring.
     */
    public static String stateToBitstring(int state) {
        return Integer.toBinaryString(state);
    }
}
