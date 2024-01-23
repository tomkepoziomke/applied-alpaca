package tom.kepoziomke;
import sun.misc.Signal;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public static void main(String[] args) {
        AlpacaApp app = new AlpacaApp();
        app.addAlgorithm(new SillyStockMarketAlgorithm(List.of("DO", "NAL", "DD", "TUSK")), 15, TimeUnit.SECONDS);
        app.addAlgorithm(new SillyCryptoAlgorithm(List.of("DOGE/USD", "ETH/USD")), 10, TimeUnit.SECONDS);
        app.start();
    }
}
