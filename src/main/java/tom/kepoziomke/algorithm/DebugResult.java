package tom.kepoziomke.algorithm;

public class DebugResult extends AlgorithmResult {
    public final int result;

    public DebugResult(int result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Debug result: " + this.result;
    }
}
