package uk.ac.manchester.tornado.qsim.circuit.operation;

import uk.ac.manchester.tornado.qsim.circuit.Qubit;

public interface Operation {
    public Qubit[] involvedQubits();
}
