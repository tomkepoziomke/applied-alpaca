package tom.kepoziomke;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException  {

        AlpacaApp app = new AlpacaApp();
        app.start();
        app.addAlgorithm(new SillyCryptoAlgorithm(List.of("BTC/USD", "ETH/USD")), 60, TimeUnit.SECONDS);
        Thread.sleep(30000);
        app.addAlgorithm(new SillyCryptoAlgorithm(List.of("DOGE/USD")), 60, TimeUnit.SECONDS);
        Thread.sleep(1000 * 60 * 60);
        app.stop();
    }
}
