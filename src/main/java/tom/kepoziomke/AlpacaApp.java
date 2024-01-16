package tom.kepoziomke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tom.kepoziomke.algorithms.AlpacaAlgorithm;

public class AlpacaApp {

    private final Logger logger = LoggerFactory.getLogger(AlpacaApp.class);
    private final AlpacaConnector connector;
    private AlpacaAlgorithm algorithm;
    public AlpacaApp() {
        connector = new AlpacaConnector();
        algorithm = null;
    }

    public void setAlgorithm(AlpacaAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void start() {
        if (algorithm != null) {
            Thread thread = new Thread(() -> {
               algorithm.run(connector);
            });
            try {
                thread.start();
                thread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                System.exit(-1);
            }
        }
    }
}
