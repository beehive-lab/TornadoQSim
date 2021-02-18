package uk.ac.manchester.tornado.qsim.circuit.operation;

import uk.ac.manchester.tornado.qsim.circuit.Qubit;

public class CustomGate implements Operation {
    @Override
    public Qubit[] involvedQubits() {
        return null;
    }
}
