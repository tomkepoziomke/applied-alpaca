package tom.kepoziomke;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        AlpacaApp app = new AlpacaApp();
        app.addAlgorithm(new LessSillyCryptoAlgorithm("DOGE/USD", 100), 60, TimeUnit.SECONDS);
        app.start();
    }
}
