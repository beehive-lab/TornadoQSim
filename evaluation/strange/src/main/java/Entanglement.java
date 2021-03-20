import org.redfx.strange.Program;
import org.redfx.strange.Step;
import org.redfx.strange.gate.Cnot;
import org.redfx.strange.gate.Hadamard;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;

public class Entanglement {

    public static void main(String[] args) {
        int noQubits = Common.getQubitCount(args);

        Program program = new Program(noQubits);

        Step step1 = new Step();
        step1.addGate(new Hadamard(0));
        program.addStep(step1);

        for (int target = noQubits - 1; target > 0; target--) {
            Step step = new Step();
            step.addGate(new Cnot(0, target));
            program.addStep(step);
        }

        SimpleQuantumExecutionEnvironment environment = new SimpleQuantumExecutionEnvironment();

        Common.simulateAndPrint(environment, program);
    }
}
