package tom.kepoziomke;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException  {

        AlpacaApp app = new AlpacaApp();
        app.addAlgorithm(new SillyCryptoAlgorithm(List.of("BTC/USD", "ETH/USD", "DOGE/USD")), 30, TimeUnit.SECONDS);
        app.start();
        Thread.sleep(1000 * 60 * 60);
        app.stop();
    }
}
