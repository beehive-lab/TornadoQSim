package uk.ac.manchester.tornado.qsim.circuit;

import java.util.Objects;

public class Qubit {
    private final int id;

    protected Qubit(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Qubit qubit = (Qubit) o;
        return id == qubit.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Qubit " + id;
    }
}
