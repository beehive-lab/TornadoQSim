package circuit;

import java.util.ArrayList;
import java.util.List;

public class Circuit {
    private int noQubits;
    private List<Step> steps;

    public Circuit(List<Step> steps, int noQubits) {
        this.noQubits = noQubits;
        this.steps = steps;
    }

    public Circuit() {
        this.noQubits = 5;
        this.steps = new ArrayList<Step>();
    }

    public void setNoQubits(int noQubits) {
        this.noQubits = noQubits;
    }

    public int getNoQubits() {
        return noQubits;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Step> getSteps() {
        return steps;
    }
}
