package circuit;

public class Gate {
    private GateType type;
    private int[] connections;

    public Gate(GateType type, int[] connections) {
        this.type = type;
        this.connections = connections;
    }

    public GateType getType() {
        return type;
    }

    public int[] getConnections() {
        return connections;
    }
}
