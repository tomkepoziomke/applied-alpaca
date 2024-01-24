package tom.kepoziomke.app;

import tom.kepoziomke.algorithm.AlgorithmResult;

import java.util.List;

/**
 * The interface representing an object processing list of results from algorithms. Deals with querying the API object
 * and handles errors.
 */
public interface OutputHandler {
    /**
     * Processes single batch of algorithm results.
     * @param results The list of algorithm results.
     */
    void output(List<AlgorithmResult> results);
}
