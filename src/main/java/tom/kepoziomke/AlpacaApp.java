package tom.kepoziomke;

import tom.kepoziomke.algorithm.Algorithm;

import java.util.concurrent.TimeUnit;

public class AlpacaApp {

    private final AlpacaBackend backend;

    public AlpacaApp() {
        backend = new AlpacaBackend();
    }

    public void addAlgorithm(Algorithm algorithm) {
        backend.addAlgorithm(algorithm);
    }

    public void addAlgorithm(Algorithm algorithm, int period, TimeUnit unit) {
        backend.addAlgorithm(algorithm, period, unit);
    }

    public void removeAlgorithm(Algorithm algorithm) {
        backend.removeAlgorithm(algorithm);
    }

    public void start() {
        backend.start();
    }


    public void stop() {
        backend.stop();
    }
}
