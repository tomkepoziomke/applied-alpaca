package tom.kepoziomke.algorithm;

import tom.kepoziomke.connector.AlpacaReadOnlyConnector;

public interface AlpacaAlgorithm {
    AlgorithmResult run(AlpacaReadOnlyConnector connector);
}
