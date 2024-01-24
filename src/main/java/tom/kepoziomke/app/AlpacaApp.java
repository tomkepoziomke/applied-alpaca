package tom.kepoziomke.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tom.kepoziomke.algorithm.Algorithm;
import tom.kepoziomke.algorithm.EmptyResult;
import tom.kepoziomke.connector.Connector;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main wrapper around the API. Responsible for automatic algorithm handling.
 */
public class AlpacaApp {


    /**
     * Class representing the status of an algorithm.
     */
    private static class AlgorithmAsyncStatus {
        public final int period;
        public final TimeUnit unit;
        private Future<?> future;


        /**
         * Constructor.
         * @param period Period of the algorithm, or how many units of time will pass between each iteration of the algorithm.
         * @param unit Unit of time.
         */
        private AlgorithmAsyncStatus(int period, TimeUnit unit) {
            this.period = period;
            this.unit = unit;
            this.future = CompletableFuture.completedFuture(null);
        }


        /**
         * Creates a new
         * @param period Period of the algorithm, or how many units of time will pass between each iteration of the algorithm.
         * @param unit Unit of time.
         * @return New algorithm status with given period and time unit.
         */
        public static AlgorithmAsyncStatus periodic(int period, TimeUnit unit) {
            if (period < 0)
                period = 0;
            return new AlgorithmAsyncStatus(period, unit);
        }

        /**
         * Returns algorithm's future object.
         * @return Future object.
         */
        public Future<?> getFuture() {
            return future;
        }

        /**
         * Sets future of the algorithm.
         * @param future The future.
         */
        public void setFuture(Future<?> future) {
            this.future = future;
        }
    }

    private static final Integer ALGORITHM_THREADS = 10;
    public static final Integer DEFAULT_TIMEOUT_DURATION = 1000;
    public static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

    private final Connector connector;
    private final ConcurrentHashMap<Algorithm, AlgorithmAsyncStatus> algorithms;
    private final OutputHandler outputHandler;
    private final Logger logger;
    private final ScheduledExecutorService threadPool;
    private final AtomicBoolean running;

    /**
     * Default constructor.
     */
    public AlpacaApp() {
        logger              = LoggerFactory.getLogger(AlpacaApp.class);
        threadPool          = new ScheduledThreadPoolExecutor(ALGORITHM_THREADS);
        connector           = new Connector();

        algorithms          = new ConcurrentHashMap<>();
        outputHandler       = new BasicOutputHandler(connector);
        running             = new AtomicBoolean(false);
    }

    /**
     * Adds a new algorithm to the pool of algorithms. If the application is already running, the method also launches it.
     * @param algorithm The algorithm to be added.
     * @param period The period of the algorithm.
     * @param unit The unit of time of the algorithm.
     */
    public void addAlgorithm(Algorithm algorithm, Integer period, TimeUnit unit) {
        var contains = algorithms.get(algorithm);
        if (contains != null)
            return;
        AlgorithmAsyncStatus algorithmStatus = AlgorithmAsyncStatus.periodic(period, unit);
        algorithms.put(algorithm, algorithmStatus);
        if (running.get())
            startAlgorithm(algorithm);
    }

    /**
     * Starts the algorithm.
     * @param algorithm The algorithm to be started.
     */
    private void startAlgorithm(Algorithm algorithm) {
        var algorithmStatus = algorithms.get(algorithm);
        if (algorithmStatus == null)
            return;
        if (algorithmStatus.getFuture().isDone()) {
            algorithmStatus.setFuture(runAlgorithmPeriodically(algorithm, algorithmStatus.period, algorithmStatus.unit));
        }
    }

    /**
     * Queries the algorithm for next response, then passes its result to the output handler.
     * @param algorithm The algorithm to be queried.
     */
    private void retrieveAndProcessAlgorithmResult(Algorithm algorithm) {
        var result = algorithm.run(connector.read());
        if (!(result instanceof EmptyResult))
            outputHandler.output(result);
    }

    /**
     * Prompts the executor with the algorithm, which is to be launched periodically.
     * @param algorithm The algorithm to be launched periodically.
     * @param period The period of the algorithm.
     * @param unit The unit of time of the period.
     * @return The future of this asynchronous task.
     */
    private Future<?> runAlgorithmPeriodically(Algorithm algorithm, Integer period, TimeUnit unit) {
        return threadPool.scheduleWithFixedDelay(() -> retrieveAndProcessAlgorithmResult(algorithm), 0, period, unit);
    }

    /**
     * Starts the application.
     */
    public void start() {
        if (!running.get()) {
            for (Algorithm algorithm : algorithms.keySet()) {
                startAlgorithm(algorithm);
            }
        }
        running.set(true);
    }

    /**
     * Halts the application.
     */
    public void stop() {
        running.set(false);
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(DEFAULT_TIMEOUT_DURATION, DEFAULT_TIMEOUT_UNIT);
        }
        catch (InterruptedException e) {
            logger.error("Interruption during termination", e);
        }

    }
}
