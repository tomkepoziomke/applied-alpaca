package tom.kepoziomke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tom.kepoziomke.algorithm.AlgorithmResult;
import tom.kepoziomke.algorithm.AlpacaAlgorithm;
import tom.kepoziomke.connector.AlpacaConnector;

import java.util.concurrent.*;

public class AlpacaApp {

    private final class AlgorithmAsyncHandler implements Runnable {
        public void run() {
            var result = algorithm.run(connector.read());
            results.add(result);
        }
    }

    private final class OutputAsyncHandler implements Runnable {
        public void run() {
            try {
                AlgorithmResult res = results.take();
                synchronized (logger) {
                    logger.info(res.toString());
                }
            }
            catch (InterruptedException e) {
                synchronized (logger) {
                    logger.error("Error inside output handler", e);
                }
            }
        }
    }

    private final AlpacaConnector connector;
    private AlpacaAlgorithm algorithm;
    private ScheduledExecutorService executor;
    private BlockingQueue<AlgorithmResult> results;
    private final Logger logger;

    private static final int THREAD_POOL_SIZE = 10;

    public AlpacaApp() {
        connector = new AlpacaConnector();
        executor = new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE);
        logger = LoggerFactory.getLogger(AlpacaApp.class);
        results = new LinkedBlockingQueue<>();
        algorithm = null;
    }

    public void setAlgorithm(AlpacaAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void start() {
        executor.scheduleAtFixedRate(new AlgorithmAsyncHandler(), 0, 2, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(new AlgorithmAsyncHandler(), 0, 555, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(new OutputAsyncHandler(), 0, 100, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executor.shutdown();
    }
}
