package tom.kepoziomke;

import tom.kepoziomke.app.AlpacaApp;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        AlpacaApp app = new AlpacaApp();
        app.start();
        app.addAlgorithm(new ExampleCryptoAlgorithm("AAVE/USD", 0.1, 1, 0.25, 0.5), 2, TimeUnit.MINUTES);
        Thread.sleep(1000 * 12 * 2);
        app.addAlgorithm(new ExampleCryptoAlgorithm("BTC/USD", 0.0001, 0.001, 0.25, 0), 2, TimeUnit.MINUTES);
        Thread.sleep(1000 * 12 * 2);
        app.addAlgorithm(new ExampleCryptoAlgorithm("DOGE/USD", 100, 1000, 0.25, 2), 2, TimeUnit.MINUTES);
        Thread.sleep(1000 * 12 * 2);
        app.addAlgorithm(new ExampleCryptoAlgorithm("ETH/USD", 0.002, 0.02, 0.25,0.2), 2, TimeUnit.MINUTES);
        Thread.sleep(1000 * 12 * 2);
        app.addAlgorithm(new ExampleCryptoAlgorithm("UNI/USD", 1, 10, 0.25,1), 2, TimeUnit.MINUTES);
    }
}
