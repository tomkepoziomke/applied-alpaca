package tom.kepoziomke.algorithms;

import org.apache.commons.math3.distribution.WeibullDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tom.kepoziomke.AlpacaConnector;

public class SillyAlgorithm extends AlpacaAlgorithm {
    private final Logger logger = LoggerFactory.getLogger(SillyAlgorithm.class);
    private final WeibullDistribution distribution;
    double weibullShape;
    double weibullScale;

    public SillyAlgorithm(double shape, double scale) {
        this.weibullScale = scale;
        this.weibullShape = shape;
        distribution = new WeibullDistribution(weibullShape, weibullScale);
    }

    @Override
    public void run(AlpacaConnector connector) {
        var positions = connector.getPositionsEndpoint();
        if (positions.isPresent()) {

            while (true) {
                try {
                    int sample = (int)(distribution.sample() * 1000);
                    Thread.sleep(sample);
                }
                catch (InterruptedException e) {
                    logger.error(e.getMessage());
                    System.exit(-1);
                }
                catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }
}
