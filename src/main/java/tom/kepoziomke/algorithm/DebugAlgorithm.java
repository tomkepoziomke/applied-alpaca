package tom.kepoziomke.algorithm;

import tom.kepoziomke.connector.AlpacaReadOnlyConnector;

import java.util.Random;

public class DebugAlgorithm implements Algorithm {

    public DebugAlgorithm() {
    }

    @Override
    public AlgorithmResult run(AlpacaReadOnlyConnector connector) {
        Random rnd = new Random();
        return new DebugResult(rnd.nextInt(10, 20));
    }
}
