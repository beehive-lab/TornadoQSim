package uk.ac.manchester.tornado.qsim.circuit;

import uk.ac.manchester.tornado.qsim.math.Complex;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;

import java.util.Objects;
import java.util.Random;

/**
 * Represents a state of a quantum system (exponential size in terms of number of qubits). The state can be queried
 * to retrive information for particular qubit, particular state or to obtain the whole state vector. Information
 * about the collapsed state of the whole system or single qubit can also be obtained.
 * @author Ales Kubicek
 */
public class State {
    private final ComplexTensor stateVector;
    private final int noQubits;
    private final Random random;

    /**
     * Constructs a quantum state of 2^noQubits amplitudes.
     * @param noQubits number of qubits.
     */
    public State(int noQubits) {
        if (noQubits < 1)
            throw new IllegalArgumentException("Number of qubits in a state must be greater than 0.");
        this.noQubits = noQubits;
        stateVector = new ComplexTensor((int)Math.pow(2, noQubits));
        stateVector.insertElement(new Complex(1,0),0);
        random = new Random();
    }

    /**
     * Constructs a quantum state based on supplied initial state vector (must be normalized).
     * @param initialStateVector normalized state vector.
     */
    public State(ComplexTensor initialStateVector) {
        if (!isValidInitialStateVector(initialStateVector))
            throw new IllegalArgumentException("Invalid supplied state vector (NULL / not a vector / size).");
        stateVector = initialStateVector;
        noQubits = getQubitCount(initialStateVector.size());
        random = new Random();
        if (!isNormalized())
            throw new IllegalArgumentException("Supplied state vector is not normalized.");
    }

    /**
     * Sets seed for the random generator used for collapsing states / qubits (Unit tests use only).
     * @param seed random generator seed.
     */
    protected void setSeed(long seed) { random.setSeed(seed); }

    /**
     * Gets the full state vector.
     * @return full state vector.
     */
    public ComplexTensor getStateVector() { return stateVector; }

    /**
     * Gets the size (number of amplitudes) of the state vector.
     * @return state vector size.
     */
    public int size() { return stateVector.size(); }

    /**
     * Checks whether the state vector is normalized.
     * @return true, if the state vector is normalized.
     */
    public boolean isNormalized() {
        float sum = 0;
        for (int i = 0; i < stateVector.size(); i++)
            sum += getProbabilityForState(i);
        // Tolerate some precision loss
        return sum > 0.99f && sum < 1.01f;
    }

    /**
     * Collapses the supplied qubit to the state 0 or 1.
     * @param qubit qubit to be collapsed.
     * @return state to which the qubit collapsed (0 or 1).
     */
    public int getQubitCollapsed(int qubit) {
        return random.nextFloat() < getQubitProbability(qubit) ? 1 : 0;
    }

    /**
     * Gets the probability of collapsing to the state 1.
     * @param qubit qubit for which to retrieve the probability.
     * @return probability value (0.0 - 1.0).
     */
    public float getQubitProbability(int qubit) {
        if (!isValidQubit(qubit))
            throw new IllegalArgumentException("Invalid qubit supplied.");
        float probability = 0;
        for (int state = 0; state < stateVector.size(); state++)
            if (isBitSet(state, qubit))
                probability += getProbabilityForState(state);
        return probability;
    }

    /**
     * Gets the aplitude (complex number) of the supplied state (eg. state '0010' → 2).
     * @param state single quantum state of the state vector.
     * @return complex amplitude of the state.
     */
    public Complex getStateAmplitude(int state) {
        if (!isValidState(state))
            throw new IllegalArgumentException("Invalid state supplied");
        return stateVector.getElement(state);
    }

    /**
     * Gets the probability of the quantum system to collapse to the supplied state (eg. state '0010' → 2).
     * @param state single quantum state of the state vector.
     * @return probability value (0.0 - 1.0).
     */
    public float getStateProbability(int state) {
        if (!isValidState(state))
            throw new IllegalArgumentException("Invalid state supplied");
        return getProbabilityForState(state);
    }

    /**
     * Gets the collapsed state of the whole quantum system.
     * @return collapsed quantum state (eg. 2 → '0010');
     */
    public int collapse() {
        float randomNumber = random.nextFloat();
        int collapsedState = 0;
        float totalWeight = 0;
        for (int state = 0; state < stateVector.size(); state++) {
            totalWeight += getProbabilityForState(state);
            if (randomNumber < totalWeight) {
                collapsedState = state;
                break;
            }
        }
        return collapsedState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return noQubits == state.noQubits && stateVector.equals(state.stateVector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateVector, noQubits);
    }

    private boolean isBitSet(int number, int bit) {
        return (number & (1 << bit)) != 0;
    }

    private float getProbabilityForState(int state) {
        return stateVector.getElement(state).abs() * stateVector.getElement(state).abs();
    }

    private int getQubitCount(int noStates) {
        int exponent = 0;
        while ((noStates = noStates >> 1) != 0)
            exponent++;
        return exponent;
    }

    private boolean isValidState(int state) {
        return state >= 0 && state < stateVector.size();
    }

    private boolean isValidQubit(int qubit) {
        return qubit >= 0 && qubit < noQubits;
    }

    private boolean isValidInitialStateVector(ComplexTensor vector) {
        return vector != null
                && vector.rank() == 1
                && vector.size() >= 2
                && (vector.size() & (vector.size() - 1)) == 0;
    }
}
