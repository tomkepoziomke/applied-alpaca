package tom.kepoziomke.algorithm;

import tom.kepoziomke.connector.ReadOnlyConnector;

public interface Algorithm {
    AlgorithmResult run(ReadOnlyConnector connector);
}
