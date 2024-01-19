package tom.kepoziomke.algorithm;

import org.apache.commons.math3.distribution.WeibullDistribution;

import tom.kepoziomke.connector.AlpacaReadOnlyConnector;

import java.util.List;
import java.util.Random;

public class DebugAlgorithm implements AlpacaAlgorithm {

    public DebugAlgorithm() {
    }

    @Override
    public AlgorithmResult run(AlpacaReadOnlyConnector connector) {
        Random rnd = new Random();
        return new DebugResult(rnd.nextInt(10, 100));
    }
}
