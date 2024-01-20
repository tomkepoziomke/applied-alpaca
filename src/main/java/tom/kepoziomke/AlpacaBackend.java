package tom.kepoziomke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tom.kepoziomke.algorithm.AlgorithmResult;
import tom.kepoziomke.algorithm.Algorithm;
import tom.kepoziomke.algorithm.DebugResult;
import tom.kepoziomke.connector.AlpacaConnector;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AlpacaBackend {

    private static class AlgorithmAsyncStatus {
        public final boolean periodic;
        public final int period;
        public final TimeUnit unit;
        private Future<?> future;

        private AlgorithmAsyncStatus(boolean periodic, int period, TimeUnit unit) {
            this.periodic = periodic;
            this.period = period;
            this.unit = unit;
            this.future = CompletableFuture.completedFuture(null);
        }

        public static AlgorithmAsyncStatus nonPeriodic() {
            return new AlgorithmAsyncStatus(false, 0, null);
        }

        public static AlgorithmAsyncStatus periodic(int period, TimeUnit unit) {
            if (period < 0)
                period = 0;
            return new AlgorithmAsyncStatus(true, period, unit);
        }

        public Future<?> getFuture() {
            return future;
        }

        public void setFuture(Future<?> future) {
            this.future = future;
        }
    }

    private static final int ALGORITHM_THREADS = 10;
    private static final int OUTPUT_THREADS = 1;
    private static final int DEFAULT_TIMEOUT_DURATION = 1000;
    private static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

    private final AlpacaConnector connector;
    private final BlockingQueue<AlgorithmResult> results;
    private final ConcurrentHashMap<Algorithm, AlgorithmAsyncStatus> algorithms;
    private final Logger logger;
    private final ScheduledExecutorService algorithmExecutor;
    private final ScheduledExecutorService outputExecutor;
    private final AtomicBoolean running;

    public AlpacaBackend() {
        algorithmExecutor   = new ScheduledThreadPoolExecutor(ALGORITHM_THREADS);
        outputExecutor      = new ScheduledThreadPoolExecutor(OUTPUT_THREADS);
        results             = new LinkedBlockingQueue<>();
        algorithms          = new ConcurrentHashMap<>();
        logger              = LoggerFactory.getLogger(AlpacaBackend.class);
        connector           = new AlpacaConnector();
        running             = new AtomicBoolean(false);
    }

    public void addAlgorithm(Algorithm algorithm) {
        var contains = algorithms.get(algorithm);
        if (contains != null)
            return;
        AlgorithmAsyncStatus algorithmStatus = AlgorithmAsyncStatus.nonPeriodic();
        algorithms.put(algorithm, algorithmStatus);
        if (running.get())
            startAlgorithm(algorithm);
    }

    public void addAlgorithm(Algorithm algorithm, int period, TimeUnit unit) {
        var contains = algorithms.get(algorithm);
        if (contains != null)
            return;
        AlgorithmAsyncStatus algorithmStatus = AlgorithmAsyncStatus.periodic(period, unit);
        algorithms.put(algorithm, algorithmStatus);
        if (running.get())
            startAlgorithm(algorithm);
    }

    private void startAlgorithm(Algorithm algorithm) {
        var algorithmStatus = algorithms.get(algorithm);
        if (algorithmStatus == null)
            return;
        if (algorithmStatus.getFuture().isDone()) {
            if (algorithmStatus.periodic)
                algorithmStatus.setFuture(runAlgorithmPeriodically(algorithm, algorithmStatus.period, algorithmStatus.unit));
            else
                algorithmStatus.setFuture(runAlgorithmOnce(algorithm));
        }
    }

    private void stopAlgorithm(Algorithm algorithm) {
        var algorithmStatus = algorithms.get(algorithm);
        if (algorithmStatus == null)
            return;
        var future = algorithmStatus.getFuture();
        if (!future.isDone()) {
            future.cancel(false);
        }
    }

    public void removeAlgorithm(Algorithm algorithm) {
        var algorithmStatus = algorithms.get(algorithm);
        if (algorithmStatus == null)
            return;
        var future = algorithmStatus.getFuture();
        if (!future.isDone()) {
            future.cancel(false);
            algorithms.remove(algorithm);
        }
    }

    private void retrieveAlgorithmResult(Algorithm algorithm) {
        var result = algorithm.run(connector.read());
        results.add(result);
    }

    private Future<?> runAlgorithmPeriodically(Algorithm algorithm, int interval, TimeUnit unit) {
        return algorithmExecutor.scheduleWithFixedDelay(() -> retrieveAlgorithmResult(algorithm), 0, interval, unit);
    }

    private Future<?> runAlgorithmOnce(Algorithm algorithm) {
        return algorithmExecutor.submit(() -> retrieveAlgorithmResult(algorithm));
    }

    private void submitOutputOnce() {
        if (running.get()) {
            handleOutput(DEFAULT_TIMEOUT_DURATION, DEFAULT_TIMEOUT_UNIT);
            outputExecutor.execute(this::submitOutputOnce);
        }
    }

    private void handleOutput(int timeout, TimeUnit timeUnit) {
        try {
            AlgorithmResult res = results.poll(timeout, timeUnit);
            if (res == null)
                return;

            // do something with the result...
            synchronized (logger) {
                DebugResult cast = (DebugResult) res;
                logger.info(Integer.toString(cast.result));
            }
        }
        catch (InterruptedException e) {
            synchronized (logger) {
                logger.error("Error inside output handler", e);
            }
        }
    }

    public void start() {
        running.set(true);
        for (Algorithm algorithm : algorithms.keySet()) {
            startAlgorithm(algorithm);
        }
        outputExecutor.execute(this::submitOutputOnce);
    }

    public void stop() {
        running.set(false);
        outputExecutor.shutdown();
        algorithmExecutor.shutdown();
        try {
            outputExecutor.awaitTermination(DEFAULT_TIMEOUT_DURATION, DEFAULT_TIMEOUT_UNIT);
            algorithmExecutor.awaitTermination(DEFAULT_TIMEOUT_DURATION, DEFAULT_TIMEOUT_UNIT);
        }
        catch (InterruptedException e) {
            logger.error("Interrupted during stop", e);
        }

    }
}
