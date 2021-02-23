package uk.ac.manchester.tornado.qsim.circuit;

public class TestQubitFactory {

    private static TestQubitFactory instance;

    private TestQubitFactory() { }

    public static TestQubitFactory getInstance(){
        if(instance == null)
            instance = new TestQubitFactory();
        return instance;
    }

    public Qubit createQubit(int id) { return new Qubit(id); }

    public Qubit[] createQubits(int... ids) {
        Qubit[] qubits = new Qubit[ids.length];
        for (int i = 0; i < ids.length; i++)
            qubits[i] = new Qubit(ids[i]);
        return qubits;
    }
}
