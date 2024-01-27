package tom.kepoziomke;

import tom.kepoziomke.app.AlpacaApp;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        AlpacaApp app = new AlpacaApp();
        app.start();
        app.addAlgorithm(new ExampleCryptoAlgorithm(
                "AAVE/USD",
                0.1,
                1,
                0.25,
                0.5), 1, TimeUnit.MINUTES);
    }
}
