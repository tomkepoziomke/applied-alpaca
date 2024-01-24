package tom.kepoziomke.algorithm;

import tom.kepoziomke.connector.ReadOnlyConnector;

import java.util.List;

/**
 * The interface for a generic algorithm.
 */
public interface Algorithm {
    /**
     * Runs an algorithm once.
     * @param connector The read-only connector to be queried by the algorithm.
     * @return List of algorithm results.
     */
    List<AlgorithmResult> run(ReadOnlyConnector connector);
}
