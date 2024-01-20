package tom.kepoziomke.algorithm;

import tom.kepoziomke.connector.AlpacaReadOnlyConnector;

public interface Algorithm {
    AlgorithmResult run(AlpacaReadOnlyConnector connector);
}
