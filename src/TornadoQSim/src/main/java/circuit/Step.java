package circuit;

import java.util.List;

public class Step {
    private List<Gate> gates;

    public Step(List<Gate> gates) {
        this.gates = gates;
    }

    public void setGates(List<Gate> gates) {
        this.gates = gates;
    }

    public List<Gate> getGates() {
        return gates;
    }

    public boolean addGate(Gate gate) {
        return gates.add(gate);
    }
}
