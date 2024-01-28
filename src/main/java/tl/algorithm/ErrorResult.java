package tom.kepoziomke.algorithm;

/**
 * Represents an empty result (usually due to an error).
 */
public class EmptyResult implements AlgorithmResult {
    /**
     * String representation of the result.
     * @return String representation.
     */
    @Override
    public String toString() {
        return "EmptyResult";
    }
}
